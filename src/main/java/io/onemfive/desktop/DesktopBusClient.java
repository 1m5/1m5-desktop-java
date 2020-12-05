package io.onemfive.desktop;

import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.home.HomeView;
import io.onemfive.desktop.views.ops.network.bluetooth.BluetoothOpsView;
import io.onemfive.desktop.views.ops.network.fullspectrum.FullSpectrumRadioOpsView;
import io.onemfive.desktop.views.ops.network.i2p.I2POpsView;
import io.onemfive.desktop.views.ops.network.idn.IDNView;
import io.onemfive.desktop.views.ops.network.lifi.LiFiOpsView;
import io.onemfive.desktop.views.ops.network.satellite.SatelliteOpsView;
import io.onemfive.desktop.views.ops.network.tor.TOROpsView;
import io.onemfive.desktop.views.ops.network.wifidirect.WifiDirectOpsView;
import io.onemfive.desktop.views.personal.identities.IdentitiesView;
import io.onemfive.desktop.views.settings.network.bluetooth.BluetoothSensorSettingsView;
import io.onemfive.desktop.views.settings.network.fullspectrum.FullSpectrumRadioSensorSettingsView;
import io.onemfive.desktop.views.settings.network.i2p.I2PSensorSettingsView;
import io.onemfive.desktop.views.settings.network.ims.IMSSettingsView;
import io.onemfive.desktop.views.settings.network.lifi.LiFiSensorSettingsView;
import io.onemfive.desktop.views.settings.network.satellite.SatelliteSensorSettingsView;
import io.onemfive.desktop.views.settings.network.tor.TORSensorSettingsView;
import io.onemfive.desktop.views.settings.network.wifidirect.WifiDirectSensorSettingsView;
import ra.common.Client;
import ra.common.DLC;
import ra.common.Envelope;
import ra.common.client.TCPBusClient;
import ra.common.identity.DID;
import ra.common.messaging.EventMessage;
import ra.common.network.ControlCommand;
import ra.common.network.NetworkState;
import ra.common.notification.Subscription;
import ra.common.route.Route;
import ra.i2p.I2PService;
import ra.notification.NotificationService;
import ra.notification.SubscriptionRequest;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class DesktopBusClient implements Client {

    private static final Logger LOG = Logger.getLogger(DesktopBusClient.class.getName());

    public static final String OPERATION_NOTIFY_UI = "NOTIFY_UI";

    public static final String OPERATION_UPDATE_ACTIVE_IDENTITY = "UPDATE_ACTIVE_IDENTITY";
    public static final String OPERATION_UPDATE_IDENTITIES = "UPDATE_IDENTITIES";

    private static TCPBusClient busClient;

    public DesktopBusClient(TCPBusClient tcpBusClient) {
        busClient = tcpBusClient;
        busClient.setClient(this);
    }

    public static void startService(Class serviceClass) {
        Envelope e = Envelope.documentFactory();
        e.setCommandPath(ControlCommand.StartService.name());
        e.addNVP("serviceClass", serviceClass.getName());
        deliver(e);
    }

    public static void shutdownService(Class serviceClass, boolean hardStop) {
        Envelope e = Envelope.documentFactory();
        if(hardStop) {
            e.setCommandPath(ControlCommand.StopService.name());
        } else {
            e.setCommandPath(ControlCommand.GracefullyStopService.name());
        }
        e.addNVP("serviceClass", serviceClass.getName());
        deliver(e);
    }

    public static void deliver(Envelope e) {
        MVC.execute(new Runnable() {
            @Override
            public void run() {
                busClient.sendMessage(e);
            }
        });
    }

    @Override
    public void reply(Envelope envelope) {
        LOG.info("Received message for UI...");
        Route route = envelope.getRoute();
        String operation = route.getOperation();
        switch (operation) {
            case OPERATION_UPDATE_ACTIVE_IDENTITY: {
                LOG.info("Update active identity request...");
                final DID activeIdentity = (DID) DLC.getEntity(envelope);
                if(activeIdentity!=null) {
                    javafx.application.Platform.runLater(() -> {
                        LOG.info("Updating IdentitiesView active DID...");
                        IdentitiesView v = (IdentitiesView)MVC.loadView(IdentitiesView.class, true);
                        v.updateActiveDID(activeIdentity);
                    });
                }
                break;
            }
            case OPERATION_UPDATE_IDENTITIES: {
                LOG.info("Update identities request...");
                final List<DID> identities = (List<DID>)DLC.getValue("identities", envelope);
                if(identities!=null) {
                    javafx.application.Platform.runLater(() -> {
                        LOG.info("Updating IdentitiesView identities...");
                        IdentitiesView v = (IdentitiesView)MVC.loadView(IdentitiesView.class, true);
                        v.updateIdentities(identities);
                    });
                }
                break;
            }
            case OPERATION_NOTIFY_UI: {
                LOG.warning("UI Notifications not yet implemented.");
                break;
            }
            default: {
                LOG.warning("Operation unsupported: " + operation);
            }
        }
    }

    public boolean start(Properties p) {
        LOG.info("Starting Desktop Bus Client...");
        MVC.registerManConStatusListener(() -> javafx.application.Platform.runLater(() -> {
            LOG.info("Updating ManCon status...");
            HomeView v = (HomeView)MVC.loadView(HomeView.class, true);
            v.updateManConBox();
        }));

        // 1M5 Network State Update
        Envelope e1M5Status = Envelope.documentFactory();
        e1M5Status.setCommandPath(ControlCommand.Send.name());
        SubscriptionRequest subscriptionRequest1M5Status = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "1M5", new Subscription() {
                    @Override
                    public void notifyOfEvent(Envelope e) {
                        javafx.application.Platform.runLater(() -> {
                            LOG.info("Updating UI with 1M5 Network State...");
                            EventMessage em = (EventMessage)e.getMessage();
                            NetworkState state = (NetworkState)em.getMessage();
                            TopicListener listener = (TopicListener)MVC.loadView(IDNView.class, true);
                            listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                            listener = (TopicListener)MVC.loadView(IMSSettingsView.class, true);
                            listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                        });
                    }
                });
        e1M5Status.addData(SubscriptionRequest.class, subscriptionRequest1M5Status);
        e1M5Status.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE);
        busClient.sendMessage(e1M5Status);

        // TOR Network State Update
        Envelope eTorStatus = Envelope.documentFactory();
        eTorStatus.setCommandPath(ControlCommand.Send.name());
        SubscriptionRequest subscriptionRequestTorStatus = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "Tor", new Subscription() {
            @Override
            public void notifyOfEvent(Envelope e) {
                javafx.application.Platform.runLater(() -> {
                    LOG.info("Updating UI with TOR Network State...");
                    EventMessage em = (EventMessage)e.getMessage();
                    NetworkState state = (NetworkState)em.getMessage();
                    TopicListener listener = (TopicListener)MVC.loadView(TOROpsView.class, true);
                    listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                    listener = (TopicListener)MVC.loadView(TORSensorSettingsView.class, true);
                    listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                });
            }
        });
        eTorStatus.addData(SubscriptionRequest.class, subscriptionRequestTorStatus);
        eTorStatus.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE);
        busClient.sendMessage(eTorStatus);

        // I2P Network State Update
        Envelope eI2PStatus = Envelope.documentFactory();
        eI2PStatus.setCommandPath(ControlCommand.Send.name());
        SubscriptionRequest subscriptionRequestI2PStatus = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "I2P", new Subscription() {
                    @Override
                    public void notifyOfEvent(Envelope e) {
                        javafx.application.Platform.runLater(() -> {
                            LOG.info("Updating UI with I2P Network State...");
                            EventMessage em = (EventMessage)e.getMessage();
                            NetworkState state = (NetworkState)em.getMessage();
                            TopicListener listener = (TopicListener)MVC.loadView(I2POpsView.class, true);
                            listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                            listener = (TopicListener)MVC.loadView(I2PSensorSettingsView.class, true);
                            listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                        });
                    }
                });
        eI2PStatus.addData(SubscriptionRequest.class, subscriptionRequestI2PStatus);
        eI2PStatus.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE);
        busClient.sendMessage(eI2PStatus);

        // WiFi Direct Network State Update
        Envelope eWFDStatus = Envelope.documentFactory();
        eWFDStatus.setCommandPath(ControlCommand.Send.name());
        SubscriptionRequest subscriptionRequestWFDStatus = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "WiFi", new Subscription() {
                    @Override
                    public void notifyOfEvent(Envelope e) {
                        javafx.application.Platform.runLater(() -> {
                            LOG.info("Updating UI with WiFi-Direct Network State...");
                            EventMessage em = (EventMessage)e.getMessage();
                            NetworkState state = (NetworkState)em.getMessage();
                            TopicListener listener = (TopicListener)MVC.loadView(WifiDirectOpsView.class, true);
                            listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                            listener = (TopicListener)MVC.loadView(WifiDirectSensorSettingsView.class, true);
                            listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                        });
                    }
                });
        eWFDStatus.addData(SubscriptionRequest.class, subscriptionRequestWFDStatus);
        eWFDStatus.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE);
        busClient.sendMessage(eWFDStatus);

        // Bluetooth Network State Update
        Envelope eBTStatus = Envelope.documentFactory();
        eBTStatus.setCommandPath(ControlCommand.Send.name());
        SubscriptionRequest subscriptionRequestBTStatus = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "BT", new Subscription() {
                    @Override
                    public void notifyOfEvent(Envelope e) {
                        javafx.application.Platform.runLater(() -> {
                            LOG.info("Updating UI with Bluetooth Network State...");
                            EventMessage em = (EventMessage)e.getMessage();
                            NetworkState state = (NetworkState)em.getMessage();
                            TopicListener listener = (TopicListener)MVC.loadView(BluetoothOpsView.class, true);
                            listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                            listener = (TopicListener)MVC.loadView(BluetoothSensorSettingsView.class, true);
                            listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                        });
                    }
                });
        eBTStatus.addData(SubscriptionRequest.class, subscriptionRequestBTStatus);
        eBTStatus.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE);
        busClient.sendMessage(eBTStatus);

        // Satellite Network State Update
        Envelope eSatStatus = Envelope.documentFactory();
        eSatStatus.setCommandPath(ControlCommand.Send.name());
        SubscriptionRequest subscriptionRequestSatStatus = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "Sat", new Subscription() {
                    @Override
                    public void notifyOfEvent(Envelope e) {
                        javafx.application.Platform.runLater(() -> {
                            LOG.info("Updating UI with Satellite Network State...");
                            EventMessage em = (EventMessage)e.getMessage();
                            NetworkState state = (NetworkState)em.getMessage();
                            TopicListener listener = (TopicListener)MVC.loadView(SatelliteOpsView.class, true);
                            listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                            listener = (TopicListener)MVC.loadView(SatelliteSensorSettingsView.class, true);
                            listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                        });
                    }
                });
        eSatStatus.addData(SubscriptionRequest.class, subscriptionRequestSatStatus);
        eSatStatus.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE);
        busClient.sendMessage(eSatStatus);

        // Full Spectrum Radio Network State Update
        Envelope eFSRStatus = Envelope.documentFactory();
        eFSRStatus.setCommandPath(ControlCommand.Send.name());
        SubscriptionRequest subscriptionRequestFSRStatus = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "Rad", new Subscription() {
                    @Override
                    public void notifyOfEvent(Envelope e) {
                        javafx.application.Platform.runLater(() -> {
                            LOG.info("Updating UI with Full Spectrum Radio Network State...");
                            EventMessage em = (EventMessage)e.getMessage();
                            NetworkState state = (NetworkState)em.getMessage();
                            TopicListener listener = (TopicListener)MVC.loadView(FullSpectrumRadioOpsView.class, true);
                            listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                            listener = (TopicListener)MVC.loadView(FullSpectrumRadioSensorSettingsView.class, true);
                            listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                        });
                    }
                });
        eFSRStatus.addData(SubscriptionRequest.class, subscriptionRequestFSRStatus);
        eFSRStatus.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE);
        busClient.sendMessage(eFSRStatus);

        // LiFi Network State Update
        Envelope eLFStatus = Envelope.documentFactory();
        eLFStatus.setCommandPath(ControlCommand.Send.name());
        SubscriptionRequest subscriptionRequestLFStatus = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "LiFi", new Subscription() {
                    @Override
                    public void notifyOfEvent(Envelope e) {
                        javafx.application.Platform.runLater(() -> {
                            LOG.info("Updating UI with LiFi Network State...");
                            EventMessage em = (EventMessage)e.getMessage();
                            NetworkState state = (NetworkState)em.getMessage();
                            TopicListener listener = (TopicListener)MVC.loadView(LiFiOpsView.class, true);
                            listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                            listener = (TopicListener)MVC.loadView(LiFiSensorSettingsView.class, true);
                            listener.modelUpdated(NetworkState.class.getSimpleName(), state);
                        });
                    }
                });
        eLFStatus.addData(SubscriptionRequest.class, subscriptionRequestLFStatus);
        eLFStatus.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE);
        busClient.sendMessage(eLFStatus);

        return true;
    }
}
