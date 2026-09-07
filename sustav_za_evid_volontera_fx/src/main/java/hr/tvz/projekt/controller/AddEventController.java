package hr.tvz.projekt.controller;

import hr.tvz.projekt.app.AppState;
import hr.tvz.projekt.entities.Event;
import hr.tvz.projekt.entities.EventType;
import hr.tvz.projekt.repository.jdbc.EventJdbcRepository;
import hr.tvz.projekt.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Optional;
public class AddEventController {

    @FXML private TextField cityField;
    @FXML private TextField titleField;
    @FXML private TextField addressField;
    @FXML private ComboBox<EventType> typeCombo;
    @FXML private Label statusLabel;

    private Event createdEvent; // ovdje spremimo rezultat

    private AppState state;

    public void setStateAppState(AppState state) {
        this.state = state;
    }

    @FXML
    public void initialize() {
        typeCombo.getItems().setAll(EventType.values());
        typeCombo.getSelectionModel().selectFirst();
    }

    public Optional<Event> getCreatedEvent() {
        return Optional.ofNullable(createdEvent);
    }

    private EventJdbcRepository eventRepo;
    public void setEventRepo(EventJdbcRepository eventRepo) {
        this.eventRepo = eventRepo;
    }

    @FXML
    private void save() {
        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        String city = cityField.getText() == null ? "" : cityField.getText().trim();
        String address = addressField.getText() == null ? "" : addressField.getText().trim();
        EventType type = typeCombo.getValue();

        if (title.isEmpty() || city.isEmpty() || address.isEmpty() || type == null ) {
            AlertUtils.error("Neispravan unos", "Sva polja su obavezna.");
            return;
        }

        createdEvent = new Event(title, city, address, type);

        eventRepo.save(createdEvent);
        state.getAllEvents().add(createdEvent);



        statusLabel.setText("Događaj spremljen (u memoriji).");
    }

    @FXML
    private void cancel() {
        createdEvent = null;
        statusLabel.setText("Odustao si od unosa.");

        titleField.clear();
        cityField.clear();
        addressField.clear();
        typeCombo.getSelectionModel().selectFirst();
    }
}
