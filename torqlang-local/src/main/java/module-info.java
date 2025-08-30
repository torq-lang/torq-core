module org.torqlang.local {
    requires org.torqlang.lang;
    requires org.torqlang.klvm;
    requires org.torqlang.util;

    opens torq.lang;
    opens torq.util;

    exports org.torqlang.local;
}
