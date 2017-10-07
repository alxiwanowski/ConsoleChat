package com.edu.acme;

import com.edu.acme.exception.InvalidMessageException;

import java.io.*;
import java.net.Socket;

public class ClientApp {
    private static final int PORT = 9999;
    private static Validator messageValidator = new MessageValidator();
    public static void main(String[] args) {
        try (
            Socket socket = new Socket("localhost", PORT);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(
                    new BufferedOutputStream(
                            socket.getOutputStream())
            );

            ObjectInputStream messagesReader = new ObjectInputStream(
                    new BufferedInputStream(
                            socket.getInputStream())
            )
        ) {
            new Thread(() -> {
                readMessageLoop(messagesReader);
            }).start();

            while (true) {
                String message = consoleReader.readLine();
                try {
                    messageValidator.validate(message);
                    //TODO: вынести split в отдельный метод
                    out.println(message.split("\\s+", 2)[1]);
                    out.flush();
                }catch (InvalidMessageException e) {
                    System.err.println(e.getMessage());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readMessageLoop(ObjectInputStream messagesReader) {
        try {
            while (true){
                System.out.println(messagesReader.readObject().toString());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}