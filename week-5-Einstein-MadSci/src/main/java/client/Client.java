package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
//    socket to connect server and client
    private Socket socket;
//    buffered reader to receive messages
    private BufferedReader bufferedReader;
//    buffered writer to send messages
    private BufferedWriter bufferedWriter;
//    string username to identify users
    private String userName;

//    constructor to initialise fields
    public Client(Socket socket, String userName) {
        try {
//            socket of current object
            this.socket = socket;
//            buffered writer set to socket output stream for sending messages
            this.bufferedWriter = new BufferedWriter((new OutputStreamWriter(socket.getOutputStream())));
//            buffered reader set to socket input stream for reading messages
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            username of current user
            this.userName = userName;
        } catch (IOException exception){
            closeConnections(this.socket, bufferedReader, bufferedWriter);
        }
    }

//    sends message to client handler
    public void sendMsg() {
        try {
//            write username to client handler
            bufferedWriter.write(userName);
//            print new line
            bufferedWriter.newLine();
//            flush buffer
            bufferedWriter.flush();

//            create scanner tp receive input from user (console)
            Scanner input = new Scanner(System.in);
//            while the socket is connected, listen for and receive text to send to the chat
            while (socket.isConnected()){
//                receive input from user
                String messageToSend = input.nextLine();
//                write message received to socket input stream
                bufferedWriter.write(userName + ": " + messageToSend);
//                write new line
                bufferedWriter.newLine();
//                flush buffer
                bufferedWriter.flush();
            }
        } catch (IOException exception){
            closeConnections(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage(){
//        new anonymous thread for listening for messages
        new Thread(new Runnable() {
            @Override
            public void run() {
//                string message from chat storing message sent to chat
                String msgFromChat;
//                while socket is connected wait for and read message from user
                while(socket.isConnected()){
                    try {
                        msgFromChat = bufferedReader.readLine();
                        System.out.println(msgFromChat);
                    } catch (IOException exception){
                        closeConnections(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

//    method to close client connections
    public void closeConnections(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
//            if buffered reader is null, there is nothing to close
            if(bufferedReader != null){
                bufferedReader.close();
            }
//            if buffered writer is null, there is nothing to close
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
//            if socket is null, there is nothing to close
            if(socket != null){
                socket.close();
            }
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        new scanner to receive input from user
        Scanner input = new Scanner(System.in);
//        prompt for user name
        System.out.print("Choose a username for the group chat: ");
//        receive username
        String userName = input.nextLine();

//        create new socket to connect/communicate with the server
        Socket socket = null;
        try {
//            set host address (localhost in this case) and port number to connect to server process
            socket = new Socket("localhost", 1212);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        new client object taking socket and userName as arguments
        Client client = new Client(socket, userName);
//        listen for messages
        client.listenForMessage();
//        send messages
        client.sendMsg();
    }
}
