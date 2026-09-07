package hr.tvz.projekt.app;

import hr.tvz.projekt.entities.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AppState implements Serializable {

    private List<Skill> skills;
    private List<Volunteer> volunteers;
    private List<Event> events;
    private Map<Volunteer, Assignment> assignments;
    private List<Organizer> organizers;

    public AppState(List<Skill> skills, List<Volunteer> volunteers, List<Event> events, Map<Volunteer, Assignment> assignments, List<Organizer> organizers) {
        this.skills = skills;
        this.volunteers = volunteers;
        this.events = events;
        this.assignments = assignments;
        this.organizers = organizers;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public List<Volunteer> getVolunteers() {
        return volunteers;
    }

    public List<Event> getEvents() {
        return events;
    }

    public Map<Volunteer, Assignment> getAssignments() {
        return assignments;
    }

    public List<Organizer> getOrganizers() {
        return organizers;
    }
}

