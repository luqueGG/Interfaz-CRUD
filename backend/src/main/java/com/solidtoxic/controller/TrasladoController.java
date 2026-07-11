package com.solidtoxic.controller;

import com.solidtoxic.model.Traslado;
import com.solidtoxic.model.dto.TrasladoDTO;
import com.solidtoxic.service.TrasladoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/traslado")
public class TrasladoController {

    private final TrasladoService service;

    public TrasladoController(TrasladoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Traslado> getAll(@RequestParam(defaultValue = "A") String state) {
        return service.getByState(state);
    }

    @GetMapping("/{id}")
    public Traslado getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Traslado> create(@RequestBody TrasladoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public Traslado update(@PathVariable int id, @RequestBody TrasladoDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<Void> updateState(@PathVariable int id, @RequestBody Map<String, String> body) {
        service.updateState(id, body.get("estReg"));
        return ResponseEntity.ok().build();
    }
}
