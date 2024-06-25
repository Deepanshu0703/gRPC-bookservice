package com.example.bookstore;

import io.grpc.inprocess.*;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.*;
import io.grpc.*;
import static org.junit.Assert.*;
import com.example.bookstore.BookOuterClass.*;

public class BookServiceImplTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
    private final BookServiceGrpc.BookServiceBlockingStub bookServiceStub;

    public BookServiceImplTest() throws Exception {
        String serverName = InProcessServerBuilder.generateName();
        Server server = InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(new BookServiceImpl())
                .build()
                .start();
        grpcCleanup.register(server);

        ManagedChannel channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
        bookServiceStub = BookServiceGrpc.newBlockingStub(channel);
    }

    @Test
    public void addBookTest() {
        Book book = Book.newBuilder().setIsbn("123").setTitle("Test Book").addAuthors("Author1").setPageCount(100).build();
        AddBookResponse response = bookServiceStub.addBook(AddBookRequest.newBuilder().setBook(book).build());
        assertTrue(response.getSuccess());
    }
    @Test
    public void updateBookTest() {
        Book book = Book.newBuilder().setIsbn("123").setTitle("Test Book").addAuthors("Author1").setPageCount(100).build();
        bookServiceStub.addBook(AddBookRequest.newBuilder().setBook(book).build());
        Book book2 = Book.newBuilder().setIsbn("123").setTitle("Updated Book").addAuthors("Author1").setPageCount(120).build();
        UpdateBookResponse response2 = bookServiceStub.updateBook(UpdateBookRequest.newBuilder().setBook(book2).build());
        assertTrue(response2.getSuccess());
    }

    @Test
    public void deleteBookTest() {
        Book book = Book.newBuilder().setIsbn("123").setTitle("Test Book").addAuthors("Author1").setPageCount(100).build();
        bookServiceStub.addBook(AddBookRequest.newBuilder().setBook(book).build());
        DeleteBookResponse response = bookServiceStub.deleteBook(DeleteBookRequest.newBuilder().setIsbn("123").build());
        assertTrue(response.getSuccess());
    }

    @Test
    public void getBooksTest() {
        Book book1 = Book.newBuilder().setIsbn("123").setTitle("Test Book1").addAuthors("Author1").setPageCount(100).build();
        Book book2 = Book.newBuilder().setIsbn("124").setTitle("Test Book2").addAuthors("Author2").setPageCount(150).build();
        bookServiceStub.addBook(AddBookRequest.newBuilder().setBook(book1).build());
        bookServiceStub.addBook(AddBookRequest.newBuilder().setBook(book2).build());
        GetBooksResponse response = bookServiceStub.getBooks(GetBooksRequest.newBuilder().build());
        assertEquals(2, response.getBooksCount());
    }

    // Edge cases
    // Exception handling: Adding a book with the same ISBN
    @Test
    public void addBookWithSameIsbnTest() {
        Book book1 = Book.newBuilder().setIsbn("123").setTitle("Test Book1").addAuthors("Author1").setPageCount(100).build();
        Book book2 = Book.newBuilder().setIsbn("123").setTitle("Test Book2").addAuthors("Author2").setPageCount(150).build();

        bookServiceStub.addBook(AddBookRequest.newBuilder().setBook(book1).build());
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            bookServiceStub.addBook(AddBookRequest.newBuilder().setBook(book2).build());
        });
        assertEquals(Status.ALREADY_EXISTS.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getMessage().contains("Book with ISBN 123 already exists."));
    }

    // Exception handling: Adding a book with missing fields
    @Test
    public void addBookWithMissingFieldsTest() {
        Book book = Book.newBuilder().setIsbn("123").build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            bookServiceStub.addBook(AddBookRequest.newBuilder().setBook(book).build());
        });
        assertEquals(Status.INVALID_ARGUMENT.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getMessage().contains("ISBN, title, and authors must be provided."));
    }

    // Exception handling: Invalid ISBN format
    @Test
    public void addBookWithInvalidIsbnTest() {
        Book book = Book.newBuilder().setIsbn("").setTitle("Invalid ISBN Book").addAuthors("Author1").setPageCount(100).build();
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            bookServiceStub.addBook(AddBookRequest.newBuilder().setBook(book).build());
        });
        assertEquals(Status.INVALID_ARGUMENT.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getMessage().contains("ISBN, title, and authors must be provided."));
    }

    // Exception handling: Deleting a non-existent Book
    @Test
    public void deleteNonExistentBookTest() {
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            bookServiceStub.deleteBook(DeleteBookRequest.newBuilder().setIsbn("123").build());
        });
        assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getMessage().contains("Book with ISBN 123 not found"));
    }


    // Exception handling: Updating a non-existent Book
    @Test
    public void updateNonExistentBookTest() {
        Book book = Book.newBuilder().setIsbn("999").setTitle("Non-Existent Book").addAuthors("Author1").setPageCount(100).build();
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            bookServiceStub.updateBook(UpdateBookRequest.newBuilder().setBook(book).build());
        });
        assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getMessage().contains("Book with ISBN 999 not found"));
    }

}
