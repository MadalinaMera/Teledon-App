package gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import teledon.model.CharityCase;
import teledon.model.Donor;
import teledon.model.Volunteer;
import teledon.services.ITeledonObserver;
import teledon.services.ITeledonServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import teledon.services.TeledonException;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TeledonController implements Initializable, ITeledonObserver {
    private ITeledonServices server;
    private Volunteer volunteer;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField donatedSumField;
    @FXML
    private TableColumn<CharityCase, String> nameColumn;
    @FXML
    private TableColumn<CharityCase, String> sumColumn;
    @FXML
    private TableView<CharityCase> charityCasesTable;
    @FXML
    private ListView<Donor> filteredDonorsList;

    private final ObservableList<CharityCase> charityCasesListObservable = FXCollections.observableArrayList();
    private final ObservableList<Donor> donorsListObservable = FXCollections.observableArrayList();

    private static final Logger logger = LogManager.getLogger(TeledonController.class);
    public TeledonController() {
        logger.debug("Constructor gui.TeledonController");
    }
    public TeledonController(ITeledonServices server) {
        this.server = server;
        logger.debug("constructor gui.TeledonController cu server param");
    }

    public void setServer(ITeledonServices s) {
        server = s;
    }
    public void initApplication() {
        logger.debug("initApplication");
        setCharityCasesTable();
        setDonorsList();
        charityCasesTable.setItems(charityCasesListObservable);
        filteredDonorsList.setItems(donorsListObservable);
        filterDonors();
    }
    public void setCharityCasesTable() {
        try {
            List<CharityCase> charityCases = server.findAllCases();
            if (charityCases != null) {
               charityCasesListObservable.clear();
                charityCasesListObservable.addAll(charityCases);
            }
        } catch (TeledonException e) {
            logger.error("An error occurred while fetching charity cases: {}", e.getMessage(), e);
        }
    }

    public void setDonorsList() {
        try {
            List<Donor> donors = server.findAllDonors();
            donorsListObservable.clear();
            donorsListObservable.addAll(donors);
        } catch (TeledonException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }
    private void filterDonors(){
        FilteredList<Donor> filteredData = new FilteredList<>(donorsListObservable, p -> true);
        filteredDonorsList.setItems(filteredData);
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(donor -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return donor.getName().toLowerCase().contains(lowerCaseFilter);
            });
        });
        filteredDonorsList.setOnMouseClicked(event -> {
            Donor selectedDonor = filteredDonorsList.getSelectionModel().getSelectedItem();
            if (selectedDonor != null) {
                nameField.setText(selectedDonor.getName());
                phoneNumberField.setText(selectedDonor.getPhone());
                addressField.setText(selectedDonor.getAddress());
            }
        });
    }
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        logger.debug("INIT");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        sumColumn.setCellValueFactory(new PropertyValueFactory<>("totalSum"));
        charityCasesTable.setItems(charityCasesListObservable);
        logger.debug("END INIT!!!!!!!!!");
    }
    @FXML
    public void searchDonor() {
        filteredDonorsList.setManaged(true);
        filteredDonorsList.setVisible(true);
    }

    public void handleLogout(ActionEvent actionEvent) {
        logout();
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }

    void logout() {
        try {
            server.logout(volunteer, this);
        } catch (TeledonException e) {
            logger.error("Logout error {}", String.valueOf(e));
        }
    }

    public void handleAddDonation(ActionEvent actionEvent) {
        int index = charityCasesTable.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            Util.showWarning("Donation error", "Please select a charity case from the list");
            return;
        }
        CharityCase charityCase = charityCasesTable.getItems().get(index);
        String name = nameField.getText();
        String phoneNumber = phoneNumberField.getText();
        String address = addressField.getText();
        String sum = donatedSumField.getText();
        if (sum.isEmpty()) {
            Util.showWarning("Donation error", "Please fill in the donation field before sending");
            return;
        }
        new Thread(() -> {
            try {
                server.donate(name, phoneNumber, address, Integer.parseInt(sum), charityCase.getId());
                List<CharityCase> updatedCharityCases = server.findAllCases(); // Fetch updated cases
                Platform.runLater(() -> {
                    Util.showWarning("Donation success", "You have successfully donated " + sum + " to " + charityCase.getName());
                    try {
                        donationsUpdated(updatedCharityCases); // Update the table for the current client
                    } catch (TeledonException e) {
                        logger.error("Error updating donations: {}", e.getMessage(), e);
                    }
                    donatedSumField.clear();
                });
            } catch (TeledonException e) {
                logger.error(e);
                Platform.runLater(() -> Util.showWarning("Donation error", "An error occurred: " + e.getMessage()));
            }
        }).start();
    }
    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }


    @Override
    public void donationsUpdated(List<CharityCase> updatedCharityCases) throws TeledonException {
        /// fill the table with the updated charity cases
        charityCasesListObservable.clear();
        charityCasesListObservable.addAll(updatedCharityCases);
    }
}