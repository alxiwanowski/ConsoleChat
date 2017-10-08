package com.edu.acme.message;

import com.edu.acme.Command;
import com.edu.acme.ServerState;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;

public class LoginMessage extends Message {
    private static final String USER_NAME_ALREADY_IN_USE_MESSAGE = "This username is already in use";

    private final Command command = Command.REGISTER;


    public LoginMessage(String userName, ObjectOutputStream out) {
        super(userName);
        this.out = out;
    }

    @Override
    public Command getCommand() {
        return command;
    }

    @Override
    public void process(ObjectOutputStream out) {
        if (ServerState.userExist(text)) {
            System.out.println(USER_NAME_ALREADY_IN_USE_MESSAGE);
            try {
                out.writeObject(new ServerMessage(USER_NAME_ALREADY_IN_USE_MESSAGE));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        ServerState.addNewUser(text);
        String prevUserName = ServerState.getUserStreamMap().get(out);

        if(out != null){
            handleNameChange(prevUserName);
        }

        ServerState.getUserStreamMap().replace(out, text);
    }

    private void handleNameChange(String prevUserName) {
        ServerState.getLoginSet().remove(prevUserName);
        try {
            for (ObjectOutputStream outputStream : ServerState.getClientOutList()) {
                outputStream.writeObject(new ServerMessage(
                        "user " + prevUserName + " changed name to " + text));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
