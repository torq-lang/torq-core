module org.torqlang.local {

    requires org.torqlang.lang;
    requires org.torqlang.klvm;
    requires org.torqlang.util;

    opens org.torqlang.local.torqsrc.torq.lang;
    opens org.torqlang.local.torqsrc.torq.util;

    exports org.torqlang.local;

}
