package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import teledon.model.Volunteer;
import teledon.services.ITeledonServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;

public class LogInController {

    @FXML
    public Text usernameError;
    public Button loginButton;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    private ITeledonServices server;
    private TeledonController teledonController;
    private Volunteer volunteer;
    private Parent mainTeledonParent;
    private static final Logger logger = LogManager.getLogger(LogInController.class);
    public void setServer(ITeledonServices servicesImpl) {
        this.server = servicesImpl;
    }
    public void setTeledonController(TeledonController teledonController) {
        this.teledonController = teledonController;
    }
    public void setParent(Parent p){
        mainTeledonParent=p;
    }
    @FXML
    void loginButtonClicked(ActionEvent event) throws IOException {
        try{
            Volunteer volunteer = server.login(username.getText(), password.getText(), teledonController);
            usernameError.setVisible(false);
            Stage stage = new Stage();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    teledonController.logout();
                    logger.debug("Logout from server");
                    System.exit(0);
                }
            });
            Scene scene = new Scene(mainTeledonParent);
            stage.setScene(scene);
            teledonController.setVolunteer(volunteer);
            teledonController.initApplication();
            stage.show();
            ((Node)(event.getSource())).getScene().getWindow().hide();
        } catch (Exception e) {
            Platform.runLater(() -> {
                usernameError.setManaged(true);
                usernameError.setText(e.getMessage());
                usernameError.setVisible(true);
            });
        }
    }
}