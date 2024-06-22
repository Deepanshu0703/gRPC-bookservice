package com.example.bookstore;

import io.grpc.*;

public class BookClient {
    private final ManagedChannel channel;
    private final BookServiceGrpc.BookServiceBlockingStub blockingStub;

    public BookClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = BookServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() {
        channel.shutdown();
    }

    public void addBook(BookOuterClass.Book book) {
        BookOuterClass.AddBookRequest request = BookOuterClass.AddBookRequest.newBuilder().setBook(book).build();
        BookOuterClass.AddBookResponse response = blockingStub.addBook(request);
        System.out.println("AddBook Response: " + response.getSuccess());
    }

    public void updateBook(BookOuterClass.Book book) {
        BookOuterClass.UpdateBookRequest request = BookOuterClass.UpdateBookRequest.newBuilder().setBook(book).build();
        BookOuterClass.UpdateBookResponse response = blockingStub.updateBook(request);
        System.out.println("UpdateBook Response: " + response.getSuccess());
    }

    public void deleteBook(String isbn) {
        BookOuterClass.DeleteBookRequest request = BookOuterClass.DeleteBookRequest.newBuilder().setIsbn(isbn).build();
        BookOuterClass.DeleteBookResponse response = blockingStub.deleteBook(request);
        System.out.println("DeleteBook Response: " + response.getSuccess());
    }

    public void getBooks() {
        BookOuterClass.GetBooksRequest request = BookOuterClass.GetBooksRequest.newBuilder().build();
        BookOuterClass.GetBooksResponse response = blockingStub.getBooks(request);
        for (BookOuterClass.Book book : response.getBooksList()) {
            System.out.printf("ISBN: %s, Title: %s, Authors: %s, Page Count: %d%n",
                    book.getIsbn(), book.getTitle(), book.getAuthorsList(), book.getPageCount());
        }
    }

    public static void main(String[] args) {
        BookClient client = new BookClient("localhost", 50051);

        BookOuterClass.Book book1 = BookOuterClass.Book.newBuilder().setIsbn("123").setTitle("Test Book 1").addAuthors("Author 1").setPageCount(100).build();
        BookOuterClass.Book book2 = BookOuterClass.Book.newBuilder().setIsbn("124").setTitle("Test Book 2").addAuthors("Author 2").setPageCount(150).build();

        client.addBook(book1);
        client.addBook(book2);

        System.out.println("All Books:");
        client.getBooks();

        book1 = BookOuterClass.Book.newBuilder().setIsbn("123").setTitle("Updated Book 1").addAuthors("Author 1 Updated").setPageCount(120).build();
        client.updateBook(book1);

        System.out.println("After Update:");
        client.getBooks();

        client.deleteBook("124");

        System.out.println("After Deleting ISBN 124:");
        client.getBooks();

        client.shutdown();
    }
}
