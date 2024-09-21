package exampleUDP;

public enum RequestType {
    PING("PING"),
    PONG("PONG");

    private String request;

    RequestType(String request) {
        this.request = request;
    }

    public String getRequest() {
        return request;
    }
}
