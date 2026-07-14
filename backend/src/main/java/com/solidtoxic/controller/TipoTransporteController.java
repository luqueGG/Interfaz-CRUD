package com.solidtoxic.controller;

import com.solidtoxic.model.TipoTransporte;
import com.solidtoxic.model.dto.TipoTransporteDTO;
import com.solidtoxic.service.TipoTransporteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transporte")
public class TipoTransporteController {

    private final TipoTransporteService service;

    public TipoTransporteController(TipoTransporteService service) {
        this.service = service;
    }

    @GetMapping
    public List<TipoTransporte> getAll(@RequestParam(required = false) String state) {
        return state != null ? service.getByState(state) : service.getAll();
    }

    @GetMapping("/{id}")
    public TipoTransporte getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<TipoTransporte> create(@RequestBody TipoTransporteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public TipoTransporte update(@PathVariable int id, @RequestBody TipoTransporteDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<Void> updateState(@PathVariable int id, @RequestBody Map<String, String> body) {
        service.updateState(id, body.get("estReg"));
        return ResponseEntity.ok().build();
    }
}
