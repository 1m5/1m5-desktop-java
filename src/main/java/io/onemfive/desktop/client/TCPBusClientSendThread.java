package io.onemfive.desktop.client;

import ra.util.Wait;

import java.io.PrintWriter;
import java.util.logging.Logger;

public class TCPBusClientSendThread implements Runnable {

    private static Logger LOG = Logger.getLogger(TCPBusClientSendThread.class.getName());

    private volatile String message;
    private final PrintWriter writeToServer;
    private boolean running = false;

    public TCPBusClientSendThread(PrintWriter writeToServer) {
        this.writeToServer = writeToServer;
    }

    public void sendMessage(String msg) {
        message = msg;
    }

    public void shutdown() {
        running = false;
    }

    @Override
    public void run() {
        running = true;
        while(running) {
            if(message!=null && !message.isEmpty()) {
                LOG.info("Sending message to server...");
                writeToServer.println(message);
                writeToServer.flush();
                if (message.equalsIgnoreCase("exit")) {
                    running = false;
                }
                message = null;
            }
            Wait.aMs(100);
        }
    }
}
