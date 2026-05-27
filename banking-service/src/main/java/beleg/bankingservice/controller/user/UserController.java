package beleg.bankingservice.controller.user;

import beleg.bankingservice.handler.user.UserHandler;
import beleg.bankingservice.view.UserRequest;
import beleg.bankingservice.view.UserView;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller nimmt HTTP-Requests von außen etgegen und gibt HTTP-Responses zurück

@RestController
@RequestMapping("/casino/bank/api")
public class UserController {

    private final UserHandler userHandler;

    public UserController(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    /**
     * GET /casino/bank/api/user/{id}
     * Liefert einen einzelnen User anhand seiner id
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<UserView> getUser(@PathVariable Long id) {
        return userHandler.getUser(id)
                .map(user -> ResponseEntity.ok(UserView.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /casino/bank/api/users
     * Liefert alle Users
     */
    @GetMapping("/users")
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
    @PostMapping("/user")
    public ResponseEntity<UserView> createUser(@Valid @RequestBody UserRequest request) {
        try {
            var user = userHandler.createUser(request.first_name(), request.last_name());
            return ResponseEntity.status(HttpStatus.CREATED).body(UserView.from(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /casino/bank/api/user/{user_id}
     * Aktualisiert den Namen eines bestehenden Users
     */
    @PutMapping("/user/{user_id}")
    public ResponseEntity<UserView> updateUser(
            @PathVariable Long user_id,
            @Valid @RequestBody UserRequest request) {
        try {
            return userHandler.updateUser(user_id, request.first_name(), request.last_name())
                    .map(user -> ResponseEntity.ok(UserView.from(user)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /casino/bank/api/user/{user_id}
     * Löscht einen User. Gibt den gelöschten User zurück (ohne ID)
     */
    @DeleteMapping("/user/{user_id}")
    public ResponseEntity<UserView> deleteUser(@PathVariable Long user_id) {
        return userHandler.deleteUser(user_id)
                .map(user -> ResponseEntity.ok(UserView.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /casino/bank/api/user/{user_id}/deposit/{amount}/{decimals}
     * Führt eine Einzahlung durch.
     * Beispiel: /deposit/10/50 → +10.50
     */
    @PostMapping("/user/{user_id}/deposit/{amount}/{decimals}")
    public ResponseEntity<UserView> deposit(
            @PathVariable Long user_id,
            @PathVariable long amount,
            @PathVariable int decimals) {
        try {
            return userHandler.deposit(user_id, amount, decimals)
                    .map(user -> ResponseEntity.ok(UserView.from(user)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}