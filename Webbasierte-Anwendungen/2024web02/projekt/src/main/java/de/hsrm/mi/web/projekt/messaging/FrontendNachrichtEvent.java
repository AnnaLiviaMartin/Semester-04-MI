package de.hsrm.mi.web.projekt.messaging;

public record FrontendNachrichtEvent(NachrichtenTyp typ, long id, Operation operation) {

}