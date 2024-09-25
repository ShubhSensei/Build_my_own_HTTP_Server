package FactoryPattern.Responses;
import java.util.*;

public class HttpResponse{
    private final String version;
    private final int StatusCode;
    private final String StatusMessage;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(String version, int StatusCode, String StatusMessage, Map<String, String> headers, byte[] body){
        this.version = version;
        this.StatusCode = StatusCode;
        this.StatusMessage = StatusMessage;
        this.headers = headers;
        this.body = body;
    }

    public String getVersion(){
        return version;
    }

    public int getStatusCode(){
        return StatusCode;
    }

    public String getStatusMessage(){
        return StatusMessage;
    } 

    public Map<String, String> getHeaders(){
        return headers;
    }

    public byte[] getBody(){
        return body;
    }
}