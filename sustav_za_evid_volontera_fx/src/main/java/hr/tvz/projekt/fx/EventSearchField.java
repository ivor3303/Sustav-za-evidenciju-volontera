package hr.tvz.projekt.fx;

public enum EventSearchField {
    TITLE("Naziv"),
    CITY("Grad"),
    ADDRESS("Adresa"),
    TYPE("Tip događaja");

    private final String label;

    EventSearchField(String label) { this.label = label; }

    @Override public String toString() { return label; }
}
