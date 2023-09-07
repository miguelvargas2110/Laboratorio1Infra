package webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class HttpServer {
    public static final int PORT = 8000;
    private ServerSocket listener;
    private Socket serverSideSocket;
    private PrintWriter toNetwork;
    private BufferedReader fromNetwork;

    public HttpServer() {
        System.out.println("The web server is tunning on port: " + PORT);
    }

    public void init() throws Exception {
        listener = new ServerSocket(PORT);
        while (true) {
            System.out.println("Antes del accept");
            serverSideSocket = listener.accept();
            createStreams(serverSideSocket);
            String message = fromNetwork.readLine();
            String direccion = "";
            while(true){
                String [] mensaje = message.split(" ");
                direccion = mensaje[1];
                break;
            }
            while (!message.equals("")){
                System.out.println(message);
                message = fromNetwork.readLine();
            }
            enviarPagina(direccion);
            serverSideSocket.close();
            System.out.println("=============================");
        }
    }

    private void createStreams(Socket socket) throws Exception {
        toNetwork = new PrintWriter(socket.getOutputStream(), true);
        fromNetwork = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void enviarPagina(String direccion) {

        try{
            String [] spliteado = direccion.split("\\.");
            String extension = spliteado[spliteado.length-1];
            if(extension.equals("hmtl")) {
                BufferedReader fileReader = new BufferedReader(new FileReader("src" + direccion));
                toNetwork.println("HTTP/1.1 200");
                toNetwork.println("Context-Type: text/html");
                toNetwork.println("");
                String message;
                while ((message = fileReader.readLine()) != null) {
                    toNetwork.println(message);
                }
            }else{
                FileInputStream fileInputStream = new FileInputStream("src" + direccion);
                toNetwork.println("HTTP/1.1 200");
                toNetwork.println("Context-Type: image/jpg");
                toNetwork.println("");
                OutputStream outputStream = serverSideSocket.getOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                fileInputStream.close();
                outputStream.close();
            }

        }catch (Exception e){
            toNetwork.println("HTTP/1.1 403 Not Found");
            toNetwork.println("Server: Bard");
            toNetwork.println("Date: " + new java.util.Date());
            toNetwork.println("Content-type: text/html");
            toNetwork.println("");
            toNetwork.println("<html>");
            toNetwork.println("<head>");
            toNetwork.println("<title>404 Not Found</title>");
            toNetwork.println("</head>");
            toNetwork.println("<body>");
            toNetwork.println("<h1>404 Not Found</h1>");
            toNetwork.println("<p>The requested resource could not be found.</p>");
            toNetwork.println("</body>");
            toNetwork.println("</html>");
            toNetwork.flush();
        }

    }



    public static void main(String[] args) throws Exception {
        HttpServer server = new HttpServer();
        server.init();
    }
}