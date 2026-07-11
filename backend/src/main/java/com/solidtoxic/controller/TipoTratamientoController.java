package com.solidtoxic.controller;

import com.solidtoxic.model.TipoTratamiento;
import com.solidtoxic.model.dto.TipoTratamientoDTO;
import com.solidtoxic.service.TipoTratamientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tratamiento")
public class TipoTratamientoController {

    private final TipoTratamientoService service;

    public TipoTratamientoController(TipoTratamientoService service) {
        this.service = service;
    }

    @GetMapping
    public List<TipoTratamiento> getAll(@RequestParam(defaultValue = "A") String state) {
        return service.getByState(state);
    }

    @GetMapping("/{id}")
    public TipoTratamiento getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<TipoTratamiento> create(@RequestBody TipoTratamientoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public TipoTratamiento update(@PathVariable int id, @RequestBody TipoTratamientoDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<Void> updateState(@PathVariable int id, @RequestBody Map<String, String> body) {
        service.updateState(id, body.get("estReg"));
        return ResponseEntity.ok().build();
    }
}
