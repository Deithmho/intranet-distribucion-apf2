package com.dwi.intranetdistribucion.controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
public class EstadoController {
    @io.swagger.v3.oas.annotations.security.SecurityRequirements
    @GetMapping("/api/v1/public/estado") public Map<String,String> estado() { return Map.of("estado","OK","proyecto","Intranet de distribución APF2"); }
}
