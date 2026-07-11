package com.solidtoxic.controller;

import com.solidtoxic.model.EmpresaProductora;
import com.solidtoxic.model.dto.EmpresaProductoraDTO;
import com.solidtoxic.service.EmpresaProductoraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productora")
public class EmpresaProductoraController {

    private final EmpresaProductoraService service;

    public EmpresaProductoraController(EmpresaProductoraService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmpresaProductora> getAll(@RequestParam(defaultValue = "A") String state) {
        return service.getByState(state);
    }

    @GetMapping("/{id}")
    public EmpresaProductora getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<EmpresaProductora> create(@RequestBody EmpresaProductoraDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public EmpresaProductora update(@PathVariable String id, @RequestBody EmpresaProductoraDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<Void> updateState(@PathVariable String id, @RequestBody Map<String, String> body) {
        service.updateState(id, body.get("estReg"));
        return ResponseEntity.ok().build();
    }
}
