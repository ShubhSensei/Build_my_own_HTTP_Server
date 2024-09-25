package FactoryPattern;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import FactoryPattern.Requests.HttpRequest;
import FactoryPattern.Routes.DefaultRequestHandler;
import FactoryPattern.Routes.HelloRequestHandler;
import FactoryPattern.Routes.ImageRequestHandler;

public class RequestHandlerFactory {
    public HttpRequestHandler createHandler(String path){
        switch (path){
            case "/hello":
                return (HttpRequestHandler) new HelloRequestHandler();
            case "/image":
                return (HttpRequestHandler) new ImageRequestHandler();
            default:
                return (HttpRequestHandler) new DefaultRequestHandler();
        }
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
