package hr.tvz.projekt.service;

import hr.tvz.projekt.entities.Assignment;
import hr.tvz.projekt.entities.Person;
import hr.tvz.projekt.entities.SkillType;
import hr.tvz.projekt.entities.Volunteer;

import java.util.*;
import java.util.stream.Collectors;

public class StatsService {

    public Optional<Assignment> maxAssignment(Map<?, Assignment> assignments) {
        return assignments.values().stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(Assignment::getDurationHours));
    }

    public Optional<Assignment> minAssignment(Map<?, Assignment> assignments) {
        return assignments.values().stream()
                .filter(Objects::nonNull)
                .min(Comparator.comparingInt(Assignment::getDurationHours));
    }

    public Optional<Map.Entry<Volunteer, Assignment>> volunteerWithMostHours(Map<Volunteer, Assignment> assignments) {
        return assignments.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .max(Comparator.comparingInt(e -> e.getValue().getDurationHours()));
    }

    public Map<Boolean, List<Volunteer>> partitionAdults(List<Volunteer> volunteers) {
        return volunteers.stream()
                .collect(Collectors.partitioningBy(v -> v.getAge() >= 18));
    }

    public Map<SkillType, List<Volunteer>> groupBySkillType(List<Volunteer> volunteers) {
        return volunteers.stream()
                .collect(Collectors.groupingBy(v -> v.getSkill().getType()));
    }

    public Optional<Person> youngest(List<? extends Person> persons) {
        return persons.stream().min(Comparator.comparingInt(Person::getAge)).map(p -> (Person) p);
    }

    public Optional<Person> oldest(List<? extends Person> persons) {
        return persons.stream().max(Comparator.comparingInt(Person::getAge)).map(p -> (Person) p);
    }
}
