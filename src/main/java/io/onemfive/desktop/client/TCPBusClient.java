package io.onemfive.desktop.client;

import ra.common.Client;
import ra.common.Envelope;
import ra.common.network.ControlCommand;
import ra.common.notification.Subscription;
import ra.util.Wait;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class TCPBusClient implements Runnable {

    private static final Logger LOG = Logger.getLogger(TCPBusClient.class.getName());

    private static final Integer MAX_CONNECT_ATTEMPTS = 30;

    private Integer currentConnectAttempts = 0;

    final String clientId;
    String serverId;

    private Socket socket = null;

    boolean initiatedComm = false;
    Client client;

    private InputStream readFromServer;
    private TCPBusClientReceiveThread tcpBusClientReceiveThread;

    private PrintWriter writeToServer;
    private TCPBusClientSendThread tcpBusClientSendThread;

    private boolean connected = false;
    private boolean shutdown = false;

    Map<String, Subscription> subscriptions = new HashMap<>();


    public TCPBusClient() {
        clientId = UUID.randomUUID().toString();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        init();
        while(!shutdown) {
            Wait.aSec(1);
        }
        LOG.info("Shutdown.");
    }

    public void init() {
        connected = false;
        currentConnectAttempts = 0;
        while(!connected) {
            try {
                connected = connect(2013);
            } catch (IOException e) {
                currentConnectAttempts++;
                if (currentConnectAttempts < MAX_CONNECT_ATTEMPTS) {
                    LOG.severe(e.getLocalizedMessage() + "; Wait 2 seconds and try again...");
                    Wait.aSec(2);
                } else {
                    LOG.severe(e.getLocalizedMessage() + "; Maximum attempts reached; exiting...");
                    return;
                }
            }
        }
        Envelope envelope = Envelope.documentFactory();
        envelope.setCommandPath(ControlCommand.InitiateComm.name());
        envelope.setClient(clientId);
        envelope.addNVP("initAttempt",1);
        sendMessage(envelope);
        while(!initiatedComm) {
            LOG.info("Not initiated. Waiting.");
            Wait.aSec(1);
        }
        LOG.info("Initiated Comm, running...");
    }

    public boolean isInitiated() {
        return initiatedComm;
    }

    public void shutdown(boolean reconnect) {
        tcpBusClientSendThread.shutdown();
        tcpBusClientReceiveThread.shutdown();
        if(reconnect) {
            init();
        } else {
            this.shutdown = true;
        }
    }

    public boolean connect(int port) throws IOException {
        // Connect to the localhost server socket at supplied port by a client with local address and port picked from randomly available options supporting multiple clients.
        socket = new Socket("localhost", port, null, 0);
        readFromServer = socket.getInputStream();
        writeToServer = new PrintWriter(socket.getOutputStream(), true);
        tcpBusClientReceiveThread = new TCPBusClientReceiveThread(this, readFromServer);
        tcpBusClientSendThread = new TCPBusClientSendThread(writeToServer);
        Thread receive = new Thread(tcpBusClientReceiveThread);
        Thread send = new Thread(tcpBusClientSendThread);
        receive.start();
        send.start();
        return true;
    }

    public void sendMessage(Envelope envelope) {
        envelope.setClient(clientId);
        tcpBusClientSendThread.sendMessage(envelope.toJSONRaw());
    }

    public void subscribe(Subscription subscription) {
        subscriptions.put(subscription.getEventMessageType().name(), subscription);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Envelope env = Envelope.documentFactory();
                // Tell TCP Server Socket to Send this message into bus
                env.setCommandPath(ControlCommand.Send.name());
                env.addNVP("EventMessageType", subscription.getEventMessageType().name());
                if(subscription.getFilter()!=null) {
                    env.addNVP("Filter", subscription.getFilter());
                }
                env.addNVP("ClientId", clientId);
                env.addNVP("Service", "TCPClient");
                env.addNVP("Operation", "Notify");
                // Destination is the Notification Service SUBSCRIBE operation
                env.addRoute("ra.notification.NotificationService", "SUBSCRIBE");
                env.ratchet();
                sendMessage(env);
            }
        }).start();
    }

    public static void main(String[] args) {
        TCPBusClient tcpBusClient = new TCPBusClient();
        tcpBusClient.init();
    }
}
