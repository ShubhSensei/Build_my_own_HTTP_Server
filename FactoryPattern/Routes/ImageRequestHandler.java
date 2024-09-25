package FactoryPattern.Routes;
import java.io.FileInputStream;
import java.util.Collections;

import FactoryPattern.Requests.HttpRequest;
import FactoryPattern.HttpRequestHandler;
import FactoryPattern.Responses.HttpResponse;

public class ImageRequestHandler implements HttpRequestHandler{

    @Override
    public HttpResponse handleRequest(HttpRequest request) throws Exception{
        if(request.getMethod().equals("GET")){
            FileInputStream image = new FileInputStream("image_for_http_response.jpg");
            System.out.println(image.toString());
            byte[] body = image.readAllBytes();
            String StatusMessage = "HTTP/1.1 \r\n OK";
            image.close();
            return new HttpResponse(request.getVersion(), 200, StatusMessage, Collections.emptyMap(), body);
        }else{
            String StatusError = "HTTP/1.1 \r\n Method Not Allowed";
            return new HttpResponse(request.getVersion(), 405, StatusError, Collections.emptyMap(), null);
        }
    }
}
