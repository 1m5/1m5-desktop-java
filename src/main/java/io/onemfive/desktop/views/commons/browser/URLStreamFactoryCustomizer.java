package io.onemfive.desktop.views.commons.browser;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class URLStreamFactoryCustomizer {

    private static Logger LOG = Logger.getLogger(URLStreamFactoryCustomizer.class.getName());
    private static Proxy TOR_PROXY = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", 9050));
    private static Proxy I2P_PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 4444));
    private static Hashtable handlers;
    private static URLStreamHandler HTTP_NO_PROXY_HANDLER;
    private static URLStreamHandler HTTPS_NO_PROXY_HANDLER;
    private static DelegatingScopedProxyAwareUrlStreamHandler HTTP_TOR_PROXIED_HANDLER;
    private static DelegatingScopedProxyAwareUrlStreamHandler HTTPS_TOR_PROXIED_HANDLER;
    private static DelegatingScopedProxyAwareUrlStreamHandler HTTP_I2P_PROXIED_HANDLER;
    private static DelegatingScopedProxyAwareUrlStreamHandler HTTPS_I2P_PROXIED_HANDLER;

    static {
        try {
            Field handlersField = URL.class.getDeclaredField("handlers");
            handlersField.setAccessible(true);
            handlers = (Hashtable) handlersField.get(null);
            HTTP_NO_PROXY_HANDLER = (URLStreamHandler) handlers.get("http");
            HTTPS_NO_PROXY_HANDLER = (URLStreamHandler) handlers.get("https");
            HTTP_TOR_PROXIED_HANDLER = new DelegatingScopedProxyAwareUrlStreamHandler(HTTP_NO_PROXY_HANDLER, TOR_PROXY);
            HTTPS_TOR_PROXIED_HANDLER = new DelegatingScopedProxyAwareUrlStreamHandler(HTTPS_NO_PROXY_HANDLER, TOR_PROXY);
            HTTP_I2P_PROXIED_HANDLER = new DelegatingScopedProxyAwareUrlStreamHandler(HTTP_NO_PROXY_HANDLER, I2P_PROXY);
            HTTPS_I2P_PROXIED_HANDLER = new DelegatingScopedProxyAwareUrlStreamHandler(HTTPS_NO_PROXY_HANDLER, I2P_PROXY);
        } catch (Exception e) {
            LOG.warning(e.getLocalizedMessage());
        }
    }

    public static void useTORProxyForWebkit() {
        handlers.put("http", HTTP_TOR_PROXIED_HANDLER);
        handlers.put("https", HTTPS_TOR_PROXIED_HANDLER);
    }

    public static void useI2PProxyForWebkit() {
        handlers.put("http", HTTP_I2P_PROXIED_HANDLER);
        handlers.put("https", HTTPS_I2P_PROXIED_HANDLER);
    }

    public static void noProxyForWebKit() {
        handlers.put("http", HTTP_NO_PROXY_HANDLER);
        handlers.put("https", HTTPS_NO_PROXY_HANDLER);
        forceInitializationOfOriginalUrlStreamHandlers();
    }

//    public static void useDedicatedProxyForWebkit(Proxy proxy, String protocols) {
//        tryReplaceOriginalUrlStreamHandlersWithScopeProxyAwareVariants(proxy, protocols);
//    }

//    private static void tryReplaceOriginalUrlStreamHandlersWithScopeProxyAwareVariants(Proxy proxy, String protocols) {
//
//        try {

//            Hashtable handlers = tryExtractInternalHandlerTableFromUrl();
            //LOG.info(handlers);

//            Consumer<String> wrapStreamHandlerWithScopedProxyHandler = protocol ->
//            {
//                URLStreamHandler originalHandler = (URLStreamHandler) handlers.get(protocol);
//                handlers.put(protocol, new DelegatingScopedProxyAwareUrlStreamHandler(originalHandler, proxy));
//            };
//
//            Arrays.stream(protocols.split(",")).map(String::trim).filter(s -> !s.isEmpty()).forEach(wrapStreamHandlerWithScopedProxyHandler);
//
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
//    }

//    private static Hashtable tryExtractInternalHandlerTableFromUrl() {
//
//        try {
//            Field handlersField = URL.class.getDeclaredField("handlers");
//            handlersField.setAccessible(true);
//            return (Hashtable) handlersField.get(null);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    private static void forceInitializationOfOriginalUrlStreamHandlers() {

        try {
            new URL("http://.");
            new URL("https://.");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    static class DelegatingScopedProxyAwareUrlStreamHandler extends URLStreamHandler {

        private static final Method openConnectionMethod;
        private static final Method openConnectionWithProxyMethod;

        static {

            try {
                openConnectionMethod = URLStreamHandler.class.getDeclaredMethod("openConnection", URL.class);
                openConnectionWithProxyMethod = URLStreamHandler.class.getDeclaredMethod("openConnection", URL.class, Proxy.class);

                openConnectionMethod.setAccessible(true);
                openConnectionWithProxyMethod.setAccessible(true);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private final URLStreamHandler delegatee;
        private final Proxy proxy;

        public DelegatingScopedProxyAwareUrlStreamHandler(URLStreamHandler delegatee, Proxy proxy) {

            this.delegatee = delegatee;
            this.proxy = proxy;
        }

        @Override
        protected URLConnection openConnection(URL url) throws IOException {

            try {
                if (isWebKitURLLoaderThread(Thread.currentThread())) {
                    LOG.info("WebKit using proxy..."+proxy.toString());
                    //WebKit requested loading the given url, use provided proxy.
                    return (URLConnection) openConnectionWithProxyMethod.invoke(delegatee, url, proxy);
                }
                LOG.info("WebKit no proxy...");
                //Invoke the standard url handler.
                return (URLConnection) openConnectionMethod.invoke(delegatee, url);

            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        private boolean isWebKitURLLoaderThread(Thread thread) {

            StackTraceElement[] st = thread.getStackTrace();

            //TODO Add more robust stack-trace inspection.
            return st.length > 4 && st[4].getClassName().startsWith("com.sun.webkit.network");
        }
    }
}
