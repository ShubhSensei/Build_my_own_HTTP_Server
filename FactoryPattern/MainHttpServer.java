package FactoryPattern;
import java.net.ServerSocket;
import java.net.Socket;

import FactoryPattern.Requests.HttpRequest;
import FactoryPattern.Responses.HttpResponse;

import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainHttpServer {
    public static void main(String[] args) throws Exception{
        try(ServerSocket serverSocket = new ServerSocket(8080)){
            System.out.println("Server started on port 8080");

            while(true){
                try(Socket clientSocket = serverSocket.accept()){
                    System.out.println("Client connected");
                    
                    // Use a factory to create the appropriate request handler
                    RequestHandlerFactory factory = new RequestHandlerFactory();

                    // Create a request object from the incoming socket
                    HttpRequest request = RequestHandlerFactory.parse(new InputStreamReader(clientSocket.getInputStream()), clientSocket);

                    HttpRequestHandler handler = factory.createHandler(request.getPath());

                    // Handle the request and send the response
                    HttpResponse response = handler.handleRequest(request);

                    OutputStream outputStream = clientSocket.getOutputStream();

                    // outputStream.write("HTTP/1.1".getBytes());
                    outputStream.write(response.getBody());
                    clientSocket.close();
                }
            }
        }
    }
}
