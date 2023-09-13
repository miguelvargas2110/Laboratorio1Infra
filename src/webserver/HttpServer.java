package webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
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
                try{
                    String [] mensaje = message.split(" ");
                    direccion = mensaje[1];
                    break;
                }catch (Exception e){

                }
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

    public void enviarPagina(String direccion) throws IOException {

        try{
            String [] spliteado = direccion.split("\\.");
            String extension = spliteado[spliteado.length-1];
            if(extension.equals("html")) {
                BufferedReader fileReader = new BufferedReader(new FileReader("src" + direccion));
                File localFile = new File("src\\" + direccion);
                long contentLength = localFile.length();
                long fecha = localFile.lastModified();
                Date lastModified = new Date(fecha);
                toNetwork.println("HTTP/1.1 200");
                toNetwork.println("Server: Laboratorio 1");
                toNetwork.println("Date: " + new java.util.Date());
                toNetwork.println("Last-Modified: " + lastModified);
                toNetwork.println("Content-type: text/html");
                toNetwork.println("Content-length: " + contentLength);
                toNetwork.println("Cache-Control: no-cache");
                toNetwork.println("");
                String message;
                while ((message = fileReader.readLine()) != null) {
                    toNetwork.println(message);
                }
            }else if(extension.equals("pdf")){
                FileInputStream fileInputStream = new FileInputStream("src" + direccion);
                File localFile = new File("src\\" + direccion);
                long contentLength = localFile.length();
                long fecha = localFile.lastModified();
                Date lastModified = new Date(fecha);
                toNetwork.println("HTTP/1.1 200");
                toNetwork.println("Server: Laboratorio 1");
                toNetwork.println("Date: " + new java.util.Date());
                toNetwork.println("Last-Modified: " + lastModified);
                toNetwork.println("Content-type: application/pdf");
                toNetwork.println("Content-length: " + contentLength);
                toNetwork.println("Cache-Control: no-cache");
                toNetwork.println("");
                OutputStream outputStream = serverSideSocket.getOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                fileInputStream.close();
                outputStream.close();
            }else{
                FileInputStream fileInputStream = new FileInputStream("src" + direccion);
                File localFile = new File("src\\" + direccion);
                long contentLength = localFile.length();
                long fecha = localFile.lastModified();
                Date lastModified = new Date(fecha);
                toNetwork.println("HTTP/1.1 200");
                toNetwork.println("Server: Laboratorio 1");
                toNetwork.println("Date: " + new java.util.Date());
                toNetwork.println("Last-Modified: " + lastModified);
                toNetwork.println("Content-type: image/" + extension);
                toNetwork.println("Content-length: " + contentLength);
                toNetwork.println("Cache-Control: no-cache");
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
            BufferedReader fileReader = new BufferedReader(new FileReader("src/NotFound.html"));
            File localFile = new File("src/NotFound.html");
            long contentLength = localFile.length();
            toNetwork.println("HTTP/1.1 404 Not Found");
            toNetwork.println("Server: Laboratorio 1");
            toNetwork.println("Date: " + new java.util.Date());
            toNetwork.println("Content-type: text/html");
            toNetwork.println("Content-length: " + contentLength);
            toNetwork.println("");
            String message;
            while ((message = fileReader.readLine()) != null) {
                toNetwork.println(message);
            }
            toNetwork.flush();
        }

    }



    public static void main(String[] args) throws Exception {
        HttpServer server = new HttpServer();
        server.init();
    }
}
