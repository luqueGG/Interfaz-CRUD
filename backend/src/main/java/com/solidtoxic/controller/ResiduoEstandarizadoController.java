package com.solidtoxic.controller;

import com.solidtoxic.model.ResiduoEstandarizado;
import com.solidtoxic.model.dto.ResiduoEstandarizadoDTO;
import com.solidtoxic.service.ResiduoEstandarizadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/estandar")
public class ResiduoEstandarizadoController {

    private final ResiduoEstandarizadoService service;

    public ResiduoEstandarizadoController(ResiduoEstandarizadoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ResiduoEstandarizado> getAll(@RequestParam(required = false) String state) {
        return state != null ? service.getByState(state) : service.getAll();
    }

    @GetMapping("/{id}")
    public ResiduoEstandarizado getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<ResiduoEstandarizado> create(@RequestBody ResiduoEstandarizadoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResiduoEstandarizado update(@PathVariable int id, @RequestBody ResiduoEstandarizadoDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<Void> updateState(@PathVariable int id, @RequestBody Map<String, String> body) {
        service.updateState(id, body.get("estReg"));
        return ResponseEntity.ok().build();
    }
}
