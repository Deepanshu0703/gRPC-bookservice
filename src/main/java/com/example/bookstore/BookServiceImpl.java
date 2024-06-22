package com.example.bookstore;
import com.example.bookstore.BookServiceGrpc.BookServiceImplBase;
import com.example.bookstore.BookOuterClass.*;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ConcurrentHashMap;
public class BookServiceImpl extends BookServiceImplBase{
    private final ConcurrentHashMap<String, BookOuterClass.Book> books = new ConcurrentHashMap<>();
    @Override
    public void addBook(AddBookRequest request, StreamObserver<AddBookResponse> responseObserver) {
        BookOuterClass.Book book = request.getBook();
        books.put(book.getIsbn(), book);
        AddBookResponse response = AddBookResponse.newBuilder().setSuccess(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateBook(UpdateBookRequest request, StreamObserver<UpdateBookResponse> responseObserver) {
        BookOuterClass.Book book = request.getBook();
        books.put(book.getIsbn(), book);
        UpdateBookResponse response = UpdateBookResponse.newBuilder().setSuccess(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBook(DeleteBookRequest request, StreamObserver<DeleteBookResponse> responseObserver) {
        books.remove(request.getIsbn());
        DeleteBookResponse response = DeleteBookResponse.newBuilder().setSuccess(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getBooks(GetBooksRequest request, StreamObserver<GetBooksResponse> responseObserver) {
        GetBooksResponse.Builder responseBuilder = GetBooksResponse.newBuilder();
        responseBuilder.addAllBooks(books.values());
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

}
