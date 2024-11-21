package web.rempro_api.utils.exception;

public class CustomAuthException extends RuntimeException {
    
    private final int statusCode;

    // Constructeur pour personnaliser le message et le code d'état HTTP
    public CustomAuthException(String message) {
        super(message);
        this.statusCode = 400;  // Valeur par défaut, vous pouvez la modifier si nécessaire
    }

    // Constructeur permettant de définir un message et un code d'état HTTP
    public CustomAuthException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    // Récupérer le code d'état HTTP
    public int getStatusCode() {
        return statusCode;
    }
}

