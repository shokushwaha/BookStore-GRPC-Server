# BookService gRPC Application

This project is a gRPC-based Book Management System implemented in Java. It includes a gRPC server (`BookServer`) that provides CRUD operations for managing books and a gRPC client (`BookClient`) to interact with the server. The project uses Protocol Buffers (protobuf) for defining the data models and gRPC services.
## Prerequisites

- Java 8 or higher
- Gradle 8 or higher
- Protocol Buffers Compiler (`protoc`) 3.18.1 or higher

## Setup and Build Instructions

1. **Clone the repository:**

    ```bash
    git clone https://github.com/shokushwaha/BookStore-GRPC-Server.git
    cd book-service-grpc
    ```

2. **Install Protocol Buffers:**

   Download and install the Protocol Buffers compiler (`protoc`) from the [official website](https://github.com/protocolbuffers/protobuf/releases).

3. **Build the project:**

   Use Gradle to build the project and generate gRPC classes.

    ```bash
    ./gradlew clean build
    ```

   This command will compile the `proto` files, generate Java classes, and compile the Java source code.

4. **Run the Server:**

   You can run the gRPC server using the following command:

    ```bash
    ./gradlew run --args='org.example.BookServer'
    ```

   

5. **Run the Client:**

   To interact with the server, run the `BookClient`:

    ```bash
    ./gradlew run --args='org.example.BookClient'
    ```


## Services

### AddBook Method

#### Request: AddBookRequest

- `Book book`: A `Book` message containing the book's details.
   - `string isbn`: Unique identifier for the book.
   - `string title`: Title of the book.
   - `repeated string authors`: List of authors.
   - `int32 page_count`: Number of pages.

#### Response: AddBookResponse

- `string message`: A message indicating the result of the operation.

---

### UpdateBook Method

#### Request: UpdateBookRequest

- `string isbn`: The ISBN of the book to update.
- `Book book`: Updated `Book` message containing the new details.
   - `string isbn`: Unique identifier for the book.
   - `string title`: Title of the book.
   - `repeated string authors`: List of authors.
   - `int32 page_count`: Number of pages.

#### Response: UpdateBookResponse

- `string message`: A message indicating the result of the operation.

---

### DeleteBook Method

#### Request: DeleteBookRequest

- `string isbn`: The ISBN of the book to delete.

#### Response: DeleteBookResponse

- `string message`: A message indicating the result of the operation.

---

### GetBooks Method

#### Request: GetBooksRequest

- No specific fields defined.

#### Response: GetBooksResponse

- `repeated Book books`: List of `Book` messages containing all available books.
   - `string isbn`: Unique identifier for the book.
   - `string title`: Title of the book.
   - `repeated string authors`: List of authors.
   - `int32 page_count`: Number of pages.


## Test
The application includes unit tests for the BookServiceImpl class. You can run the tests using the following Gradle command:

```commandline
./gradlew test
```