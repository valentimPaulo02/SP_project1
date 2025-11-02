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

    @PostMapping("/run/deid")
    public LinkageSummary runDeid() {
        return service.runLinkageAttack("deid");
    }

    @PostMapping("/run/kanon")
    public LinkageSummary runKanon() {
        return service.runLinkageAttack("kanon");
    }
}
