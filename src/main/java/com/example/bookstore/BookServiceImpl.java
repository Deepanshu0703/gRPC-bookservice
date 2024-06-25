package com.example.bookstore;

import com.example.bookstore.BookOuterClass.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.ConcurrentHashMap;

public class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {
    private final ConcurrentHashMap<String, Book> books = new ConcurrentHashMap<>();

    @Override
    public void addBook(AddBookRequest request, StreamObserver<AddBookResponse> responseObserver) {
        Book book = request.getBook();
        if (book.getIsbn().isEmpty() || book.getTitle().isEmpty() || book.getAuthorsList().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("ISBN, title, and authors must be provided.")
                    .asRuntimeException());
            return;
        }
        if (books.containsKey(book.getIsbn())) {
            responseObserver.onError(Status.ALREADY_EXISTS
                    .withDescription("Book with ISBN " + book.getIsbn() + " already exists.")
                    .asRuntimeException());
            return;
        }
        books.put(book.getIsbn(), book);
        AddBookResponse response = AddBookResponse.newBuilder().setSuccess(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateBook(UpdateBookRequest request, StreamObserver<UpdateBookResponse> responseObserver) {
        Book book = request.getBook();
        if (!books.containsKey(book.getIsbn())) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Book with ISBN " + book.getIsbn() + " not found.")
                    .asRuntimeException());
            return;
        }
        books.put(book.getIsbn(), book);
        UpdateBookResponse response = UpdateBookResponse.newBuilder().setSuccess(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBook(DeleteBookRequest request, StreamObserver<DeleteBookResponse> responseObserver) {
        String isbn = request.getIsbn();
        if (!books.containsKey(isbn)) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Book with ISBN " + isbn + " not found.")
                    .asRuntimeException());
            return;
        }
        books.remove(isbn);
        DeleteBookResponse response = DeleteBookResponse.newBuilder().setSuccess(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getBooks(GetBooksRequest request, StreamObserver<GetBooksResponse> responseObserver) {
        try {
            GetBooksResponse.Builder responseBuilder = GetBooksResponse.newBuilder();
            responseBuilder.addAllBooks(books.values());
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to retrieve books: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
