package com.example.bookstore;

import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.Test;
import io.grpc.ManagedChannel;
import io.grpc.Server;

import static org.junit.Assert.*;

import com.example.bookstore.BookOuterClass.Book;
import com.example.bookstore.BookOuterClass.AddBookRequest;
import com.example.bookstore.BookOuterClass.AddBookResponse;
import com.example.bookstore.BookOuterClass.UpdateBookRequest;
import com.example.bookstore.BookOuterClass.UpdateBookResponse;
import com.example.bookstore.BookOuterClass.DeleteBookRequest;
import com.example.bookstore.BookOuterClass.DeleteBookResponse;
import com.example.bookstore.BookOuterClass.GetBooksRequest;
import com.example.bookstore.BookOuterClass.GetBooksResponse;

public class BookServiceImplTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private BookServiceGrpc.BookServiceBlockingStub bookServiceStub;

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
        Book book = Book.newBuilder().setIsbn("123").setTitle("Updated Book").addAuthors("Author1").setPageCount(120).build();
        UpdateBookResponse response = bookServiceStub.updateBook(UpdateBookRequest.newBuilder().setBook(book).build());
        assertTrue(response.getSuccess());
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
}
