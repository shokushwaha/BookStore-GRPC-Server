package org.example;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.example.BookOuterClass.*;
import org.example.BookServiceGrpc.*;
import org.junit.*;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class BookClientTest {

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private BookServiceGrpc.BookServiceBlockingStub blockingStub;

    @Before
    public void setUp() throws IOException {
        // Generate a unique server name
        String serverName = InProcessServerBuilder.generateName();

        // Create and start the in-process server with the service implementation
        grpcCleanup.register(InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(new BookServiceImpl())
                .build()
                .start());

        // Create a channel to the in-process server
        ManagedChannel channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build());

        // Create a blocking stub for the client
        blockingStub = BookServiceGrpc.newBlockingStub(channel);
    }

    @Test
    public void testAddBook() {
        // Create a Book instance
        Book book = Book.newBuilder()
                .setIsbn("1234567890")
                .setTitle("Sample Book")
                .addAllAuthors(Collections.singletonList("Author1"))
                .setPageCount(200)
                .build();

        // Test adding a book
        AddBookRequest request = AddBookRequest.newBuilder()
                .setBook(book)
                .build();
        AddBookResponse response = blockingStub.addBook(request);

        // Verify the response
        assertEquals("Book added: Sample Book", response.getMessage());
    }

    @Test
    public void testUpdateBook() {
        // Add a book first to update it
        Book initialBook = Book.newBuilder()
                .setIsbn("1111111111")
                .setTitle("Initial Book")
                .addAllAuthors(Collections.singletonList("Initial Author"))
                .setPageCount(100)
                .build();
        AddBookRequest addRequest = AddBookRequest.newBuilder()
                .setBook(initialBook)
                .build();
        blockingStub.addBook(addRequest);

        // Create an updated Book instance
        Book updatedBook = Book.newBuilder()
                .setIsbn("1111111111")
                .setTitle("Updated Book Title")
                .addAllAuthors(Collections.singletonList("Updated Author"))
                .setPageCount(250)
                .build();

        // Test updating a book
        UpdateBookRequest request = UpdateBookRequest.newBuilder()
                .setIsbn("1111111111")
                .setBook(updatedBook)
                .build();
        UpdateBookResponse response = blockingStub.updateBook(request);

        // Verify the response
        assertEquals("Book updated: Updated Book Title", response.getMessage());
    }

    @Test
    public void testDeleteBook() {
        // Add a book first to delete it
        Book book = Book.newBuilder()
                .setIsbn("2222222222")
                .setTitle("Book to be Deleted")
                .addAllAuthors(Collections.singletonList("Author"))
                .setPageCount(150)
                .build();
        AddBookRequest addRequest = AddBookRequest.newBuilder()
                .setBook(book)
                .build();
        blockingStub.addBook(addRequest);

        // Test deleting a book
        DeleteBookRequest request = DeleteBookRequest.newBuilder()
                .setIsbn("2222222222")
                .build();
        DeleteBookResponse response = blockingStub.deleteBook(request);

        // Verify the response
        assertEquals("Book deleted: 2222222222", response.getMessage());
    }

    @Test
    public void testGetAllBooks() {
        // Add a book to get
        Book book = Book.newBuilder()
                .setIsbn("1234567890")
                .setTitle("Sample Book")
                .addAllAuthors(Collections.singletonList("Author1"))
                .setPageCount(200)
                .build();
        AddBookRequest addRequest = AddBookRequest.newBuilder()
                .setBook(book)
                .build();
        blockingStub.addBook(addRequest);

        // Test getting all books
        GetBooksRequest request = GetBooksRequest.newBuilder().build();
        GetBooksResponse response = blockingStub.getBooks(request);

        // Verify the response contains the expected book
        assertEquals(1, response.getBooksCount());
        Book returnedBook = response.getBooks(0);
        assertEquals("1234567890", returnedBook.getIsbn());
        assertEquals("Sample Book", returnedBook.getTitle());
        assertEquals(1, returnedBook.getAuthorsCount());
        assertEquals("Author1", returnedBook.getAuthors(0));
        assertEquals(200, returnedBook.getPageCount());
    }
}
