package hr.tvz.projekt.controller;

import hr.tvz.projekt.app.AppState;
import hr.tvz.projekt.entities.Organizer;
import hr.tvz.projekt.repository.jdbc.OrganizerJdbcRepository;
import hr.tvz.projekt.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

public class AddOrganizerController {

    @FXML private TextField nameField;
    @FXML private TextField cityField;
    @FXML private TextField emailField;
    @FXML private Spinner<Integer> ageSpinner;
    @FXML private Label statusLabel;

    private AppState appState;

    public void setStateAppState(AppState appState) {
        this.appState = appState;
    }

    private OrganizerJdbcRepository organizerRepo;

    public void setOrganizerRepo(OrganizerJdbcRepository organizerRepo) {
        this.organizerRepo = organizerRepo;
    }

    @FXML
    public void initialize() {
        ageSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(16, 99, 25));
    }

    @FXML
    private void save() {
        if (appState == null) {
            statusLabel.setText("Greška: AppState nije postavljen.");
            return;
        }

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String city = cityField.getText() == null ? "" : cityField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        Integer age = ageSpinner.getValue();

        if (name.isBlank() || city.isBlank() || email.isBlank() || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") || age <= 0 ) {
            AlertUtils.error("Neispravan unos", "Sva polja su obavezna.");
            return;
        }

        Organizer organizer = new Organizer(name, city, email, age);
        organizerRepo.save(organizer);
        appState.getAllOrganizers().add(organizer);


        statusLabel.setText("Organizator dodan.");
        clearForm();
    }

    @FXML
    private void cancel() {
        clearForm();
        statusLabel.setText("Unos poništen.");
    }

    private void clearForm() {
        nameField.clear();
        cityField.clear();
        emailField.clear();
        ageSpinner.getValueFactory().setValue(25);
    }
}
