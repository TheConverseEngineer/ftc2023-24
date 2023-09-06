package com.example.opensimclient;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/** Serves as a socket client for a single port */
public final class SocketClient {
    private final Socket client;
    private final BufferedOutputStream output;
    private final DataInputStream input;

    /** Constructor for the SocketClient class
     *
     * @param ip        the socket's ip address (use "127.0.0.1" for localhost)
     * @param port      the port to use
     * */
    public SocketClient(String ip, int port) {
        try {
            client = new Socket(ip, port);
            output = new BufferedOutputStream(client.getOutputStream());
            input = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Error creating socket!: " + e.getMessage());
        }

    }

    public void receiveMessage(byte[] msg) {
        this.sendMessage(msg);
    }

    public byte[] receiveMessageWithReply(byte[] msg) {
        this.sendMessage(msg);
        byte[] response = new byte[4];
        try {
            input.readFully(response);
        } catch (IOException e) {
            throw new RuntimeException("Error reading input!: " + e.getMessage());
        }
        return response;
    }

    private void sendMessage(byte[] msg) {
        System.out.println("Sending " + Arrays.toString(msg));
        try {
            output.write(msg);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error when sending message!: " + e.getMessage());
        }
    }

    public void stopConnection() {
        try {
            input.close();
            output.close();
            client.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing socket!: " + e.getMessage());
        }
    }
}
