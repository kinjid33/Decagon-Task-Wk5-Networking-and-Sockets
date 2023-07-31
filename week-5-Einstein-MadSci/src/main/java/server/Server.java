package server;

import client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
//    Server socket to administrate communication
    private ServerSocket serverSocket;
//    constructor to initialise Server socket
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

//    method to start server and keep it running
    public void startServer(){
        try {
//            while server socket is open or running
            while(!(serverSocket.isClosed())){
//                listen for connection from client socket
                Socket socket = serverSocket.accept();
//                when connection is made, alert user(s) (connection returns a socket object)
                System.out.println("A new client has connected");
//                client handler for communicating with the client (also implements Runnable)
                ClientHandler clientHandler = new ClientHandler(socket);
//                thread for server connection (accepts clientHandler because it implements Runnable and should run multiple instances)
                Thread thread = new Thread(clientHandler);
//                start thread
                thread.start();
            }
        }
        catch (IOException exception) {
            closeServerSocket();
        }
    }

//    method to close socket after chat

    public void closeServerSocket(){
        try {
//            if serverSocket is not active (if the socket is not active, there is nothing to close)
            if(serverSocket != null){
                serverSocket.close();
            }
        }
        catch (IOException exception) {
//            exception.printStackTrace();
            System.out.println("Someone just left the chat");
        }
    }

    public static void main(String[] args) throws IOException {
//        new serverSocket with port number 1212
            ServerSocket serverSocket1 = new ServerSocket(1212);
//            new server object taking serverSocket as argument
            Server server = new Server(serverSocket1);
//            starting the server
            server.startServer();
    }
}
