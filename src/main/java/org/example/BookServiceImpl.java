package org.example;

import org.example.BookOuterClass.*;
import org.example.BookServiceGrpc.BookServiceImplBase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.grpc.stub.StreamObserver;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class BookServiceImpl extends   BookServiceImplBase {

    private final Map<String, Book> bookStorage = new ConcurrentHashMap<>();


    // Set up GRPC server on 50051 and keep it running until terminated
    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(50051)
                .addService(new BookServiceImpl())
                .build();

        server.start();
        System.out.println("Server started on port " + server.getPort());

        // Shutdown hook to properly close the server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server");
            server.shutdown();
            System.out.println("Server shut down");
        }));

        // Keep the server running until terminated
        server.awaitTermination();
    }

    // Method to add book
    @Override
    public void addBook(AddBookRequest request, StreamObserver<AddBookResponse> responseObserver) {
        // Extract book information from request
        Book book = request.getBook();

        // Check if the book with same isbn already exists
        if (bookStorage.containsKey(book.getIsbn())) {
            AddBookResponse response = AddBookResponse.newBuilder()
                    .setMessage("Error: Book with ISBN " + book.getIsbn() + " already exists.")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        // Store the book in in-memory hashmap
        bookStorage.put(book.getIsbn(), book);

        // Create a response message
        AddBookResponse response = AddBookResponse.newBuilder()
                .setMessage("Book added successfully!")
                .build();

        // Send the response to the client
        responseObserver.onNext(response);

        // Notify the client that request is completed
        responseObserver.onCompleted();
    }


    // Method to update book
    @Override
    public void updateBook(UpdateBookRequest request, StreamObserver<UpdateBookResponse> responseObserver) {
        // Extract the book information
        Book book = request.getBook();

        // Check if book with isbn does not exist
        if (!bookStorage.containsKey(book.getIsbn())) {
            UpdateBookResponse response = UpdateBookResponse.newBuilder()
                    .setMessage("Error: Book with ISBN " + book.getIsbn() + " does not exist.")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        // Updating the book information
        bookStorage.put(book.getIsbn(), book);

        // Creating the response message
        UpdateBookResponse response = UpdateBookResponse.newBuilder()
                .setMessage("Book updated successfully!")
                .build();

        // Send the response
        responseObserver.onNext(response);

        // Notify the client
        responseObserver.onCompleted();
    }

    // Method to delete book
    @Override
    public void deleteBook(DeleteBookRequest request, StreamObserver<DeleteBookResponse> responseObserver) {

        // Check if the book with isbn does not exist
        if (!bookStorage.containsKey(request.getIsbn())) {
            // If the book does not exist, return an error message
            DeleteBookResponse response = DeleteBookResponse.newBuilder()
                    .setMessage("Error: Book with ISBN " + request.getIsbn() + " does not exist.")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        // Removing the book from hashmap
        bookStorage.remove(request.getIsbn());

        // Creating the response message
        DeleteBookResponse response = DeleteBookResponse.newBuilder()
                .setMessage("Book deleted successfully!")
                .build();

        // Send the response
        responseObserver.onNext(response);

        // Notify client
        responseObserver.onCompleted();
    }

    // Method to get all the books
    @Override
    public void getBooks(GetBooksRequest request, StreamObserver<GetBooksResponse> responseObserver) {
        // Builder used to construct response by adding books from hashmap
        GetBooksResponse.Builder responseBuilder = GetBooksResponse.newBuilder();

        // Adding books in the response
        bookStorage.values().forEach(responseBuilder::addBooks);

        // Send the data to client
        responseObserver.onNext(responseBuilder.build());

        // Notify the client
        responseObserver.onCompleted();
    }
}
