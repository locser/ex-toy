package locser.controller.http;
// Spring Security Core

import java.util.Map;
import locser.application.services.user.UserApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class UserController {

  private final UserApplicationService userApplicationService;

  public UserController(UserApplicationService userApplicationService) {
    this.userApplicationService = userApplicationService;
  }

//  @GetMapping("/login/google")
//  public ResponseEntity<?> loginGoogle(Authentication authentication) {
//    if (authentication instanceof OAuth2AuthenticationToken) {
//      OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
//      System.out.println(oauth2Token.getPrincipal());
//      // Lấy user attributes từ Google
//      Map<String, Object> attributes = oauth2Token.getPrincipal().getAttributes();
//
//      // Tạo response object
//      User user = new User();
//      user.setEmail(attributes.get("email").toString());
//      user.setName(attributes.get("name").toString());
//      user.setPicture(attributes.get("picture").toString());
//      user.setGoogleId(attributes.get("sub").toString());
//
//      User existingUser = userApplicationService.loginGoogle(user);
//
//      return ResponseEntity.ok(existingUser);
//    }
//
//    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
//  }

  @GetMapping("/login/error")
  public Map<String, Object> loginError() {
    return Map.of(
        "error", "true");
  }

  // http://localhost:1122/login/oauth2/code/google?code=4/0Aa5w1234567890
  @GetMapping("/login/oauth2/code/google")
  public Map<String, Object> google(@RequestParam("code") String code) {
    System.out.println("code: " + code);
    return Map.of(
        "name", "test",
        "email", "test@test.com",
        "picture", "test",
        "attributes", "test");
  }
}