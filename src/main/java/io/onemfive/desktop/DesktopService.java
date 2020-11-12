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
import ra.common.DLC;
import ra.common.Envelope;
import ra.common.identity.DID;
import ra.common.messaging.EventMessage;
import ra.common.messaging.MessageProducer;
import ra.common.network.NetworkState;
import ra.common.notification.Subscription;
import ra.common.route.Route;
import ra.common.service.BaseService;
import ra.common.service.ServiceStatusListener;
import ra.notification.NotificationService;
import ra.notification.SubscriptionRequest;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class DesktopService extends BaseService {

    private static final Logger LOG = Logger.getLogger(DesktopService.class.getName());

    public static final String OPERATION_NOTIFY_UI = "NOTIFY_UI";

    public static final String OPERATION_UPDATE_ACTIVE_IDENTITY = "UPDATE_ACTIVE_IDENTITY";
    public static final String OPERATION_UPDATE_IDENTITIES = "UPDATE_IDENTITIES";

    private static DesktopService instance;

    public DesktopService() {
        instance = this;
    }

    public DesktopService(MessageProducer producer, ServiceStatusListener listener) {
        super(producer, listener);
        instance = this;
    }

    public static void deliver(Envelope e) {
        instance.producer.send(e);
    }

    @Override
    public void handleDocument(Envelope e) {
        handleAll(e);
    }

    @Override
    public void handleEvent(Envelope e) {
        handleAll(e);
    }

    @Override
    public void handleHeaders(Envelope e) {
        handleAll(e);
    }

    private void handleAll(Envelope e) {
        LOG.info("Received UI Service request...");
        Route route = e.getRoute();
        String operation = route.getOperation();
        switch (operation) {
            case OPERATION_UPDATE_ACTIVE_IDENTITY: {
                LOG.info("Update active identity request...");
                final DID activeIdentity = (DID) DLC.getEntity(e);
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
                final List<DID> identities = (List<DID>)DLC.getValue("identities", e);
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

    @Override
    public boolean start(Properties p) {
        if(!super.start(p)) {
            LOG.warning("DesktopService's parent failed to start.");
            return false;
        }
        MVC.registerManConStatusListener(() -> javafx.application.Platform.runLater(() -> {
            LOG.info("Updating ManCon status...");
            HomeView v = (HomeView)MVC.loadView(HomeView.class, true);
            v.updateManConBox();
        }));

        // 1M5 Network State Update
        Envelope e1M5Status = Envelope.documentFactory();
        SubscriptionRequest subscriptionRequest1M5Status = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "1M5",
                new Subscription() {
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
        DLC.addData(SubscriptionRequest.class, subscriptionRequest1M5Status, e1M5Status);
        DLC.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE, e1M5Status);
        BusClient.sendRequest(e1M5Status);

        // TOR Network State Update
        Envelope eTorStatus = Envelope.documentFactory();
        SubscriptionRequest subscriptionRequestTorStatus = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "Tor",
        new Subscription() {
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
        DLC.addData(SubscriptionRequest.class, subscriptionRequestTorStatus, eTorStatus);
        DLC.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE, eTorStatus);
        BusClient.sendRequest(eTorStatus);

        // I2P Network State Update
        Envelope eI2PStatus = Envelope.documentFactory();
        SubscriptionRequest subscriptionRequestI2PStatus = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "I2P",
                new Subscription() {
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
        DLC.addData(SubscriptionRequest.class, subscriptionRequestI2PStatus, eI2PStatus);
        DLC.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE, eI2PStatus);
        BusClient.sendRequest(eI2PStatus);

        // WiFi Direct Network State Update
        Envelope eWFDStatus = Envelope.documentFactory();
        SubscriptionRequest subscriptionRequestWFDStatus = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "WiFi",
                new Subscription() {
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
        DLC.addData(SubscriptionRequest.class, subscriptionRequestWFDStatus, eWFDStatus);
        DLC.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE, eWFDStatus);
        BusClient.sendRequest(eWFDStatus);

        // Bluetooth Network State Update
        Envelope eBTStatus = Envelope.documentFactory();
        SubscriptionRequest subscriptionRequestBTStatus = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "BT",
                new Subscription() {
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
        DLC.addData(SubscriptionRequest.class, subscriptionRequestBTStatus, eBTStatus);
        DLC.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE, eBTStatus);
        BusClient.sendRequest(eBTStatus);

        // Satellite Network State Update
        Envelope eSatStatus = Envelope.documentFactory();
        SubscriptionRequest subscriptionRequestSatStatus = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "Sat",
                new Subscription() {
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
        DLC.addData(SubscriptionRequest.class, subscriptionRequestSatStatus, eSatStatus);
        DLC.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE, eSatStatus);
        BusClient.sendRequest(eSatStatus);

        // Full Spectrum Radio Network State Update
        Envelope eFSRStatus = Envelope.documentFactory();
        SubscriptionRequest subscriptionRequestFSRStatus = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "Rad",
                new Subscription() {
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
        DLC.addData(SubscriptionRequest.class, subscriptionRequestFSRStatus, eFSRStatus);
        DLC.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE, eFSRStatus);
        BusClient.sendRequest(eFSRStatus);

        // LiFi Network State Update
        Envelope eLFStatus = Envelope.documentFactory();
        SubscriptionRequest subscriptionRequestLFStatus = new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, "LiFi",
                new Subscription() {
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
        DLC.addData(SubscriptionRequest.class, subscriptionRequestLFStatus, eLFStatus);
        DLC.addRoute(NotificationService.class, NotificationService.OPERATION_SUBSCRIBE, eLFStatus);
        BusClient.sendRequest(eLFStatus);

        return true;
    }
}
