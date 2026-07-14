package com.solidtoxic.controller;

import com.solidtoxic.model.Constituyente;
import com.solidtoxic.model.dto.ConstituyenteDTO;
import com.solidtoxic.service.ConstituyenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/constituyente")
public class ConstituyenteController {

    private final ConstituyenteService service;

    public ConstituyenteController(ConstituyenteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Constituyente> getAll(@RequestParam(required = false) String state) {
        return state != null ? service.getByState(state) : service.getAll();
    }

    @GetMapping("/{id}")
    public Constituyente getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Constituyente> create(@RequestBody ConstituyenteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public Constituyente update(@PathVariable String id, @RequestBody ConstituyenteDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<Void> updateState(@PathVariable String id, @RequestBody Map<String, String> body) {
        service.updateState(id, body.get("estReg"));
        return ResponseEntity.ok().build();
    }
}
