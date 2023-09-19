module com.example.demo8 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.mail;
    requires org.apache.commons.lang3;
    requires java.logging;
    requires javax.activation;
    requires org.apache.commons.io;
    requires java.sql;


    opens com.example.demo8 to javafx.fxml;
    exports com.example.demo8;
}