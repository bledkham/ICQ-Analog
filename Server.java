package com.javarush.task.task30.task3008;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(ConsoleHelper.readInt());){
            ConsoleHelper.writeMessage("Server is start!");

            while (true){
                Handler handler = new Handler(serverSocket.accept());
                handler.start();
            }

        }catch (IOException e){
            ConsoleHelper.writeMessage("Something goes wrong...");
            e.printStackTrace();
        }
        
    }
    
    public static void sendBroadcastMessage(Message message){
        for(Map.Entry<String, Connection> connection : connectionMap.entrySet()){
            try {
                connection.getValue().send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Message send is failed!");
                e.printStackTrace();
            }
        }
    }

    private static class Handler extends Thread{
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run(){
            ConsoleHelper.writeMessage(String.valueOf(socket.getRemoteSocketAddress()));
            String userName = null;
            try (Connection connection = new Connection(socket);){
                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
                ConsoleHelper.writeMessage("Connection was closed");
            }catch (ClassNotFoundException ignore){
                ConsoleHelper.writeMessage("Connection error with remote socket address");
            } catch (IOException ignore) {
                ConsoleHelper.writeMessage("Connection error with remote socket address");
            }

        }

        private String serverHandshake(Connection connection) throws IOException,ClassNotFoundException {
            connection.send(new Message(MessageType.NAME_REQUEST));
            Message message = connection.receive();
            String name = message.getData();

                if(message.getType().equals(MessageType.USER_NAME) && name != "" && !connectionMap.containsKey(name)){
                        connectionMap.put(message.getData(), connection);
                        connection.send(new Message(MessageType.NAME_ACCEPTED));
                        return name;
                }
                else return serverHandshake(connection);
        }

        private void notifyUsers(Connection connection, String userName) throws IOException{
            connectionMap.forEach((k,v)->{
                try {
                    if (!k.equals(userName)) connection.send(new Message(MessageType.USER_ADDED, k));
                } catch (IOException e) {

                }
            });
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException{
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + message.getData()));
                } else {
                    ConsoleHelper.writeMessage("Something goes wrong");
                }
            }

        }
    }
}


