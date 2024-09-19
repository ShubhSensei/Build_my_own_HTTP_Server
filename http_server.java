import java.net.Socket;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;

public class http_server{
    public static void main(String[] args) throws Exception{
        // start receiving messages - ready to receive messages
        try(ServerSocket serverSocket = new ServerSocket(8080)){
            System.out.println("Server Started.\nListening for messages");

            while(true){
                // Hande a new incoming message
                try(Socket client = serverSocket.accept()){
                    // client <-- messages queued up in it!!
                    System.out.println("Debug: got new message " + client.toString());

                    // Resad the request - Listen to the message
                    InputStreamReader isr = new InputStreamReader(client.getInputStream());

                    BufferedReader reader = new BufferedReader(isr);

                    StringBuilder request = new StringBuilder();
                    String line;    // Temp variable called line that holds one line at a time of our message
                    line = reader.readLine();
                    while(!line.isBlank()){
                        request.append(line + "\r\n");
                        line = reader.readLine();
                    }

                    System.out.println("--REQUEST--");
                    // System.out.println("Response Sent");
                    // System.out.println(request);

                    OutputStream clientOutput = client.getOutputStream();
                    
                    // Decide how er'd like to respond
                    
                    // Change response based on route?

                    // Get the first line of the request
                    String firstLine = request.toString().split("\n")[0];
                    // Get the second thing "resource" from the first line (separated by spaces)
                    String resource = firstLine.split(" ")[1];
                    
                    // compare the "resource" to our list of changes
                    // sends back the appropriate thing based on resource
                    if(resource.equals("/hello")){
                        // Just send back a simple "Hello World"
                        clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
                        clientOutput.write("\r\n".getBytes());
                        clientOutput.write(("Hello World").getBytes());
                    } else if(resource.equals("/image")){
                        // Load the image from the filesystem
                        // Send back an image
                        FileInputStream image = new FileInputStream("image_for_http_response.jpg");
                        System.out.println(image.toString());
                        clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
                        clientOutput.write("\r\n".getBytes());
                        clientOutput.write((image).readAllBytes());
                        image.close();
                    } else {
                        clientOutput.write("HTTP/1.1 200 OK \r\n".getBytes());
                        clientOutput.write("\r\n".getBytes());
                        clientOutput.write("What ya lookin at? ".getBytes());
                    }
                // Get ready for the next message
                    client.close();
                }
            }
        }
    }
}