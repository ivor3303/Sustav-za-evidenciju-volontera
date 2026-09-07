package hr.tvz.projekt.util;

public final class ValidationUtil {

    private ValidationUtil() {
        // util class
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static int parseIntOrThrow(String text, String fieldName) {
        if (isBlank(text)) {
            throw new IllegalArgumentException("Polje '" + fieldName + "' je obavezno.");
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException _) {
            throw new IllegalArgumentException("Polje '" + fieldName + "' mora biti cijeli broj.");
        }
    }

    public static void requireAtLeastOneCriteria(String... criteria) {
        if (criteria == null || criteria.length == 0) {
            throw new IllegalArgumentException("Unesite barem jedan kriterij pretraživanja.");
        }
        for (String c : criteria) {
            if (!isBlank(c)) return;
        }
        throw new IllegalArgumentException("Unesite barem jedan kriterij pretraživanja.");
    }
}
