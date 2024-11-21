package web.rempro_api.utils.exception;

import org.springframework.stereotype.Component;

/**
 * Utility class for handling authentication-related exceptions.
 * This class provides methods for creating custom exceptions to standardize error handling in the application.
 */
@Component
public class ExceptionAuthUtil {

    /**
     * Creates an exception indicating that an entity (e.g., a user or resource)
     * was not found based on its identifier.
     *
     * @param entity     The entity that was not found (e.g., "User").
     * @param identifier The identifier of the entity (e.g., the username or ID).
     * @return A custom exception indicating that the entity was not found.
     */
    public CustomAuthException createNotFoundException(String entity, Object identifier) {
        return new CustomAuthException(entity + " not found with identifier: " + identifier, 404);
    }

    /**
     * Creates a generic exception with a custom error message and an HTTP status code.
     *
     * @param message        The custom error message to be included in the exception.
     * @param httpStatusCode The HTTP status code to be associated with the exception.
     * @return A custom exception with the provided message and HTTP status code.
     */
    public CustomAuthException createGenericException(String message, int httpStatusCode) {
        return new CustomAuthException(message, httpStatusCode);
    }
}
