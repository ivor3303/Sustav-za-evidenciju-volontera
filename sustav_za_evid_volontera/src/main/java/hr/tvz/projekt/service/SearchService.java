package hr.tvz.projekt.service;

import hr.tvz.projekt.entities.Assignment;
import hr.tvz.projekt.entities.Event;
import hr.tvz.projekt.entities.Volunteer;

import java.util.*;
import java.util.stream.Collectors;

public class SearchService {

    public List<Volunteer> searchVolunteersByName(List<Volunteer> volunteers, String term) {
        if (term == null) term = "";
        String t = term.toLowerCase();

        return volunteers.stream()
                .filter(Objects::nonNull)
                .filter(v -> v.getName() != null)
                .filter(v -> v.getName().toLowerCase().contains(t))
                .toList();
    }

    public List<Event> searchEventsByCity(List<Event> events, String city) {
        if (city == null) city = "";
        String c = city.toLowerCase();

        return events.stream()
                .filter(Objects::nonNull)
                .filter(e -> e.getCity() != null)
                .filter(e -> e.getCity().toLowerCase().contains(c))
                .toList();
    }

    public Optional<Assignment> findAssignmentForVolunteer(Map<Volunteer, Assignment> assignments, Volunteer v) {
        return Optional.ofNullable(assignments.get(v));
    }

    public List<Volunteer> sortVolunteersByName(List<Volunteer> volunteers) {
        return volunteers.stream()
                .sorted(Comparator.comparing(v -> v.getName().toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Volunteer> sortVolunteersByAge(List<Volunteer> volunteers) {
        return volunteers.stream()
                .sorted(Comparator.comparingInt(Volunteer::getAge))
                .collect(Collectors.toList());
    }

    public List<Event> sortEventsByCityThenTitle(List<Event> events) {
        return events.stream()
                .sorted(Comparator.comparing(Event::getCity, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Event::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }
}
