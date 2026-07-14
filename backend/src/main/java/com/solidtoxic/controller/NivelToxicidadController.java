package com.solidtoxic.controller;

import com.solidtoxic.model.NivelToxicidad;
import com.solidtoxic.model.dto.NivelToxicidadDTO;
import com.solidtoxic.service.NivelToxicidadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/toxicidad")
public class NivelToxicidadController {

    private final NivelToxicidadService service;

    public NivelToxicidadController(NivelToxicidadService service) {
        this.service = service;
    }

    @GetMapping
    public List<NivelToxicidad> getAll(@RequestParam(required = false) String state) {
        return state != null ? service.getByState(state) : service.getAll();
    }

    @GetMapping("/{id}")
    public NivelToxicidad getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<NivelToxicidad> create(@RequestBody NivelToxicidadDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public NivelToxicidad update(@PathVariable int id, @RequestBody NivelToxicidadDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<Void> updateState(@PathVariable int id, @RequestBody Map<String, String> body) {
        service.updateState(id, body.get("estReg"));
        return ResponseEntity.ok().build();
    }
}
