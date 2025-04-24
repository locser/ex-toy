package locser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleTestController {

    @GetMapping("/api/test-simple")
    public String testSimple() {
        return "Simple test successful!";
    }
    
    @GetMapping("/api-docs-test")
    public String apiDocsTest() {
        return "This is a test endpoint for API docs";
    }
}
