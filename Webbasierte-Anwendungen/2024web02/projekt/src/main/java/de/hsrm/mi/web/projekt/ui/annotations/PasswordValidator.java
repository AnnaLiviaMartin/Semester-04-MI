package de.hsrm.mi.web.projekt.ui.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class PasswordValidator implements ConstraintValidator<GutesPasswort, String> {

    /**
     * checks if password is containing "17" or "siebzehn"
     * null and "" are valid too
     * @param value object to validate
     * @param context context in which the constraint is evaluated
     *
     * @return true if the password is valid
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(Objects.equals(value, "") || value == null) return true;

        value = value.toLowerCase();
        return value.contains("17") || value.contains("siebzehn");
    }

}
