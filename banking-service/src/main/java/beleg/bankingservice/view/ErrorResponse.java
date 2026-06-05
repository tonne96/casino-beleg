package beleg.bankingservice.view;

/**
 * View (DTO) für Fehlermeldungen.
 * Wird bei 400 und 404 Responses zurückgegeben.
 *
 * Beispiel-Response:
 * {
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "firstName darf nicht leer sein."
 * }
 */
public record ErrorResponse(
        int status,
        String error,
        String message
) {}