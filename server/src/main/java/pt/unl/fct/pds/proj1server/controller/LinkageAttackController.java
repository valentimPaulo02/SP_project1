package pt.unl.fct.pds.proj1server.controller;

import org.springframework.web.bind.annotation.*;

import pt.unl.fct.pds.proj1server.DTO.LinkageSummary;
import pt.unl.fct.pds.proj1server.service.LinkageAttackService;

@RestController
@RequestMapping("/api/attack")
public class LinkageAttackController {

    private final LinkageAttackService service;

    public LinkageAttackController(LinkageAttackService service) {
        this.service = service;
    }

    @PostMapping("/run")
    public LinkageSummary runLinkage() {
        return service.runLinkageAttack();
    }
  
}
