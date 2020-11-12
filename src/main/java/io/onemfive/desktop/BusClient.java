package io.onemfive.desktop;

import ra.common.Envelope;
import ra.common.service.ServiceNotAccessibleException;
import ra.common.service.ServiceNotSupportedException;
import ra.common.service.ServiceStatusObserver;
import ra.servicebus.ServiceBus;

import java.util.List;
import java.util.Properties;

public class BusClient {

    private static ServiceBus bus;

    public static boolean sendRequest(Envelope envelope) {
        return bus.send(envelope);
    }

    public static void registerService(Class serviceClass, Properties p, List<ServiceStatusObserver> observers)
            throws ServiceNotAccessibleException, ServiceNotSupportedException {
        bus.registerService(serviceClass, p, observers);
    }

    public static boolean start(Properties properties) {
        bus = new ServiceBus();
        return bus.start(properties);
    }

    public static boolean shutdown(boolean force) {
        if(force) {
            return bus.shutdown();
        } else {
            return bus.gracefulShutdown();
        }
    }

}
