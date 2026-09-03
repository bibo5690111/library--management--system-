package util;

import exception.InvalidInputException;
import java.util.regex.Pattern;

public final class ValidationUtil {

    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{7,15}$");

    private static final Pattern ISBN_PATTERN =
            Pattern.compile("^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]{3}[- ]){3})[0-9]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]{3}[- ]){4})[0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$");

    // Private constructor to prevent instantiation
    private ValidationUtil() {}

    public static void validateNotNullOrEmpty(String value, String fieldName) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be null or empty.");
        }
    }

    public static void validateEmail(String email) throws InvalidInputException {
        validateNotNullOrEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidInputException("Invalid email format: " + email);
        }
    }

    public static void validatePhone(String phone) throws InvalidInputException {
        validateNotNullOrEmpty(phone, "Phone number");
        if (!PHONE_PATTERN.matcher(phone.replaceAll("[\\s-]", "")).matches()) {
            throw new InvalidInputException("Invalid phone number format: " + phone);
        }
    }

    public static void validateIsbn(String isbn) throws InvalidInputException {
        validateNotNullOrEmpty(isbn, "ISBN");
        if (!ISBN_PATTERN.matcher(isbn).matches()) {
            throw new InvalidInputException("Invalid ISBN format: " + isbn);
        }
    }

    public static void validateRating(double rating) throws InvalidInputException {
        if (rating < 0.0 || rating > 5.0) {
            throw new InvalidInputException("Rating must be between 0.0 and 5.0. Provided: " + rating);
        }
    }

    public static void validatePositive(int value, String fieldName) throws InvalidInputException {
        if (value <= 0) {
            throw new InvalidInputException(fieldName + " must be greater than zero.");
        }
    }
}