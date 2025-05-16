import gui.LogInController;
import gui.TeledonController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import teledon.networking.jsonprotocol.TeledonServicesJsonProxy;
import teledon.services.ITeledonServices;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class StartJsonClientFX extends Application {
    private static final int defaultChatPort = 55555;
    private static final String defaultServer = "localhost";

    private static final Logger logger = LogManager.getLogger(StartJsonClientFX.class);

    public void start(Stage primaryStage) throws Exception {
        logger.debug("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartJsonClientFX.class.getResourceAsStream("/teledonclient.properties"));
            logger.info("Client properties set {} ",clientProps);
            clientProps.list(System.out);
        } catch (IOException e) {
            logger.error("Cannot find teledonclient.properties {}", String.valueOf(e));
            logger.debug("Looking for teledonclient.properties in folder {}",(new File(".")).getAbsolutePath());
            return;
        }
        String serverIP = clientProps.getProperty("teledon.server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("teledon.server.port"));
        } catch (NumberFormatException ex) {
            logger.error("Wrong port number {}", ex.getMessage());
            logger.debug("Using default port: " + defaultChatPort);
        }
        logger.info("Using server IP {}", serverIP);
        logger.info("Using server port {}", serverPort);

        ITeledonServices server = new TeledonServicesJsonProxy(serverIP, serverPort);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/LogInView.fxml"));
        Parent root=loader.load();

        LogInController ctrl =
                loader.<LogInController>getController();
        ctrl.setServer(server);

        FXMLLoader cloader = new FXMLLoader(
                getClass().getResource("/MainView.fxml"));
        Parent croot=cloader.load();


        TeledonController teledonController =
                cloader.<TeledonController>getController();
        teledonController.setServer(server);

        ctrl.setTeledonController(teledonController);
        ctrl.setParent(croot);

        primaryStage.setTitle("Teledon");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }
    public static void main(String[] args) {
        launch();
    }
}
