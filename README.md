# Java Socket Client

This project implements a Java client that connects to a server on port 5000, sends a request, and waits for a response. If the connection fails, the client will attempt to reconnect up to 10 times with a delay of 2 seconds between each attempt.

## Requirements

- JDK 11 or higher
- Maven 3.x

## Compilation and Execution

### 1. Clone the Repository

```sh
    git clone https://github.com/your-username/repository-name.git
    cd repository-name
```

### 2. Build the Project with Maven

Run the following command:

```sh
    mvn clean package
```

This will generate a JAR file in the `target/` folder.

### 3. Run the Generated JAR

After generating the `.jar` file, run it with the following command:

```sh
    java -jar target/SocketServerTEST-1.0.jar
```

**Note:** Replace `jar-file-name.jar` with the correct name of the JAR file generated by Maven.

## Functionality

The client will attempt to connect to `127.0.0.1` on port `5000`. If the connection fails, it will wait 2 seconds before retrying, up to a maximum of 10 attempts. If the connection is successful, it will send the message `CB,01` to the server and wait for a response. The server's response will be printed to the console.

If the server closes the connection, the client will automatically attempt to reconnect.

## Project Structure

```sh
repository-name/
│   pom.xml
│   README.md
└───src/
    └───main/
        ├───java/
        │   └───org/example/
        │       └───Main.java
        └───resources/
```

## Customization

- To modify the maximum number of connection attempts, change the value of the `ATTEMPTS` variable in the code.
- To modify the delay between reconnection attempts, change the `DELAY` variable in the code.

## Contact

- Check my website: www.stefanomacinaleone.it




