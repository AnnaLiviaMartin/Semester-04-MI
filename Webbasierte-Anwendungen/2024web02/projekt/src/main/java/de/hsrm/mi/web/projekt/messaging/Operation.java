package de.hsrm.mi.web.projekt.messaging;

import lombok.Getter;

@Getter
public enum Operation {
    GET("GET"),
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE");

    private final String operationName;

    Operation(String operationName) {
        this.operationName = operationName;
    }

}
