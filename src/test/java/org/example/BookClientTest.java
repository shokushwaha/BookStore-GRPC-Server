package org.example;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.example.BookOuterClass.*;
import org.example.BookServiceGrpc.*;
import org.junit.*;

import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class BookClientTest {

    // Rule to automatically close the gRPC server and channel
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private ManagedChannel channel;
    private BookServiceBlockingStub blockingStub;

    @Before
    public void setUp() throws Exception {
        // Generate a unique server name
        String serverName = InProcessServerBuilder.generateName();

        // Start the in-process server
        grpcCleanup.register(InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(new BookServiceImpl())
                .build()
                .start());

        // Create an in-process channel
        channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build());

        // Create the blocking stub for the client
        blockingStub = BookServiceGrpc.newBlockingStub(channel);
    }

    @Test
    public void testAddBook() {
        // Create a new book with a random ISBN
        String isbn = UUID.randomUUID().toString();
        Book book = Book.newBuilder()
                .setIsbn(isbn)
                .setTitle("Sample Book")
                .addAllAuthors(Collections.singletonList("Author1"))
                .setPageCount(100)
                .build();

        // Create the add book request
        AddBookRequest request = AddBookRequest.newBuilder().setBook(book).build();

        // Send the request and get the response
        AddBookResponse response = blockingStub.addBook(request);

        // Verify the response
        assertEquals("Book added successfully!", response.getMessage());

        // Try adding the same book again to check for error message
        response = blockingStub.addBook(request);
        assertEquals("Error: Book with ISBN " + isbn + " already exists.", response.getMessage());
    }

    @Test
    public void testUpdateBook() {
        // Create a new book with a random ISBN
        String isbn = UUID.randomUUID().toString();
        Book book = Book.newBuilder()
                .setIsbn(isbn)
                .setTitle("Sample Book")
                .addAllAuthors(Collections.singletonList("Author1"))
                .setPageCount(100)
                .build();

        // Add the book first
        blockingStub.addBook(AddBookRequest.newBuilder().setBook(book).build());

        // Update the book
        Book updatedBook = Book.newBuilder()
                .setIsbn(isbn)
                .setTitle("Updated Book Title")
                .addAllAuthors(Collections.singletonList("Author1"))
                .setPageCount(150)
                .build();

        // Create the update book request
        UpdateBookRequest updateRequest = UpdateBookRequest.newBuilder()
                .setIsbn(isbn)
                .setBook(updatedBook)
                .build();

        // Send the request and get the response
        UpdateBookResponse updateResponse = blockingStub.updateBook(updateRequest);

        // Verify the response
        assertEquals("Book updated successfully!", updateResponse.getMessage());

        // Try updating a non-existing book
        String nonExistentIsbn = UUID.randomUUID().toString();
        updateRequest = UpdateBookRequest.newBuilder()
                .setIsbn(nonExistentIsbn)
                .setBook(updatedBook)
                .build();

        updateResponse = blockingStub.updateBook(updateRequest);
        assertEquals("Error: Book with ISBN " + nonExistentIsbn + " does not exist.", updateResponse.getMessage());
    }

    @Test
    public void testDeleteBook() {
        // Create a new book with a random ISBN
        String isbn = UUID.randomUUID().toString();
        Book book = Book.newBuilder()
                .setIsbn(isbn)
                .setTitle("Sample Book")
                .addAllAuthors(Collections.singletonList("Author1"))
                .setPageCount(100)
                .build();

        // Add the book first
        blockingStub.addBook(AddBookRequest.newBuilder().setBook(book).build());

        // Create the delete book request
        DeleteBookRequest deleteRequest = DeleteBookRequest.newBuilder().setIsbn(isbn).build();

        // Send the request and get the response
        DeleteBookResponse deleteResponse = blockingStub.deleteBook(deleteRequest);

        // Verify the response
        assertEquals("Book deleted successfully!", deleteResponse.getMessage());

        // Try deleting a non-existing book
        deleteResponse = blockingStub.deleteBook(deleteRequest);
        assertEquals("Error: Book with ISBN " + isbn + " does not exist.", deleteResponse.getMessage());
    }

    @Test
    public void testGetAllBooks() {
        // Create and add a new book with a random ISBN
        String isbn = UUID.randomUUID().toString();
        Book book = Book.newBuilder()
                .setIsbn(isbn)
                .setTitle("Sample Book")
                .addAllAuthors(Collections.singletonList("Author1"))
                .setPageCount(100)
                .build();

        blockingStub.addBook(AddBookRequest.newBuilder().setBook(book).build());

        // Create the get books request
        GetBooksRequest getBooksRequest = GetBooksRequest.newBuilder().build();

        // Send the request and get the response
        GetBooksResponse getBooksResponse = blockingStub.getBooks(getBooksRequest);

        // Verify the response
        assertEquals(1, getBooksResponse.getBooksCount());
        Book retrievedBook = getBooksResponse.getBooks(0);
        assertEquals(book.getIsbn(), retrievedBook.getIsbn());
        assertEquals(book.getTitle(), retrievedBook.getTitle());
        assertEquals(book.getAuthorsList(), retrievedBook.getAuthorsList());
        assertEquals(book.getPageCount(), retrievedBook.getPageCount());
    }
}
