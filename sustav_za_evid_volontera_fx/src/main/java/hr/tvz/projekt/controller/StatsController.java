package hr.tvz.projekt.controller;

import hr.tvz.projekt.entities.Event;
import hr.tvz.projekt.entities.Volunteer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class StatsController {

    @FXML private Label lblVolunteers;
    @FXML private Label lblEvents;
    @FXML private Label lblYoungest;
    @FXML private Label lblOldest;
    @FXML private Label lblAvgAge;

    public void setData(List<Volunteer> volunteers, List<Event> events) {
        int vCount = volunteers == null ? 0 : volunteers.size();
        int eCount = events == null ? 0 : events.size();

        lblVolunteers.setText(String.valueOf(vCount));
        lblEvents.setText(String.valueOf(eCount));

        if (volunteers == null || volunteers.isEmpty()) {
            lblYoungest.setText("-");
            lblOldest.setText("-");
            lblAvgAge.setText("-");
            return;
        }

        Volunteer youngest = volunteers.stream()
                .min((a,b) -> Integer.compare(a.getAge(), b.getAge()))
                .orElse(null);

        Volunteer oldest = volunteers.stream()
                .max((a,b) -> Integer.compare(a.getAge(), b.getAge()))
                .orElse(null);

        double avg = volunteers.stream()
                .mapToInt(Volunteer::getAge)
                .average()
                .orElse(0);

        lblYoungest.setText(formatVolunteer(youngest));
        lblOldest.setText(formatVolunteer(oldest));
        lblAvgAge.setText(String.format("%.2f", avg));
    }

    private String formatVolunteer(Volunteer v) {
        if (v == null) return "-";
        return v.getName() + " (" + v.getAge() + ")";
    }
}
