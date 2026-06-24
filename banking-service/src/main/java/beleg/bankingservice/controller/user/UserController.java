package beleg.bankingservice.controller.user;

import beleg.bankingservice.handler.user.UserHandler;
import beleg.bankingservice.model.User;
import beleg.bankingservice.view.BalanceAdjustRequest;
import beleg.bankingservice.view.DeletedUserView;
import beleg.bankingservice.view.UserRequest;
import beleg.bankingservice.view.UserView;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController implements IUserController {

    private final UserHandler userHandler;

    public UserController(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    /**
     * GET /casino/bank/api/user/{id}
     */
    @Override
    public ResponseEntity<UserView> getUser(Long id) {
        Optional<User> found = userHandler.getUser(id);

        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = found.get();
        UserView view = UserView.from(user);
        return ResponseEntity.ok(view);
    }

    /**
     * GET /casino/bank/api/users
     */
    @Override
    public ResponseEntity<List<UserView>> getAllUsers() {
        List<User> allUsers = userHandler.getAllUsers();

        List<UserView> views = new ArrayList<>();
        for (User user : allUsers) {
            views.add(UserView.from(user));
        }

        return ResponseEntity.ok(views);
    }

    /**
     * POST /casino/bank/api/user
     */
    @Override
    public ResponseEntity<UserView> createUser(UserRequest request) {
        User user = userHandler.createUser(request.first_name(), request.last_name());
        UserView view = UserView.from(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    /**
     * PUT /casino/bank/api/user/{user_id}
     */
    @Override
    public ResponseEntity<UserView> updateUser(Long user_id, UserRequest request) {
        Optional<User> updated = userHandler.updateUser(user_id, request.first_name(), request.last_name());

        if (updated.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = updated.get();
        UserView view = UserView.from(user);
        return ResponseEntity.ok(view);
    }

    /**
     * DELETE /casino/bank/api/user/{user_id}
     * Gibt den gelöschten User ohne ID zurück (laut Spec)
     */
    @Override
    public ResponseEntity<DeletedUserView> deleteUser(Long user_id) {
        Optional<User> deleted = userHandler.deleteUser(user_id);

        if (deleted.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = deleted.get();
        DeletedUserView view = DeletedUserView.from(user);
        return ResponseEntity.ok(view);
    }

    /**
     * POST /casino/bank/api/user/{user_id}/deposit/{amount}/{decimals}
     */
    @Override
    public ResponseEntity<UserView> deposit(Long user_id, long amount, int decimals) {
        Optional<User> updated = userHandler.deposit(user_id, amount, decimals);

        if (updated.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = updated.get();
        UserView view = UserView.from(user);
        return ResponseEntity.ok(view);
    }

    /**
     * PUT /casino/bank/api/user/{user_id}/balance/adjust
     * Wird vom Transaction-Subdomain via HTTP-Self-Call aufgerufen.
     */
    @Override
    public ResponseEntity<UserView> adjustBalance(Long user_id, BalanceAdjustRequest request) {
        Optional<User> updated = userHandler.adjustBalance(user_id, request.amount());

        if (updated.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = updated.get();
        UserView view = UserView.from(user);
        return ResponseEntity.ok(view);
    }
}
