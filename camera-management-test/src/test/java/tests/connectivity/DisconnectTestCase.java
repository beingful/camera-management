package tests.connectivity;

public record DisconnectTestCase(
        String name,
        String fixtureName) {
    @Override
    public String toString() {
        return name;
    }
}
