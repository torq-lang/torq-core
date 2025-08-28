module org.torqlang.server {

    requires org.torqlang.local;
    requires org.torqlang.lang;
    requires org.torqlang.klvm;
    requires org.torqlang.util;

    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.http2.server;

    exports org.torqlang.server;

}
