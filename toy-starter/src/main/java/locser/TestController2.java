package locser;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController2 {

    @GetMapping("/api/test2")
    public String test() {
        return "Test successful from TestController2!";
    }
}
