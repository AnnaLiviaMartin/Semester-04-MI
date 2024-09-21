package client_server;

public enum Protokoll {
    IS_OK("IS_OK"), NOT_OK("NOT_OK"), LOGIN("LOGIN"), REGISTER("REGISTER"), REQUEST_LIST("REQUEST_LIST"), REQUEST_INVITATION("REQUEST_INVITATION"), RECEIVED_INVITATION("RECEIVED_INVITATION"), INVITATION_ACCEPTED("INVITATION_ACCEPTED");

    private String text;

    private Protokoll(String text) {
        this.text = text;
    }


    public String getText() {
        return text;
    }
}
