package hr.tvz.projekt.controller;

import hr.tvz.projekt.app.AppState;
import hr.tvz.projekt.entities.Event;
import hr.tvz.projekt.fx.EventSearchField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Locale;

public class EventsController {

    @FXML private ComboBox<EventSearchField> fieldCombo;
    @FXML private TextField valueField;

    @FXML private TableView<Event> table;
    @FXML private TableColumn<Event, String> colTitle;
    @FXML private TableColumn<Event, String> colCity;
    @FXML private TableColumn<Event, String> colAddress;
    @FXML private TableColumn<Event, String> colType;

    private AppState appState;

    /**
     * MainController mora pozvati ovo odmah nakon loader.load().
     */
    public void setAppState(AppState appState) {
        this.appState = appState;

        // Ako su FXML elementi već inicijalizirani, odmah prikaži listu
        if (table != null && appState != null && appState.getAllEvents() != null) {
            table.setItems(appState.getAllEvents());
        }
    }

    @FXML
    public void initialize() {
        fieldCombo.setItems(FXCollections.observableArrayList(EventSearchField.values()));
        fieldCombo.getSelectionModel().select(EventSearchField.TITLE);

        colTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));
        colCity.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCity()));
        colAddress.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));
        colType.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getType() != null ? d.getValue().getType().toString() : ""
        ));

        // Ako je appState već postavljen prije initialize (rijetko), prikaži listu
        if (appState != null && appState.getAllEvents() != null) {
            table.setItems(appState.getAllEvents());
        }
    }

    @FXML
    private void onSearch() {
        if (appState == null || appState.getAllEvents() == null) {
            return;
        }

        String term = valueField.getText() == null ? "" : valueField.getText().trim();
        EventSearchField field = fieldCombo.getValue();
        if (field == null) field = EventSearchField.TITLE;

        if (term.isBlank()) {
            table.setItems(appState.getAllEvents());
            return;
        }

        EventSearchField finalField = field;
        List<Event> filtered = appState.getAllEvents().stream()
                .filter(e -> matches(e, finalField, term))
                .toList();

        table.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void onReset() {
        valueField.clear();
        if (appState != null && appState.getAllEvents() != null) {
            table.setItems(appState.getAllEvents());
        }
    }

    private boolean matches(Event e, EventSearchField field, String term) {
        String t = term.toLowerCase(Locale.ROOT);

        return switch (field) {
            case TITLE -> safe(e.getTitle()).contains(t);
            case CITY -> safe(e.getCity()).contains(t);
            case ADDRESS -> safe(e.getAddress()).contains(t);
            case TYPE -> e.getType() != null && safe(e.getType().toString()).contains(t);
        };
    }

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }
}
