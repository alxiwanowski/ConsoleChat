package com.edu.acme;

import com.edu.acme.exception.InvalidMessageException;

import java.io.*;
import java.net.Socket;

public class Client {
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
                messageValidator.validate(message);
                //TODO: вынести split в отдельный метод
                out.println(message.split("\\s+", 2)[1]);
                out.flush();
            }

        } catch (IOException | InvalidMessageException e) {
            e.printStackTrace();
        }
    }

    private static void readMessageLoop(ObjectInputStream messagesReader) {
        try {
            System.out.println(messagesReader.readObject().toString());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}