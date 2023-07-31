package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
//    array list of client handlers to keep track of all clients so that they can all receive messages
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
//    socket to establish connection between client and server
    private Socket socket;
//    buffered reader to read data (receive messages) from the clients
    private BufferedReader bufferedReader;
//    buffered writer to write data (send messages) to other clients
    private BufferedWriter bufferedWriter;
//    username of each client
    private String clientUserName;
//    constructor to initialise fields
    public ClientHandler(Socket socket) {
        try {
//            socket of the current object to facilitate connection
            this.socket = socket;
//            buffered writer set to sockets output stream to send messages
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            buffered reader set to sockets input stream to receive messages
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            set username to first input from user with buffered reader
            this.clientUserName = bufferedReader.readLine();
//            add current client object to client handlers array list so the client can send and receive messages
            clientHandlers.add(this);
            broadcastMessageFromServer(clientUserName + " just joined the chat!");
            broadcastMessageFromServer("Welcome " + clientUserName + " to the chat, guys!");

        } catch (IOException exception) {
            closeConnections(socket, bufferedReader, bufferedWriter);
        }
    }

    public void run() {
//         string to store client message
        String clientMsg;

//        while the client message is still connected
        while(socket.isConnected()){
            try{
//                listen for message
                clientMsg = bufferedReader.readLine();
                if(clientMsg == null){
                    clientExits();
                    break;
                }
                broadcastMessageFromServer(clientMsg);
            } catch (IOException exception){
                closeConnections(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

//    method to send messages to group chat
    public void broadcastMessageFromServer(String msgToSend){
//        looping through client handlers array list
        for (ClientHandler clientHandler :  clientHandlers){
            try{

// The broadcast method sends messages to everyone except the sender so below, if the client username is not equal
// to the current client username (i.e the sender), write the message to send via buffered writer, write a new line
// via buffered writer and flush the buffered writer to empty the buffered writer in case the output stream is not full

                if(!clientHandler.clientUserName.equals(clientUserName)){
                    clientHandler.bufferedWriter.write(msgToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException exception){
                closeConnections(socket, bufferedReader, bufferedWriter);
            }
        }
    }

//    method to show when clients leave chat
    public void clientExits(){
//        removes current object from client handlers array
        clientHandlers.remove(this);
        broadcastMessageFromServer(clientUserName + " has left the chat!");
    }

    public void closeConnections(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
//        call client exits method to remove the client from the client handlers list
        clientExits();
        try {
//            if buffered reader is active, close it (if buffered reader is null, there is nothing to close)
            if(bufferedReader != null){
                bufferedReader.close();
            }
//            if buffered writer is active, close it (if buffered writer is null, there is nothing to close)
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
//            if socket is active, close it (if socket is null, there is nothing to close)
            if(socket != null){
                socket.close();
            }
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }
}