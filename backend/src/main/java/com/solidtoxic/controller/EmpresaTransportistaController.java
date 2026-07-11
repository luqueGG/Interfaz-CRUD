package com.solidtoxic.controller;

import com.solidtoxic.model.EmpresaTransportista;
import com.solidtoxic.model.dto.EmpresaTransportistaDTO;
import com.solidtoxic.service.EmpresaTransportistaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transportista")
public class EmpresaTransportistaController {

    private final EmpresaTransportistaService service;

    public EmpresaTransportistaController(EmpresaTransportistaService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmpresaTransportista> getAll(@RequestParam(defaultValue = "A") String state) {
        return service.getByState(state);
    }

    @GetMapping("/{id}")
    public EmpresaTransportista getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<EmpresaTransportista> create(@RequestBody EmpresaTransportistaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public EmpresaTransportista update(@PathVariable String id, @RequestBody EmpresaTransportistaDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<Void> updateState(@PathVariable String id, @RequestBody Map<String, String> body) {
        service.updateState(id, body.get("estReg"));
        return ResponseEntity.ok().build();
    }
}
