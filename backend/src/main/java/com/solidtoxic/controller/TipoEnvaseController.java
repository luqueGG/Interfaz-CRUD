package com.solidtoxic.controller;

import com.solidtoxic.model.TipoEnvase;
import com.solidtoxic.model.dto.TipoEnvaseDTO;
import com.solidtoxic.service.TipoEnvaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/envase")
public class TipoEnvaseController {

    private final TipoEnvaseService service;

    public TipoEnvaseController(TipoEnvaseService service) {
        this.service = service;
    }

    @GetMapping
    public List<TipoEnvase> getAll(@RequestParam(defaultValue = "A") String state) {
        return service.getByState(state);
    }

    @GetMapping("/{id}")
    public TipoEnvase getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<TipoEnvase> create(@RequestBody TipoEnvaseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public TipoEnvase update(@PathVariable int id, @RequestBody TipoEnvaseDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<Void> updateState(@PathVariable int id, @RequestBody Map<String, String> body) {
        service.updateState(id, body.get("estReg"));
        return ResponseEntity.ok().build();
    }
}
