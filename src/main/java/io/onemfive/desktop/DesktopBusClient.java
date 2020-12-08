package io.onemfive.desktop;

import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.home.HomeView;
import io.onemfive.desktop.views.ops.network.bluetooth.BluetoothOpsView;
import io.onemfive.desktop.views.ops.network.fullspectrum.FullSpectrumRadioOpsView;
import io.onemfive.desktop.views.ops.network.i2p.I2POpsView;
import io.onemfive.desktop.views.ops.network.lifi.LiFiOpsView;
import io.onemfive.desktop.views.ops.network.satellite.SatelliteOpsView;
import io.onemfive.desktop.views.ops.network.tor.TOROpsView;
import io.onemfive.desktop.views.ops.network.wifidirect.WifiDirectOpsView;
import io.onemfive.desktop.views.personal.identities.IdentitiesView;
import io.onemfive.desktop.views.settings.network.bluetooth.BluetoothSensorSettingsView;
import io.onemfive.desktop.views.settings.network.fullspectrum.FullSpectrumRadioSensorSettingsView;
import io.onemfive.desktop.views.settings.network.i2p.I2PSensorSettingsView;
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
import ra.common.notification.ClientSubscription;
import ra.common.notification.SubscriptionRequest;
import ra.common.route.Route;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class DesktopBusClient implements Client {

    private static final Logger LOG = Logger.getLogger(DesktopBusClient.class.getName());

    public static final String OPERATION_NOTIFY_UI = "NOTIFY_UI";

    public static final String OPERATION_UPDATE_ACTIVE_IDENTITY = "UPDATE_ACTIVE_IDENTITY";
    public static final String OPERATION_UPDATE_IDENTITIES = "UPDATE_IDENTITIES";
    public static final String OPERATION_UPDATE_SERVICE_STATE = "UPDATE_SERVICE_STATE";

    private static TCPBusClient busClient;

    public DesktopBusClient(TCPBusClient tcpBusClient) {
        busClient = tcpBusClient;
        busClient.setClient(this);
    }

    public static void registerService(Class serviceClass) {
        Envelope e = Envelope.documentFactory();
        e.setCommandPath(ControlCommand.RegisterService.name());
        e.addNVP("serviceClass", serviceClass.getName());
        deliver(e);
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
            case OPERATION_UPDATE_SERVICE_STATE: {
                LOG.warning("Update service state handling not yet supported in UI.");
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

        busClient.subscribe(new SubscriptionRequest(EventMessage.Type.NETWORK_STATE_UPDATE, new ClientSubscription() {
            @Override
            public void reply(Envelope e) {
                javafx.application.Platform.runLater(() -> {
                    LOG.info("Updating UI with Network State...");
                    EventMessage em = (EventMessage)e.getMessage();
                    NetworkState state = (NetworkState)em.getMessage();
                    switch(state.network) {
                        case LiFi: {
                            ((TopicListener) MVC.loadView(LiFiOpsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                            ((TopicListener) MVC.loadView(LiFiSensorSettingsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                            break;
                        }
                        case Tor: {
                            ((TopicListener) MVC.loadView(TOROpsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                            ((TopicListener) MVC.loadView(TORSensorSettingsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                            break;
                        }
                        case I2P: {
                            ((TopicListener) MVC.loadView(I2POpsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                            ((TopicListener) MVC.loadView(I2PSensorSettingsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                            break;
                        }
                        case Bluetooth: {
                            ((TopicListener) MVC.loadView(BluetoothOpsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                            ((TopicListener) MVC.loadView(BluetoothSensorSettingsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                            break;
                        }
                        case WiFi: {
                            ((TopicListener) MVC.loadView(WifiDirectOpsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                            ((TopicListener) MVC.loadView(WifiDirectSensorSettingsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                            break;
                        }
                        case Satellite: {
                            ((TopicListener) MVC.loadView(SatelliteOpsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                            ((TopicListener) MVC.loadView(SatelliteSensorSettingsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                            break;
                        }
                        case FSRadio: {
                            ((TopicListener) MVC.loadView(FullSpectrumRadioOpsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                            ((TopicListener) MVC.loadView(FullSpectrumRadioSensorSettingsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                            break;
                        }
                    }
                });
            }
        }));

        return true;
    }

}
