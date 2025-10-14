package pt.unl.fct.pds.proj1server.controller;

import pt.unl.fct.pds.proj1server.service.KAnonService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/kanon")
public class KAnonController {

    private final KAnonService kAnonymizationService;

    public KAnonController(KAnonService kAnonymizationService) {
        this.kAnonymizationService = kAnonymizationService;
    }


    @PostMapping("/run")
    public Map<String, Integer> runKAnonymity() {
        Map<String, Integer> res = new HashMap<>();
        try { kAnonymizationService.runKAnonymization(); res.put("success", 1); }
        catch (Exception e) { e.printStackTrace(); res.put("success", 0); }
        return res;
    }
}
