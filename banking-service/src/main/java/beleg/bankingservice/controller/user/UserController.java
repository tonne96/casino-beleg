package beleg.bankingservice.controller.user;

import beleg.bankingservice.handler.user.UserHandler;
import beleg.bankingservice.view.BalanceAdjustRequest;
import beleg.bankingservice.view.DeletedUserView;
import beleg.bankingservice.view.UserRequest;
import beleg.bankingservice.view.UserView;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller nimmt HTTP-Requests von außen etgegen und gibt HTTP-Responses zurück

@RestController
public class UserController implements IUserController {

    private final UserHandler userHandler;

    public UserController(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    /**
     * GET /casino/bank/api/user/{id}
     * Liefert einen einzelnen User anhand seiner id
     */
    @Override
    public ResponseEntity<UserView> getUser(Long id) {
        return userHandler.getUser(id)
                .map(user -> ResponseEntity.ok(UserView.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /casino/bank/api/users
     * Liefert alle Users
     */
    @Override
    public ResponseEntity<List<UserView>> getAllUsers() {
        List<UserView> users = userHandler.getAllUsers()
                .stream()
                .map(UserView::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    /**
     * POST /casino/bank/api/user
     * Legt einen neuen User an
     */
    @Override
    public ResponseEntity<UserView> createUser(UserRequest request) {
        var user = userHandler.createUser(request.first_name(), request.last_name());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserView.from(user));
    }

    /**
     * PUT /casino/bank/api/user/{user_id}
     * Aktualisiert den Namen eines bestehenden Users
     */
    @Override
    public ResponseEntity<UserView> updateUser(
            Long user_id,
            UserRequest request) {
        return userHandler.updateUser(user_id, request.first_name(), request.last_name())
                .map(user -> ResponseEntity.ok(UserView.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /casino/bank/api/user/{user_id}
     * Löscht einen User. Gibt den gelöschten User zurück (ohne ID laut Spec)
     */
    @Override
    public ResponseEntity<DeletedUserView> deleteUser(Long user_id) {
        return userHandler.deleteUser(user_id)
                .map(user -> ResponseEntity.ok(DeletedUserView.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /casino/bank/api/user/{user_id}/balance/adjust
     * Passt den Kontostand an (positiv oder negativ).
     * Wird vom Transaction-Subdomain via HTTP-Self-Call aufgerufen.
     */
    @Override
    public ResponseEntity<UserView> adjustBalance(Long user_id, BalanceAdjustRequest request) {
        return userHandler.adjustBalance(user_id, request.amount())
                .map(user -> ResponseEntity.ok(UserView.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /casino/bank/api/user/{user_id}/deposit/{amount}/{decimals}
     * Führt eine Einzahlung durch.
     * Beispiel: /deposit/10/50 → +10.50
     */
    @Override
    public ResponseEntity<UserView> deposit(
            Long user_id,
            long amount,
            int decimals) {
        return userHandler.deposit(user_id, amount, decimals)
                .map(user -> ResponseEntity.ok(UserView.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}