package camera.utils;

public final class StringUtils {
    private StringUtils() {
    }

    public static String sanitize(String value) {
        if (value == null) {
            return "";
        }

        return value.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
