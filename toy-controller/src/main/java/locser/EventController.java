package locser;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EventController {

  private final EventController eventController;

  public EventController(EventController eventController) {
    this.eventController = eventController;
  }
}