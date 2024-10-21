package web.rempro_api.followed;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import web.rempro_api.utils.dto.FollowedRequest;

import java.security.Principal;
import java.util.List;

@RequestMapping("/followed")
@Tag(name = "Followed", description = "API for managing Followed items")
@RestController
@RequiredArgsConstructor
public class FollowedController {

    private final FollowedService followedService;

    @Operation(summary = "Create Followed", description = "Creates a new Followed item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Followed created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<Followed> createFollowed(@RequestBody FollowedRequest request, Principal principal) {
        String username = principal.getName();
        Followed follow = followedService.createFollowed(request, username);
        return ResponseEntity.status(201).body(follow);
    }

    @Operation(summary = "Get Followed by ID", description = "Retrieves a Followed item by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Followed found"),
            @ApiResponse(responseCode = "404", description = "Followed not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Followed> getFollowedById(@PathVariable Long id) {
        return followedService.getFollowedById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get All Followed", description = "Retrieves all Followed items.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of Followed items"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<Followed>> getAllFollowed() {
        List<Followed> followedList = followedService.getAllFollowed();
        return ResponseEntity.ok(followedList);
    }

    @Operation(summary = "Update Followed", description = "Updates an existing Followed item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Followed updated successfully"),
            @ApiResponse(responseCode = "404", description = "Followed not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Followed> updateFollowed(@PathVariable Long id, @RequestBody FollowedRequest request) {
        Followed updatedFollowed = followedService.updateFollowed(id, request);
        return ResponseEntity.ok(updatedFollowed);
    }

    @Operation(summary = "Delete Followed", description = "Deletes a Followed item by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Followed deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Followed not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFollowed(@PathVariable Long id) {
        followedService.deleteFollowed(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get All Followed by User", description = "Retrieves all Followed items created by the logged-in user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of Followed items created by the user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user")
    public ResponseEntity<List<Followed>> getAllFollowedByUser(Principal principal) {
        String username = principal.getName();
        List<Followed> followedList = followedService.getAllFollowedByUser(username);
        return ResponseEntity.ok(followedList);
    }
}
