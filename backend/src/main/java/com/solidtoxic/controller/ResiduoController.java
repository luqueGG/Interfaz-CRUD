package com.solidtoxic.controller;

import com.solidtoxic.model.Residuo;
import com.solidtoxic.model.dto.ResiduoDTO;
import com.solidtoxic.service.ResiduoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/residuo")
public class ResiduoController {

    private final ResiduoService service;

    public ResiduoController(ResiduoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Residuo> getAll(@RequestParam(defaultValue = "A") String state) {
        return service.getByState(state);
    }

    @GetMapping("/{id}")
    public Residuo getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Residuo> create(@RequestBody ResiduoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public Residuo update(@PathVariable String id, @RequestBody ResiduoDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<Void> updateState(@PathVariable String id, @RequestBody Map<String, String> body) {
        service.updateState(id, body.get("estReg"));
        return ResponseEntity.ok().build();
    }
}
