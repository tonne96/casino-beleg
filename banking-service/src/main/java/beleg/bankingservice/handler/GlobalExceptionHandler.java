package beleg.bankingservice.handler;

import beleg.bankingservice.view.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Globale Fehlerbehandlung für alle Controller.
 *
 * Statt in jedem Controller try/catch zu schreiben,
 * fängt diese Klasse alle Exceptions zentral ab und
 * gibt eine einheitliche Fehlermeldung zurück.
 *
 * SOLID — Single Responsibility:
 * Fehlerbehandlung ist hier zentralisiert, nicht verstreut
 * über alle Controller.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Fängt IllegalArgumentException ab → 400 Bad Request
     *
     * Wird geworfen wenn z.B.:
     * - Name ist leer
     * - Deposit-Betrag ist negativ
     * - InvoicingParty ist unbekannt
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        400,
                        "Bad Request",
                        ex.getMessage()
                ));
    }

    /**
     * Fängt @Valid Validierungsfehler ab → 400 Bad Request
     *
     * Wird geworfen wenn z.B.:
     * - first_name fehlt im Request Body
     * - last_name ist leer
     */
    /**
     * Fängt ungültige Pfad- oder Query-Parameter ab → 400 Bad Request
     * Beispiel: /deposit/abc/50 wirft diese Exception weil "abc" kein long ist
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        400,
                        "Bad Request",
                        "Ungültiger Wert für Parameter '" + ex.getName() + "': " + ex.getValue()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        // Alle Fehlermeldungen aus dem Request Body sammeln
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        400,
                        "Bad Request",
                        message
                ));
    }
}
