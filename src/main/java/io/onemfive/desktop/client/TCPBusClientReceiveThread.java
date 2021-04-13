package io.onemfive.desktop.client;

import ra.common.Envelope;
import ra.common.messaging.EventMessage;
import ra.common.network.ControlCommand;
import ra.common.notification.Subscription;
import ra.util.Wait;

import java.io.*;
import java.util.logging.Logger;

public class TCPBusClientReceiveThread implements Runnable {

    private static Logger LOG = Logger.getLogger(TCPBusClientReceiveThread.class.getName());

    private TCPBusClient tcpBusClient;
    private InputStream readFromServer = null;
    private boolean running = false;

    public TCPBusClientReceiveThread(TCPBusClient tcpBusClient, InputStream readFromServer) {
        this.tcpBusClient = tcpBusClient;
        this.readFromServer = readFromServer;
    }

    public void shutdown() {
        running = false;
    }

    public void run() {
        running = true;
        try {
            while(running) {
                LOG.info("Waiting for server message...");
                BufferedReader r = new BufferedReader(new InputStreamReader(readFromServer));
                String msg = null;
                try {
                    msg = r.readLine();
                } catch (IOException e) {
                    LOG.warning(e.getLocalizedMessage());
                }
                if(msg==null || msg.isEmpty()) {
                    LOG.info("Server likely shutdown. Shutting down client with re-connect attempt...");
                    tcpBusClient.shutdown(true);
                    continue;
                }
                if(msg.equalsIgnoreCase("exit")) {
                    LOG.info("Server notifying client of shutdown. Shutting down client...");
                    tcpBusClient.shutdown(false);
                    continue;
                }
                Envelope env = null;
                if(msg.contains(EventMessage.class.getName())) {
                    if(msg.contains(EventMessage.Type.NETWORK_STATE_UPDATE.name())) {
                        env = Envelope.eventFactory(EventMessage.Type.NETWORK_STATE_UPDATE);
                        env.fromJSON(msg);
                    } else if(msg.contains(EventMessage.Type.SERVICE_STATUS.name())) {
                        env = Envelope.eventFactory(EventMessage.Type.SERVICE_STATUS);
                        env.fromJSON(msg);
                    } else if(msg.contains(EventMessage.Type.DID_STATUS.name())) {
                        env = Envelope.eventFactory(EventMessage.Type.DID_STATUS);
                        env.fromJSON(msg);
                    } else if(msg.contains(EventMessage.Type.PEER_STATUS.name())) {
                        env = Envelope.eventFactory(EventMessage.Type.PEER_STATUS);
                        env.fromJSON(msg);
                    } else if(msg.contains(EventMessage.Type.BUS_STATUS.name())) {
                        env = Envelope.eventFactory(EventMessage.Type.BUS_STATUS);
                        env.fromJSON(msg);
                    }
                } else if(msg.contains("ra.common.messaging.DocumentMessage")) {
                    env = Envelope.documentFactory();
                    env.fromJSON(msg);
                }
                if(env==null) {
                    LOG.warning("Unable to determine envelope structure received.");
                    return;
                }
                LOG.info("Received Envelope...");
                ControlCommand cc = ControlCommand.valueOf(env.getCommandPath());
                LOG.info("ControlCommand: "+env.getCommandPath());
                switch (cc) {
                    case InitiateComm: {
                        tcpBusClient.initiatedComm = env.getValue("init")!=null && "true".equals(env.getValue("init"));
                        if(tcpBusClient.initiatedComm) {
                            tcpBusClient.serverId = env.getClient();
                        } else {
                            int initCount = (Integer)env.getValue("initAttempt");
                            if(initCount>60) {
                                LOG.warning("Unable to initiate communications with Bus within 60 seconds, exiting.");
                                running = false;
                                break;
                            }
                            env.addNVP("initAttempt", initCount + 1);
                            Wait.aSec(1);
                            tcpBusClient.sendMessage(env);
                        }
                        break;
                    }
                    case Notify: {
                        EventMessage em = (EventMessage) env.getMessage();
                        Subscription sub = tcpBusClient.subscriptions.get(em.getType());
                        if(sub!=null && sub.getClient()!=null) {
                            sub.getClient().reply(env);
                        }
                        break;
                    }
                    case CloseClient: {
                        // Server telling client to shutdown
                        tcpBusClient.shutdown(false);
                        break;
                    }
                    default: {
                        tcpBusClient.client.reply(env);
                    }
                }
            }
        } catch(Exception e) {
            LOG.warning(e.getLocalizedMessage());
        }
    }

}
