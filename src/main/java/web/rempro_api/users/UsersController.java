package web.rempro_api.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/users")
@Tag(name = "Users", description = "API routes for managing users")
@RestController
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @Operation(summary = "Update Password", description = "Updates the user's password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Current password is incorrect"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            Principal principal) {

        String username = principal.getName();
        usersService.updatePassword(username, currentPassword, newPassword);
        return ResponseEntity.ok("Password updated successfully");
    }

    @Operation(summary = "Delete User Account", description = "Deletes the logged-in user's account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User account deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Password is incorrect"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete-account")
    public ResponseEntity<Void> deleteUserAccount(
            @RequestParam String password,
            Principal principal) {

        String username = principal.getName();
        usersService.deleteUserAccount(username, password);
        return ResponseEntity.noContent().build();
    }
}
