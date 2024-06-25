# Bookstore gRPC Project

## Overview

This project implements a simple gRPC-based bookstore service in Java. It provides CRUD operations for managing books using an in-memory data store. The project utilizes Gradle for dependency management and build automation, Protobuf for data serialization, and gRPC for RPC communication.

## Prerequisites
- Java Development Kit (JDK) 8 or higher
- Gradle 8.5 or higher
- IntelliJ IDEA or any other preferred IDE
- Protobuf Compiler (protoc)

## Step By Step

1. Clone the repository

`git clone `https://github.com/shokushwaha/BookStore-GRPC-Server.git`
`cd bookstore-grpc`

2.  Set Up Your Development Environment
    Open the project in IntelliJ IDEA or any other preferred IDE.

3. Update build.gradle
   Ensure your build.gradle includes the following configurations:
```
plugins {
    id 'java'
    id 'com.google.protobuf' version '0.8.17'
}

repositories {
    mavenCentral()
}

protobuf {
    protoc {
        artifact = 'com.google.protobuf:protoc:3.18.1'
    }
    plugins {
        grpc {
            artifact = 'io.grpc:protoc-gen-grpc-java:1.41.0'
        }
    }
    generateProtoTasks {
        all().each { task ->
            task.builtins {
                java {}
            }
            task.plugins {
                grpc {}
            }
        }
    }
}

dependencies {
     implementation 'io.grpc:grpc-netty:1.41.0'
    implementation 'io.grpc:grpc-protobuf:1.41.0'
    implementation 'io.grpc:grpc-stub:1.41.0'
    implementation 'com.google.protobuf:protobuf-java:3.24.0'
    implementation 'javax.annotation:javax.annotation-api:1.3.2'
    implementation 'jakarta.annotation:jakarta.annotation-api:2.1.1'
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'io.grpc:grpc-testing:1.57.0'
}

sourceSets {
    main {
        java {
            srcDirs += 'build/generated/source/proto/main/grpc'
            srcDirs += 'build/generated/source/proto/main/java'
        }
    }
}
```



## Run The Server
1. Compile Protobuf
    Run the following command to generate Java classes from the protobuf definition:

`./gradlew clean`

`./gradlew build`

`./gradlew generateProto`



2. Run the GRPC Server:
   Run the main method in BookServer.java file.


3. Run the Client to test the functionalities:
   Run the main method in BookClient.java file to test the functionalities( Add Book Service, Update Book Service, Delete Book Service, Get All Books Service).


