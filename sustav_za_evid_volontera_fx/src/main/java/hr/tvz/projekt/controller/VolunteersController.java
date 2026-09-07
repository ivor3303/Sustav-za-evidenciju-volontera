package hr.tvz.projekt.controller;

import hr.tvz.projekt.app.AppState;
import hr.tvz.projekt.entities.Volunteer;
import hr.tvz.projekt.fx.VolunteerSearchField;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Locale;

public class VolunteersController {

    @FXML private ComboBox<VolunteerSearchField> fieldCombo;
    @FXML private TextField valueField;

    @FXML private TableView<Volunteer> table;
    @FXML private TableColumn<Volunteer, String> colName;
    @FXML private TableColumn<Volunteer, String> colCity;
    @FXML private TableColumn<Volunteer, String> colEmail;
    @FXML private TableColumn<Volunteer, Number> colAge;
    @FXML private TableColumn<Volunteer, String> colSkillType;

    private AppState appState;

    public void setAppState(AppState appState) {
        this.appState = appState;
        if (table != null && appState != null && appState.getAllVolunteers() != null) {
            table.setItems(appState.getAllVolunteers());
        }
    }

    @FXML
    public void initialize() {
        fieldCombo.setItems(FXCollections.observableArrayList(VolunteerSearchField.values()));
        fieldCombo.getSelectionModel().select(VolunteerSearchField.NAME);

        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        colCity.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCity()));
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        colAge.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getAge()));

        colSkillType.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getSkillType() != null ? d.getValue().getSkillType().toString() : ""
        ));

        if (appState != null && appState.getAllVolunteers() != null) {
            table.setItems(appState.getAllVolunteers());
        }
    }

    @FXML
    private void onSearch() {
        if (appState == null || appState.getAllVolunteers() == null) return;

        String term = valueField.getText() == null ? "" : valueField.getText().trim();
        VolunteerSearchField field = fieldCombo.getValue();

        if (term.isBlank()) {
            table.setItems(appState.getAllVolunteers());
            return;
        }

        List<Volunteer> filtered = appState.getAllVolunteers().stream()
                .filter(v -> matches(v, field, term))
                .toList();

        table.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void onReset() {
        valueField.clear();
        if (appState != null && appState.getAllVolunteers() != null) {
            table.setItems(appState.getAllVolunteers());
        }
    }

    private boolean matches(Volunteer v, VolunteerSearchField field, String term) {
        String t = term.toLowerCase(Locale.ROOT);

        return switch (field) {
            case NAME -> safe(v.getName()).contains(t);
            case CITY -> safe(v.getCity()).contains(t);
            case EMAIL -> safe(v.getEmail()).contains(t);
            case SKILL, SKILL_TYPE ->
                    v.getSkillType() != null && safe(v.getSkillType().toString()).contains(t);
        };
    }

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }
}
