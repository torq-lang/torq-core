package org.torqlang.server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

public record ApiCall(Request request, Response response, Callback callback) {
}
