package hr.tvz.projekt.util;

public final class DialogUtil {

    private DialogUtil() {
        // util klasa – ne instancira se
    }

    public static void info(String title, String message) {
        System.out.println("[" + title + "] " + message);
    }

    public static void error(String title, String message) {
        System.out.println("❌ [" + title + "] " + message);
    }
}
