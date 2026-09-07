package hr.tvz.projekt.app;
import hr.tvz.projekt.entities.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;


public final class AppState implements Serializable {

    private final ObservableList<Event> allEvents =  FXCollections.observableArrayList();
    public ObservableList<Event> getAllEvents() {
        return allEvents;
    }

    private final ObservableList<Organizer> allOrganizers =  FXCollections.observableArrayList();
    public ObservableList<Organizer> getAllOrganizers() {
        return allOrganizers;
    }

    private final ObservableList<Volunteer> allVolunteers =  FXCollections.observableArrayList();
    public ObservableList<Volunteer> getAllVolunteers() {
        return allVolunteers;
    }
    private final StringProperty lastVolunteerText = new SimpleStringProperty("Nema zapisa u bazi.");
    public StringProperty lastVolunteerTextProperty() { return lastVolunteerText; }
    public void setLastVolunteerText(String text) { lastVolunteerText.set(text); }
}

