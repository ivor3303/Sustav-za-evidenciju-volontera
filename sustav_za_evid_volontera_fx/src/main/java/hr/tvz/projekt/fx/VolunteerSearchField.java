package hr.tvz.projekt.fx;

public enum VolunteerSearchField {

    NAME("Ime i prezime"),
    CITY("Grad"),
    EMAIL("Email"),
    SKILL("Vještina"),
    SKILL_TYPE("Tip vještine");

    private final String label;

    VolunteerSearchField(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
