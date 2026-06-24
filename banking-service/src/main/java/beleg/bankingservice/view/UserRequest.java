package beleg.bankingservice.view;

import jakarta.validation.constraints.NotBlank;

/**
 * Request-DTO für das Anlegen und Aktualisieren eines Users.
 *
 * @NotBlank sorgt dafür, dass Spring automatisch 400 zurückgibt,
 * wenn das Feld fehlt oder nur Leerzeichen enthält.
 */
public record UserRequest(
        @NotBlank(message = "first_name darf nicht leer sein")
        String first_name,

        @NotBlank(message = "last_name darf nicht leer sein")
        String last_name
) {}