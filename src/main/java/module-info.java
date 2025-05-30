module org.example.uchattincapstoneproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.azure.cosmos;
    requires client.sdk;
    requires com.azure.http.netty;
    requires java.sql;
    requires java.net.http;
    requires jbcrypt;
    requires org.apache.qpid.proton.j;
    requires java.desktop;
    requires json.smart;
    requires org.json;
    requires com.azure.security.keyvault.secrets;
    requires com.azure.identity;

    // Export the model package
    exports org.example.uchattincapstoneproject.model;

    // Export the viewModel package
    exports org.example.uchattincapstoneproject.viewModel;

    // Open packages to javafx.fxml
    opens org.example.uchattincapstoneproject.viewModel to javafx.fxml;
}