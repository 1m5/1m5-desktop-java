package io.onemfive.desktop;

import io.onemfive.desktop.views.TopicListener;
import io.onemfive.desktop.views.View;
import io.onemfive.desktop.views.home.HomeView;
import io.onemfive.desktop.views.ops.network.bluetooth.BluetoothOpsView;
import io.onemfive.desktop.views.ops.network.fullspectrum.FullSpectrumRadioOpsView;
import io.onemfive.desktop.views.ops.network.i2p.I2POpsView;
import io.onemfive.desktop.views.ops.network.lifi.LiFiOpsView;
import io.onemfive.desktop.views.ops.network.satellite.SatelliteOpsView;
import io.onemfive.desktop.views.ops.network.tor.TOROpsView;
import io.onemfive.desktop.views.ops.network.wifidirect.WifiDirectOpsView;
import io.onemfive.desktop.views.settings.network.bluetooth.BluetoothNetworkSettingsView;
import io.onemfive.desktop.views.settings.network.fullspectrum.FullSpectrumRadioNetworkSettingsView;
import io.onemfive.desktop.views.settings.network.i2p.I2PNetworkSettingsView;
import io.onemfive.desktop.views.settings.network.lifi.LiFiNetworkSettingsView;
import io.onemfive.desktop.views.settings.network.satellite.SatelliteNetworkSettingsView;
import io.onemfive.desktop.views.settings.network.tor.TORNetworkSettingsView;
import io.onemfive.desktop.views.settings.network.wifidirect.WiFiNetworkSettingsView;
import okhttp3.*;
import onemfive.ManCon;
import onemfive.ManConStatus;
import ra.btc.BTCWallet;
import ra.btc.RPCCommand;
import ra.btc.rpc.RPCResponse;
import ra.common.Client;
import ra.common.Envelope;
import ra.common.file.Multipart;
import ra.common.identity.DID;
import ra.common.messaging.EventMessage;
import ra.common.messaging.Message;
import ra.common.network.*;
import ra.common.notification.Subscription;
import ra.common.service.ServiceReport;
import ra.maildrop.MailDropService;
import ra.notification.NotificationService;
import ra.util.JSONParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DesktopClient implements Client {

    private static final Logger LOG = Logger.getLogger(DesktopClient.class.getName());

    public static final String VIEW_NAME = "VIEW_NAME";
    public static final String VIEW_OP = "VIEW_OP";

    public static final String OPERATION_NOTIFY_UI = "NOTIFY_UI";

    // System
    private final Map<String,ServiceReport> serviceReports = new HashMap<>();
    private final Map<String,NetworkState> networkStates = new HashMap<>();
    private final Map<String, List<Subscription>> subscriptions = new HashMap<>();
    private NetworkStatus localhostStatus;
    private final Map<String,Object> globals = new HashMap<>();
    private ConnectionSpec httpSpec;
    private OkHttpClient httpClient;
    private final int apiPort;

    // Personal
    private BTCWallet activeWallet;
    private final List<String> transactions = new ArrayList<>();
    private final Map<String,DID> localIdentities = new HashMap<>();
    private DID activeIdentity;

    // Community
    private BTCWallet activeCommunityWallet;
    private final List<String> communityTransactions = new ArrayList<>();
    private final Map<String,DID> communityIdentities = new HashMap<>();
    private DID activeCommunityIdentity;

    // Public
    private BTCWallet publicCharityWallet;
    private final List<String> charitableTransactions = new ArrayList<>();
    private final Map<String,DID> publicIdentities = new HashMap<>();
    private DID activePublicIdentity;

    private static DesktopClient instance;

    private DesktopClient(int apiPort) {
        this.apiPort = apiPort;
        activeIdentity = new DID();
        activeIdentity.setUsername("ANONYMOUS");
    }

    public static DesktopClient getInstance(int apiPort) {
        if(instance == null) {
            instance = new DesktopClient(apiPort);
        }
        return instance;
    }

    public DID getActiveIdentity() {
        return activeIdentity;
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

    /**
     * Deliver Envelope to Bus as-is.
     * @param e
     */
    public static void deliver(Envelope e) {
        MVC.execute(new Runnable() {
            @Override
            public void run() {
                instance.sendMessage(e);
            }
        });
    }

    public static RPCResponse getResponse(Envelope e) {
        RPCResponse response = new RPCResponse();
        Object responseObj = e.getValue(RPCCommand.RESPONSE);
        if(responseObj instanceof String) {
            response.fromJSON((String)responseObj);
        } else if(responseObj instanceof Map) {
            response.fromMap((Map<String, Object>) responseObj);
        }
        return response;
    }

    public static void setGlobal(String name, Object value) {
        instance.globals.put(name, value);
    }

    public static Object getGlobal(String name) {
        return instance.globals.get(name);
    }

    public static void addBitcoinTransaction(String txid) {
        instance.transactions.add(txid);
    }

    public static List<String> getBitcoinTransactions() {
        return instance.transactions;
    }

    public static void setActiveWallet(BTCWallet activeWallet) {
        instance.activeWallet = activeWallet;
    }

    public static BTCWallet getActiveWallet() {
        return instance.activeWallet;
    }

    @Override
    public void reply(Envelope e) {
        String viewName = (String)e.getValue(VIEW_NAME);
        String viewOp = (String)e.getValue(VIEW_OP);
        if(viewName==null) {
            LOG.info("No view name provided, ignoring.");
            return;
        }
        LOG.info("Received message for UI: view="+viewName+"; op="+viewOp);
        View view = MVC.loadView(viewName);
        if(view instanceof TopicListener) {
            javafx.application.Platform.runLater(() -> {
                LOG.info("Updating view model...");
                ((TopicListener)view).modelUpdated(viewOp, e);
            });
        } else {
            LOG.warning(view.getClass().getName()+" must implement "+TopicListener.class.getName());
        }
    }

    public boolean start(Properties p) {
        LOG.info("Starting Desktop Bus Client...");

        MVC.registerManConStatusListener(() -> javafx.application.Platform.runLater(() -> {
            LOG.info("Updating ManCon status...");
            HomeView v = (HomeView)MVC.loadView(HomeView.class, true);
            v.updateManConBox();
        }));

        httpSpec = new ConnectionSpec
                .Builder(ConnectionSpec.CLEARTEXT)
                .build();
        httpClient = new OkHttpClient.Builder()
                .protocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2))
                .connectionSpecs(Collections.singletonList(httpSpec))
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();

        // Setup Mailbox Checker Task for default system
//        MVC.runPeriodically(new Runnable() {
//            @Override
//            public void run() {
//                List<Subscription> subs = subscriptions.get(DesktopClient.class.getName());
//                List<Envelope> mail = getMail(DesktopClient.class.getName());
//                if(mail == null) {
//                    LOG.info("No Mail.");
//                    return;
//                }
//                LOG.info(mail.size()+" mail received.");
//                for(Envelope e : mail) {
//                    if(e.getMessage() instanceof EventMessage) {
//                        EventMessage em = (EventMessage) e.getMessage();
//                        for(Subscription sub : subs) {
//                            if(sub.getEventMessageType().name().equals(em.getType())) {
//                                sub.getClient().reply(e);
//                            }
//                        }
//                    }
//                }
//            }
//        }, 5);

        if(false) {
            subscribe(DesktopClient.class.getName(), new Subscription(EventMessage.Type.NETWORK_STATE_UPDATE, new Client() {
                @Override
                public void reply(Envelope e) {
                    javafx.application.Platform.runLater(() -> {
                        LOG.info("Updating UI with Network State...");
                        EventMessage em = (EventMessage) e.getMessage();
                        NetworkState state = (NetworkState) em.getMessage();
                        networkStates.put(state.network.name(), state);
                        switch (state.network) {
                            case LiFi: {
                                ((TopicListener) MVC.loadView(LiFiOpsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                                ((TopicListener) MVC.loadView(LiFiNetworkSettingsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                                break;
                            }
                            case Tor: {
                                ((TopicListener) MVC.loadView(TOROpsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                                ((TopicListener) MVC.loadView(TORNetworkSettingsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                                break;
                            }
                            case I2P: {
                                ((TopicListener) MVC.loadView(I2POpsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                                ((TopicListener) MVC.loadView(I2PNetworkSettingsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                                break;
                            }
                            case Bluetooth: {
                                ((TopicListener) MVC.loadView(BluetoothOpsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                                ((TopicListener) MVC.loadView(BluetoothNetworkSettingsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                                break;
                            }
                            case WiFi: {
                                ((TopicListener) MVC.loadView(WifiDirectOpsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                                ((TopicListener) MVC.loadView(WiFiNetworkSettingsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                                break;
                            }
                            case Satellite: {
                                ((TopicListener) MVC.loadView(SatelliteOpsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                                ((TopicListener) MVC.loadView(SatelliteNetworkSettingsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                                break;
                            }
                            case FSRadio: {
                                ((TopicListener) MVC.loadView(FullSpectrumRadioOpsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                                ((TopicListener) MVC.loadView(FullSpectrumRadioNetworkSettingsView.class, true)).modelUpdated(NetworkState.class.getSimpleName(), state);
                                break;
                            }
                        }

                        NetworkState torNS = networkStates.get(Network.Tor.name());
                        NetworkState i2pNS = networkStates.get(Network.I2P.name());
                        NetworkState btNS = networkStates.get(Network.Bluetooth.name());
                        NetworkState wifiNS = networkStates.get(Network.WiFi.name());
                        NetworkState satNS = networkStates.get(Network.Satellite.name());
                        NetworkState fsRadNS = networkStates.get(Network.FSRadio.name());
                        NetworkState lifiNS = networkStates.get(Network.LiFi.name());

                        boolean outernetAvailable = btNS != null && btNS.networkStatus == NetworkStatus.CONNECTED
                                || wifiNS != null && wifiNS.networkStatus == NetworkStatus.CONNECTED
                                || satNS != null && satNS.networkStatus == NetworkStatus.CONNECTED
                                || fsRadNS != null && fsRadNS.networkStatus == NetworkStatus.CONNECTED
                                || lifiNS != null && lifiNS.networkStatus == NetworkStatus.CONNECTED;

                        boolean privateReroutedInternetAvailable = torNS != null && torNS.networkStatus == NetworkStatus.CONNECTED
                                && i2pNS != null && i2pNS.networkStatus == NetworkStatus.CONNECTED;

                        boolean privateInternetAvailable = torNS != null && torNS.networkStatus == NetworkStatus.CONNECTED
                                || i2pNS != null && i2pNS.networkStatus == NetworkStatus.CONNECTED;

                        if (outernetAvailable) ManConStatus.MAX_AVAILABLE_MANCON = ManCon.NEO;
                        else if (privateReroutedInternetAvailable) ManConStatus.MAX_AVAILABLE_MANCON = ManCon.VERYHIGH;
                        else if (privateInternetAvailable) ManConStatus.MAX_AVAILABLE_MANCON = ManCon.MEDIUM;
                        else ManConStatus.MAX_AVAILABLE_MANCON = ManCon.NONE;

//                    HomeView v = (HomeView)MVC.loadView(HomeView.class, true);
//                    v.updateManConBox();

                        MVC.manConStatusUpdated();

                    });
                }
            }));

            subscribe(DesktopClient.class.getName(), new Subscription(EventMessage.Type.SERVICE_STATUS, new Client() {
                @Override
                public void reply(Envelope e) {
                    javafx.application.Platform.runLater(() -> {
                        LOG.info("Updating UI with Service Report...");
                        EventMessage em = (EventMessage) e.getMessage();
                        ServiceReport report = (ServiceReport) em.getMessage();
                        if (report.serviceClassName == null) {
                            LOG.warning("No serviceClassName in Service Report! BUG");
                            return;
                        }
                        serviceReports.put(report.serviceClassName, report);
                        switch (report.serviceClassName) {
                            case "ra.lifi.LiFiService": {
                                ((TopicListener) MVC.loadView(LiFiOpsView.class, true)).modelUpdated(ServiceReport.class.getSimpleName(), report);
                                break;
                            }
                            case "ra.tor.TORClientService": {
                                ((TopicListener) MVC.loadView(TOROpsView.class, true)).modelUpdated(ServiceReport.class.getSimpleName(), report);
                                break;
                            }
                            case "ra.i2p.I2PService": {
                                ((TopicListener) MVC.loadView(I2POpsView.class, true)).modelUpdated(ServiceReport.class.getSimpleName(), report);
                                break;
                            }
                            case "ra.bluetooth.BluetoothService": {
                                ((TopicListener) MVC.loadView(BluetoothOpsView.class, true)).modelUpdated(ServiceReport.class.getSimpleName(), report);
                                break;
                            }
                            case "ra.wifidirect.WiFiDirectNetwork": {
                                ((TopicListener) MVC.loadView(WifiDirectOpsView.class, true)).modelUpdated(ServiceReport.class.getSimpleName(), report);
                                break;
                            }
                            case "ra.satellite.SatelliteService": {
                                ((TopicListener) MVC.loadView(SatelliteOpsView.class, true)).modelUpdated(ServiceReport.class.getSimpleName(), report);
                                break;
                            }
                            case "ra.gnuradio.GNURadioService": {
                                ((TopicListener) MVC.loadView(FullSpectrumRadioOpsView.class, true)).modelUpdated(ServiceReport.class.getSimpleName(), report);
                                break;
                            }
                        }
                    });
                }
            }));
        }
        return true;
    }

    private boolean subscribe(String client, Subscription subscription) {
        Envelope e = Envelope.documentFactory();
        e.addNVP("EventMessageType", subscription.getEventMessageType().name());
        e.addNVP("ClientId", client);
        e.addNVP("Service", MailDropService.class.getName());
        e.addNVP("Operation", MailDropService.OPERATION_DROPOFF);
        e.addRoute(NotificationService.class.getName(), NotificationService.OPERATION_SUBSCRIBE);
        List<Subscription> subs = subscriptions.get(client);
        if(subs==null) {
            subs = new ArrayList<>();
            subscriptions.put(client, subs);
        }
        subs.add(subscription);
        return sendMessage(e);
    }

    private List<Envelope> getMail(String client) {
        Envelope e = Envelope.documentFactory();
        e.setClient(client);
        e.addRoute(MailDropService.class, MailDropService.OPERATION_PICKUP_CLEAN);
        sendMessage(e);
        return (List<Envelope>)e.getValue("ra.maildrop.Mail");
    }

    private boolean sendMessage(Envelope e) {
        if(!isConnected() && !connect()) {
            e.getMessage().addErrorMessage("HTTP Client not connected and unable to connect.");
            LOG.warning("HTTP Client not connected and unable to connect.");
//            popError(e);
            return false;
        }
        Message m = e.getMessage();
        URL url;
        try {
            url = new URL("http://localhost:"+apiPort);
            LOG.info("URL: "+url);
        } catch (MalformedURLException malformedURLException) {
            LOG.warning(malformedURLException.getLocalizedMessage());
            return false;
        }
        Map<String, Object> h = e.getHeaders();
        Map<String, String> hStr = new HashMap<>();
//        if(h.containsKey(Envelope.HEADER_AUTHORIZATION) && h.get(Envelope.HEADER_AUTHORIZATION) != null) {
//            hStr.put(Envelope.HEADER_AUTHORIZATION, (String) h.get(Envelope.HEADER_AUTHORIZATION));
//        }
        hStr.put(Envelope.HEADER_AUTHORIZATION, "Basic cmE6MTIzNA==");
//        if(h.containsKey(Envelope.HEADER_CONTENT_DISPOSITION) && h.get(Envelope.HEADER_CONTENT_DISPOSITION) != null) {
//            hStr.put(Envelope.HEADER_CONTENT_DISPOSITION, (String) h.get(Envelope.HEADER_CONTENT_DISPOSITION));
//        }
//        if(h.containsKey(Envelope.HEADER_CONTENT_TYPE) && h.get(Envelope.HEADER_CONTENT_TYPE) != null) {
//            hStr.put(Envelope.HEADER_CONTENT_TYPE, (String) h.get(Envelope.HEADER_CONTENT_TYPE));
//        }
        hStr.put(Envelope.HEADER_CONTENT_TYPE, "application/json");
//        if(h.containsKey(Envelope.HEADER_CONTENT_TRANSFER_ENCODING) && h.get(Envelope.HEADER_CONTENT_TRANSFER_ENCODING) != null) {
//            hStr.put(Envelope.HEADER_CONTENT_TRANSFER_ENCODING, (String) h.get(Envelope.HEADER_CONTENT_TRANSFER_ENCODING));
//        }
//        if(h.containsKey(Envelope.HEADER_USER_AGENT) && h.get(Envelope.HEADER_USER_AGENT) != null) {
//            hStr.put(Envelope.HEADER_USER_AGENT, (String) h.get(Envelope.HEADER_USER_AGENT));
//        }

        ByteBuffer bodyBytes = null;
        CacheControl cacheControl = null;
        if (e.getMultipart() != null) {
            // handle file upload
            Multipart mp = e.getMultipart();
            hStr.put(Envelope.HEADER_CONTENT_TYPE, "multipart/form-data; boundary=" + mp.getBoundary());
            try {
                bodyBytes = ByteBuffer.wrap(mp.finish().getBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
                // TODO: Provide error message
                LOG.warning("IOException caught while building HTTP body with multipart: " + e1.getLocalizedMessage());
                m.addErrorMessage("IOException caught while building HTTP body with multipart: " + e1.getLocalizedMessage());
//                popError(e);
                return false;
            }
            cacheControl = new CacheControl.Builder().noCache().build();
        }

        Headers headers = Headers.of(hStr);
        byte[] body = e.toJSON().getBytes();
        if (bodyBytes == null) {
            bodyBytes = ByteBuffer.wrap(body);
        } else {
            bodyBytes.put(body);
        }
        LOG.info("Request: "+new String(body));

        RequestBody requestBody = null;
        if(bodyBytes != null) {
            if(h.get(Envelope.HEADER_CONTENT_TYPE)==null)
                requestBody = RequestBody.create(MediaType.parse("application/json"), bodyBytes.array());
            else
                requestBody = RequestBody.create(MediaType.parse((String) h.get(Envelope.HEADER_CONTENT_TYPE)), bodyBytes.array());
        }

        Request.Builder b = new Request.Builder().url(url);
        if(cacheControl != null)
            b = b.cacheControl(cacheControl);
        b = b.headers(headers);
        if(e.getAction()==null) {
            e.setAction(Envelope.Action.POST);
        }
        switch(e.getAction()) {
            case POST: {b = b.post(requestBody);break;}
            case PUT: {b = b.put(requestBody);break;}
            case DELETE: {b = (requestBody == null ? b.delete() : b.delete(requestBody));break;}
            case GET: {b = b.get();break;}
            default: {
                LOG.warning("Envelope.action must be set to ADD, UPDATE, REMOVE, or VIEW");
                m.addErrorMessage("Envelope.action must be set to ADD, UPDATE, REMOVE, or VIEW");
//                popError(e);
                return false;
            }
        }
        Request req = b.build();
        if(req == null) {
            LOG.warning("okhttp3 builder didn't build request.");
            m.addErrorMessage("okhttp3 builder didn't build request.");
//            popError(e);
            return false;
        }
        Response response = null;
        long start;
        long end;
        LOG.info("Sending http request, host="+url.getHost());
        try {
            start = new Date().getTime();
            response = httpClient.newCall(req).execute();
            end = new Date().getTime();
            LOG.info("Took: "+(end-start)+"ms");
            if(!response.isSuccessful()) {
                LOG.warning("HTTP request not successful: "+response.code());
                m.addErrorMessage(response.code()+"");
//                    handleFailure(start, end, m, url.toString());
//                    popError(e);
                return false;
            }
        } catch (IOException e2) {
            LOG.warning(e2.getLocalizedMessage());
            m.addErrorMessage(e2.getLocalizedMessage());
//                popError(e);
            return false;
        }

        LOG.info("Received http response.");
        Headers responseHeaders = response.headers();
//        for (int i = 0; i < responseHeaders.size(); i++) {
//            LOG.info(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//        }
        ResponseBody responseBody = response.body();
        if(responseBody != null) {
            try {
                e.fromMap((Map)JSONParser.parse(new String(responseBody.bytes())));
//                e.addContent(responseBody.bytes());
            } catch (IOException e1) {
                LOG.warning(e1.getLocalizedMessage());
            } finally {
                responseBody.close();
            }
        } else {
            LOG.info("Body was null.");
        }
        reply(e);
        return true;
    }

    public boolean connect() {
        try {
            LOG.info("Setting up HTTP spec client....");
            httpSpec = new ConnectionSpec
                    .Builder(ConnectionSpec.CLEARTEXT)
                    .build();
            LOG.info("Setting up http client...");
            httpClient = new OkHttpClient.Builder()
                    .protocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2))
                    .connectionSpecs(Collections.singletonList(httpSpec))
                    .retryOnConnectionFailure(true)
                    .followRedirects(true)
                    .build();

        } catch (Exception e) {
            LOG.warning("Exception caught launching HTTP Client Service: " + e.getLocalizedMessage());
            localhostStatus = NetworkStatus.ERROR;
            return false;
        }
        localhostStatus = NetworkStatus.CONNECTED;
        return true;
    }

    private boolean disconnect() {
        // Tear down clients and their specs
        httpClient = null;
        httpSpec = null;
        localhostStatus = NetworkStatus.DISCONNECTED;
        return true;
    }

    private boolean isConnected() {
        return localhostStatus == NetworkStatus.CONNECTED;
    }

}
