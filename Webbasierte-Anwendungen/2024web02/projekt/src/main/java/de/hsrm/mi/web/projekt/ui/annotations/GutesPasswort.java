package de.hsrm.mi.web.projekt.ui.annotations;

import jakarta.validation.*;
import java.lang.annotation.*;

/**
 * New Annotation @GutesPasswort
 * validity check in PasswortValidator.java
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=PasswordValidator.class)
@Documented
public @interface GutesPasswort {
    String message() default "Wert muss entweder 17 oder siebzehn beinhalten, null oder ein Leerstring sein"; //musste ich als default hinzufügen, sonst läuft der localhost auf einen Fehler
    Class<? extends Payload>[] payload() default { };
    Class<?>[] groups() default { };
}
