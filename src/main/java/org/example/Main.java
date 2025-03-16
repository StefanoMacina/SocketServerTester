package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


public class Main {
    public static void main(String[] args) {

        while(true){
            try(ServerSocket serverSocket = new ServerSocket(5000)){
                System.out.printf("Server on, accepting connection at %s:%s%n", serverSocket.getInetAddress().getCanonicalHostName(),serverSocket.getLocalPort());
                try(Socket socket = serverSocket.accept()){
                    System.out.printf("Accepted connection from %s%n", socket.getInetAddress());

                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(socket.getInputStream())
                    );
                    PrintWriter output = new PrintWriter(
                            socket.getOutputStream(), true);

                    while(true){
                        String echoString = input.readLine();

//                        if(echoString == null) {
//                            System.out.println("Client disconnesso");
//                            break;
//                        }
                        if(echoString.equalsIgnoreCase("CB,01,off".trim())){
                            break;
                        }
                        System.out.println("Server got request data: " + echoString);
                        Random r = new Random();
                        Integer num = r.nextInt(1000);
                        output.println("%s,%d".formatted(echoString,num));
                        System.out.printf("Sent to client %s,%d%n", echoString,num);
                    }
                }
            } catch (Exception e) {
                System.out.println("Server error: " + e.getMessage());
            } finally {
                System.out.println("restarting server soon...");
                //TimeUnit.SECONDS.sleep(1);
            }
        }
    }
}