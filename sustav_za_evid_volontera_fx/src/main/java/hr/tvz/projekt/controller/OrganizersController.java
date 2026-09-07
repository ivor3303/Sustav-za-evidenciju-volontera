package hr.tvz.projekt.controller;

import hr.tvz.projekt.app.AppState;
import hr.tvz.projekt.entities.Organizer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Locale;

public class OrganizersController {

    // SEARCH UI
    @FXML private ComboBox<OrganizerSearchField> fieldCombo;
    @FXML private TextField valueField;

    // TABLE UI
    @FXML private TableView<Organizer> table;
    @FXML private TableColumn<Organizer, String> colName;
    @FXML private TableColumn<Organizer, String> colCity;
    @FXML private TableColumn<Organizer, String> colEmail;
    @FXML private TableColumn<Organizer, String> colAge;

    private AppState appState;

    public void setAppState(AppState appState) {
        this.appState = appState;

        // kad MainController pozove setAppState nakon loader.load(), elementi su već inicijalizirani
        if (table != null && appState != null && appState.getAllOrganizers() != null) {
            table.setItems(appState.getAllOrganizers());
        }
    }

    @FXML
    public void initialize() {
        // combo izbor polja pretrage
        if (fieldCombo != null) {
            fieldCombo.setItems(FXCollections.observableArrayList(OrganizerSearchField.values()));
            fieldCombo.getSelectionModel().select(OrganizerSearchField.NAME);
        }

        // stupci tablice
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        colCity.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCity()));
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        colAge.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getAge()))
        );


        // ako appState već postoji (rijetko), prikaži odmah
        if (appState != null && appState.getAllOrganizers() != null) {
            table.setItems(appState.getAllOrganizers());
        }
    }

    @FXML
    private void onSearch() {
        if (appState == null || appState.getAllOrganizers() == null) return;

        String term = valueField.getText() == null ? "" : valueField.getText().trim();
        OrganizerSearchField field = fieldCombo.getValue();

        if (term.isBlank()) {
            table.setItems(appState.getAllOrganizers());
            return;
        }

        List<Organizer> filtered = appState.getAllOrganizers().stream()
                .filter(o -> matches(o, field, term))
                .toList();

        table.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void onReset() {
        valueField.clear();
        if (appState != null && appState.getAllOrganizers() != null) {
            table.setItems(appState.getAllOrganizers());
        }
    }

    private boolean matches(Organizer o, OrganizerSearchField field, String term) {
        String t = term.toLowerCase(Locale.ROOT);

        return switch (field) {
            case NAME -> safe(o.getName()).contains(t);
            case CITY -> safe(o.getCity()).contains(t);
            case EMAIL -> safe(o.getEmail()).contains(t);
            case AGE -> String.valueOf(o.getAge()).contains(t);
        };
    }

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }

    // Enum može biti i u zasebnoj datoteci, ali ovako ti je najbrže.
    public enum OrganizerSearchField {
        NAME, CITY, EMAIL, AGE
    }
}
