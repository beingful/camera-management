package tests.sessionfile;

public record VideoSessionFileTestCase(
        String name,
        String originalFileName,
        String configuredExtension,
        String expectedExtension) {
    @Override
    public String toString() {
        return name;
    }
}

