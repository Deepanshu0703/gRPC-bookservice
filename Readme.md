# Book Service

This is a gRPC-based book service application.

## Build and Run

1. **Prerequisites**:
   - Java 11 or higher
   - Gradle

2. **Build the Project**:
   - Clone the repository: `git clone https://github.com/Deepanshu0703/gRPC-bookservice.git`
   - Navigate to the project directory: `cd gRPC-bookservice`
   - Build the project using Gradle: `./gradlew build`

3. **Run the Server**:
   - Start the gRPC server `BookServer.java` file.
   - The server will start and listen on port `50051`.

4. **Run the Client**:
   - Run the client program `BookClient.java` file.
   - The client will connect to the server and interact with the gRPC service.

## Implemented RPCs

The book service application implements the following RPCs:

1. **GetBook**:
   - **Description**: Retrieves a book by its ID.
   - **Input**: A string representing the book ID.
   - **Output**: A `Book` message containing the book details.

2. **CreateBook**:
   - **Description**: Creates a new book.
   - **Input**: A `Book` message containing the book details.
   - **Output**: A `Book` message containing the created book details.

3. **UpdateBook**:
   - **Description**: Updates an existing book.
   - **Input**: A `Book` message containing the updated book details.
   - **Output**: A `Book` message containing the updated book details.

4. **DeleteBook**:
   - **Description**: Deletes a book by its ID.
   - **Input**: A string representing the book ID.
   - **Output**: A `DeleteResponse` message indicating the success or failure of the operation.

## Testing

The application includes unit tests for the `BookServiceImpl` class. You can run the tests using the following Gradle command:

```
./gradlew test
```

This will execute all the unit tests and provide you with the test results. 