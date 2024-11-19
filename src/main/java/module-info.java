module nm.cs.clickersupport {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires jnativehook;
    requires com.fasterxml.jackson.databind;
    requires java.logging;
    requires java.desktop;

    opens nm.cs.clickersupport to javafx.fxml;
    exports nm.cs.clickersupport;
}