module org.example.uchattincapstoneproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.azure.cosmos;
    requires client.sdk;
    requires com.azure.http.netty;
    requires java.sql;
    requires java.net.http;
    requires jbcrypt;
    requires com.fasterxml.jackson.databind;
    requires org.apache.qpid.proton.j;

    // Export the model package
    exports org.example.uchattincapstoneproject.model;

    // Export the viewModel package
    exports org.example.uchattincapstoneproject.viewModel;

    // Open packages to javafx.fxml
    opens org.example.uchattincapstoneproject to javafx.fxml;
    opens org.example.uchattincapstoneproject.viewModel to javafx.fxml;
}