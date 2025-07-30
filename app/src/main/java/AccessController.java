// creates "/access/user" and "/access/developers" endpoints
// Authorization handled by @preAuthorize annotations
// as long as user is authenticated, access is granted to /access/user
// /access/developer is accessible only for users with developers authority

package custom.spring.saml;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/access")
public class AccessController {

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Map<String, Object> userGreeting(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome, SAML user! You're authorized since you are in the ROLE_USER role. This role is proivided to everyone who is authenticated.");

        if (principal != null) {
            response.put("username", principal.getName());
        } else {
            response.put("error", "No authenticated principal found.");
        }

        return response;
    }

    @GetMapping("/developer")
    @PreAuthorize("hasAuthority('developers')")
    public Map<String, Object> developersGreeting(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome, SAML developer! You're authorized since you are in the developers role.");

        if (principal != null) {
            response.put("username", principal.getName());
        } else {
            response.put("error", "No authenticated principal found.");
        }

        return response;
    }

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleExceptions(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Access denied or unexpected error");
        error.put("details", ex.getMessage());
        return error;
    }
}
