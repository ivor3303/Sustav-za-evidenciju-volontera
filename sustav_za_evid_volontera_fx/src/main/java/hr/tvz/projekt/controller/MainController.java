package hr.tvz.projekt.controller;
import hr.tvz.projekt.app.AppState;

import hr.tvz.projekt.entities.Volunteer;
import hr.tvz.projekt.repository.jdbc.EventJdbcRepository;
import hr.tvz.projekt.repository.jdbc.OrganizerJdbcRepository;
import hr.tvz.projekt.repository.jdbc.VolunteerJdbcRepository;
import hr.tvz.projekt.util.AlertUtils;
import hr.tvz.projekt.util.BackupUtil;
import hr.tvz.projekt.util.SchemaUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
public class MainController {

    @FXML
    private StackPane contentPane;
    private final VolunteerJdbcRepository volunteerRepo = new VolunteerJdbcRepository();
    private final EventJdbcRepository eventRepo = new EventJdbcRepository();
    private final OrganizerJdbcRepository organizerRepo = new OrganizerJdbcRepository();

    private AppState appState =  new AppState();
    @FXML
    public void initialize() {
        lastVolunteerLabel.textProperty().bind(appState.lastVolunteerTextProperty());
        appState.setLastVolunteerText("Učitavam posljednji zapis...");
        Thread.startVirtualThread(() -> {
            try {
                SchemaUtil.init();
                var volunteers = volunteerRepo.findAll();
                var events = eventRepo.findAll();
                var organizers = organizerRepo.findAll();

                var lastV = volunteerRepo.findLastInserted();

                Platform.runLater(() -> {
                    appState.getAllVolunteers().setAll(volunteers);
                    appState.getAllEvents().setAll(events);
                    appState.getAllOrganizers().setAll(organizers);

                    String text = lastV
                            .map(v -> v.getName() + " (" + v.getCity() + ", " + v.getSkillType() + ")")
                            .orElse("Nema zapisa u bazi.");

                    lastVolunteerLabel.setText(text);
                });

            } catch (Exception ex) {
                Platform.runLater(() ->
                        AlertUtils.error("Greška", "Neuspjelo učitavanje podataka: " + ex.getMessage())
                );
            }
        });
    }
    private String formatLastVolunteer(Volunteer v) {
        return v.getName() + " (" + v.getCity() + ", " + v.getSkillType() + ")";
    }
    @FXML
    private void showVolunteers() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/hr/tvz/projekt/VolunteersView.fxml")
            );
            Parent view = loader.load();

            VolunteersController vc = loader.getController();
            vc.setAppState(appState);
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            AlertUtils.error("Greška", "Ne mogu...");
        }
    }
    @FXML
    private void showEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/hr/tvz/projekt/EventsView.fxml")
            );
            Parent view = loader.load();

            EventsController ec = loader.getController();
            ec.setAppState(appState);

            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            AlertUtils.error("Greška", "Ne mogu...");
        }
    }
    @FXML
    private void showStats() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/hr/tvz/projekt/StatsView.fxml")
            );
            Parent view = loader.load();

            StatsController sc = loader.getController();
            sc.setData(appState.getAllVolunteers(), appState.getAllEvents());
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            AlertUtils.error("Greška", "Ne mogu...");

        }
    }
    @FXML
    private void showOrganizers() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/hr/tvz/projekt/OrganizersView.fxml")
            );
            Parent view = loader.load();

            OrganizersController oc = loader.getController();
            oc.setAppState(appState); // ili oc.setOrganizers(...)

            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            AlertUtils.error("Greška", "Ne mogu...");
        }
    }
    @FXML
    private void addEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/hr/tvz/projekt/AddEventView.fxml")
            );
            Parent view = loader.load();
            AddEventController aec = loader.getController();
            aec.setStateAppState(appState);
            aec.setEventRepo(eventRepo);
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.error("Greška", "Ne mogu...");
        }
    }
    @FXML
    private void addVolunteer() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/hr/tvz/projekt/AddVolunteerView.fxml")
            );
            Parent view = loader.load();
            AddVolunteerController avc = loader.getController();
            avc.setStateAppState(appState);
            avc.setVolunteerRepo(volunteerRepo);
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.error("Greška", "Ne mogu...");
        }
    }
    @FXML
    private void addOrganizer() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/hr/tvz/projekt/AddOrganizerView.fxml")
            );
            Parent view = loader.load();
            AddOrganizerController aoc = loader.getController();
            aoc.setStateAppState(appState);
            aoc.setOrganizerRepo(organizerRepo);
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.error("Greška", "Ne mogu...");
        }
    }
    @FXML
    private Label lastVolunteerLabel;
    @FXML
    private void backupVolunteers() {
        Thread.startVirtualThread(() -> {
            try {
                int copied = BackupUtil.backupTable("volunteers");
                Platform.runLater(() ->
                        AlertUtils.info("Backup", "volunteers_BACKUP kreiran. Broj redaka: " + copied)
                );
            } catch (Exception ex) {
                Platform.runLater(() ->
                        AlertUtils.error("Backup greška", ex.getMessage())
                );
            }
        });
    }
}
