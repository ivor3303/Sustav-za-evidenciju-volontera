package hr.tvz.projekt.controller;

import hr.tvz.projekt.app.AppState;
import hr.tvz.projekt.entities.SkillType;
import hr.tvz.projekt.entities.Volunteer;
import hr.tvz.projekt.repository.jdbc.VolunteerJdbcRepository;
import hr.tvz.projekt.util.AlertUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AddVolunteerController {

    @FXML private TextField nameField;
    @FXML private TextField cityField;
    @FXML private TextField emailField;
    @FXML private Spinner<Integer> ageSpinner;

    @FXML private ComboBox<SkillType> skillCombo;

    @FXML private Label statusLabel;

    private AppState appState;

    public void setStateAppState(AppState appState) {
        this.appState = appState;
    }

    @FXML
    public void initialize() {
        ageSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(16, 99, 25));
        ageSpinner.setEditable(true);

        skillCombo.setItems(FXCollections.observableArrayList(SkillType.values()));
        skillCombo.getSelectionModel().selectFirst();
    }

    private VolunteerJdbcRepository volunteerRepo;
    public void setVolunteerRepo(VolunteerJdbcRepository repo) { this.volunteerRepo = repo; }

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
        SkillType skillType = skillCombo.getValue();
        if (name.isBlank() || city.isBlank() || email.isBlank() || skillType == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") || age == null || age <= 0 ) {
            AlertUtils.error("Neispravan unos", "Sva polja su obavezna.");
            return;
        }
        Volunteer v = new Volunteer(name, city, email, age, skillType);
        volunteerRepo.save(v);
        appState.getAllVolunteers().add(v);
        appState.setLastVolunteerText(
                v.getName() + " (" + v.getCity() + ", " + v.getSkillType() + ")"
        );
        appState.setLastVolunteerText(v.getName() + " (" + v.getCity() + ", " + v.getSkillType() + ")");
        statusLabel.setText(" Volonter dodan.");
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
        skillCombo.getSelectionModel().selectFirst();
    }
}
