/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.server;

import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public final class LocalServerBuilder {

    private int port;
    private final ContextHandlerCollection contextHandlers = new ContextHandlerCollection(false);

    LocalServerBuilder() {
    }

    public final LocalServerBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public final int port() {
        return port;
    }

    public final LocalServerBuilder addContextHandler(Handler handler, String contextPath) {
        ContextHandler contextHandler = new ContextHandler(handler, contextPath);
        contextHandlers.addHandler(contextHandler);
        return this;
    }

    public LocalServer build() {
        Server server = new Server(port);
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSendServerVersion(false);
        HttpConnectionFactory http11 = new HttpConnectionFactory(httpConfig);
        HTTP2CServerConnectionFactory http2c = new HTTP2CServerConnectionFactory(httpConfig);
        Connector connector = new ServerConnector(server, http11, http2c);
        server.addConnector(connector);
        server.setHandler(contextHandlers);
        return new LocalServer(server, port);
    }

}
