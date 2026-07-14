package com.solidtoxic.controller;

import com.solidtoxic.model.ResiduoConstituyente;
import com.solidtoxic.model.dto.ResiduoConstituyenteDTO;
import com.solidtoxic.service.ResiduoConstituyenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Composite PK endpoints:
 *   GET    /api/v1/residuo-constituyente?state=A
 *   GET    /api/v1/residuo-constituyente/{codResiduo}/{codConstituyente}
 *   GET    /api/v1/residuo-constituyente/residuo/{codResiduo}   (all constituents for one residuo)
 *   POST   /api/v1/residuo-constituyente
 *   PUT    /api/v1/residuo-constituyente/{codResiduo}/{codConstituyente}
 *   PATCH  /api/v1/residuo-constituyente/{codResiduo}/{codConstituyente}/state
 */
@RestController
@RequestMapping("/api/v1/residuo-constituyente")
public class ResiduoConstituyenteController {

    private final ResiduoConstituyenteService service;

    public ResiduoConstituyenteController(ResiduoConstituyenteService service) {
        this.service = service;
    }

    @GetMapping
    public List<ResiduoConstituyente> getAll(@RequestParam(required = false) String state) {
        return state != null ? service.getByState(state) : service.getAll();
    }

    @GetMapping("/residuo/{codResiduo}")
    public List<ResiduoConstituyente> getByResiduo(@PathVariable String codResiduo) {
        return service.getByResiduo(codResiduo);
    }

    @GetMapping("/{codResiduo}/{codConstituyente}")
    public ResiduoConstituyente getByKey(@PathVariable String codResiduo,
                                          @PathVariable String codConstituyente) {
        return service.getByKey(codResiduo, codConstituyente);
    }

    @PostMapping
    public ResponseEntity<ResiduoConstituyente> create(@RequestBody ResiduoConstituyenteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{codResiduo}/{codConstituyente}")
    public ResiduoConstituyente update(@PathVariable String codResiduo,
                                        @PathVariable String codConstituyente,
                                        @RequestBody ResiduoConstituyenteDTO dto) {
        return service.update(codResiduo, codConstituyente, dto);
    }

    @PatchMapping("/{codResiduo}/{codConstituyente}/state")
    public ResponseEntity<Void> updateState(@PathVariable String codResiduo,
                                             @PathVariable String codConstituyente,
                                             @RequestBody Map<String, String> body) {
        service.updateState(codResiduo, codConstituyente, body.get("estReg"));
        return ResponseEntity.ok().build();
    }
}
