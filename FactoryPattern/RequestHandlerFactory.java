package FactoryPattern;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import FactoryPattern.Requests.HttpRequest;
import FactoryPattern.Responses.HttpResponse;
import FactoryPattern.Routes.*;

public class RequestHandlerFactory {

    private final ExecutorService executorService;

    public RequestHandlerFactory(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Callable<HttpResponse> createHandler(HttpRequest request, String path){
        switch (path){
            case "/hello":
                return () -> new HelloRequestHandler().handleRequest(request);
            case "/image":
                return () -> new ImageRequestHandler().handleRequest(request);
            case "/echo/":
                return () -> new EchoRequestHandler().handleRequest(request);
            default:
                return () -> new DefaultRequestHandler().handleRequest(request);
        }
    }

    public Future<HttpResponse> submitRequest(HttpRequest request) throws Exception {
        Callable<HttpResponse> handlerCallable = createHandler(request, request.getPath());
        return executorService.submit(handlerCallable);
    }

    public void handleClientRequest(Socket clientSocket) throws Exception{

        // Create a request object from the incoming socket
        HttpRequest request = parse(new InputStreamReader(clientSocket.getInputStream()), clientSocket);

        HttpRequestHandler handler = (HttpRequestHandler) createHandler(request, request.getPath());

        // Handle the request and send the response
        HttpResponse response = handler.handleRequest(request);

        OutputStream outputStream = clientSocket.getOutputStream();

        // outputStream.write("HTTP/1.1".getBytes());
        outputStream.write(response.getBody());
        clientSocket.close();
    }

    public static HttpRequest parse(InputStreamReader inputStreamReader, Socket client) throws Exception {
        inputStreamReader = new InputStreamReader(client.getInputStream());
        BufferedReader reader = new BufferedReader(inputStreamReader);
    
        StringBuilder requestLine = new StringBuilder();
        String line;
        line = reader.readLine();
        while(!line.isBlank()){
            requestLine.append(line + "\r\n");
            line = reader.readLine();
        }

        // This line will contain the first line 
        String[] ArrayOfRequestLines = requestLine.toString().split("/n");

        // The below 2 lines will extract the Method and path from the first line
        String method = ArrayOfRequestLines[0].split(" ")[0];
        String path = ArrayOfRequestLines[0].split(" ")[1];
        String version = ArrayOfRequestLines[0].split(" ")[2];
        
        /*
         * Extracting the headers part
         */
        Map<String, String> headers = new HashMap<>();
        for(String item : ArrayOfRequestLines){
            if(item.contains(": ")){
                String[] parts = item.split(": ");
                headers.put(parts[0], parts[1]);
            }
        }

        StringBuilder body = new StringBuilder();
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            for (int i = 0; i < contentLength; i++) {
                body.append((char) reader.read());
            }
        }
    
        return new HttpRequest(version, method, path, headers, body.toString());
    }
}
