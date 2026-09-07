package hr.tvz.projekt.entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * Predstavlja vještinu volontera u sustavu evidencije.
 * <p>
 * Klasa sadrži tip vještine (enum), njezin naziv i opisnu kategoriju.
 *
 * @author Ivor Županić
 * @version 1.1
 * @since Java 25
 * @see Volunteer
 */
public class Skill implements Serializable {

    private final SkillType type;      // Enum – glavna grupa vještine
    private String skillName;    // Naziv vještine
    private String category;     // Dodatni opis / kategorija

    /**
     * Konstruktor za stvaranje nove vještine.
     *
     * @param type     tip vještine (enum)
     * @param skillName naziv vještine
     * @param category dodatna kategorija ili opis
     */
    public Skill(SkillType type, String skillName, String category) {
        this.type = type;
        this.skillName = skillName;
        this.category = category;
    }

    public SkillType getType() {
        return type;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skill)) return false;
        Skill skill = (Skill) o;
        return type == skill.type &&
                Objects.equals(skillName, skill.skillName) &&
                Objects.equals(category, skill.category);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, skillName, category);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return type + " - " + skillName + " (" + category + ")";
    }
}
