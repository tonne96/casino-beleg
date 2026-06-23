package beleg.bankingservice.controller.user;

import beleg.bankingservice.view.BalanceAdjustRequest;
import beleg.bankingservice.view.DeletedUserView;
import beleg.bankingservice.view.UserRequest;
import beleg.bankingservice.view.UserView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/casino/bank/api")
public interface IUserController {

    @GetMapping("/user/{id}")
    ResponseEntity<UserView> getUser(@PathVariable Long id);

    @GetMapping("/users")
    ResponseEntity<List<UserView>> getAllUsers();

    @PostMapping("/user")
    ResponseEntity<UserView> createUser(@Valid @RequestBody UserRequest request);

    @PutMapping("/user/{user_id}")
    ResponseEntity<UserView> updateUser(
            @PathVariable Long user_id,
            @Valid @RequestBody UserRequest request);

    @DeleteMapping("/user/{user_id}")
    ResponseEntity<DeletedUserView> deleteUser(@PathVariable Long user_id);

    @PostMapping("/user/{user_id}/deposit/{amount}/{decimals}")
    ResponseEntity<UserView> deposit(
            @PathVariable Long user_id,
            @PathVariable long amount,
            @PathVariable int decimals);

    @PutMapping("/user/{user_id}/balance/adjust")
    ResponseEntity<UserView> adjustBalance(
            @PathVariable Long user_id,
            @Valid @RequestBody BalanceAdjustRequest request);
}
