package locser;

import locser.toy.domain.model.entity.Event;
import locser.toy.domain.service.EventDomainService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class HiController {

  private final EventDomainService eventDomainService;

  HiController(EventDomainService eventDomainService) {
    this.eventDomainService = eventDomainService;
  }

  // phải import th toy controller vào pom.xml của toy-starter để sử dụng
  @GetMapping("/public")
  public String publicEndpoint() {
    return "public";
  }

  @GetMapping("/detail")
  public Event detail() {
    System.out.println("aaaaaa");
    return this.eventDomainService.findOneById((long) 1);
  }
}