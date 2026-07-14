package com.solidtoxic.controller;

import com.solidtoxic.model.Region;
import com.solidtoxic.model.dto.RegionDTO;
import com.solidtoxic.service.RegionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/region")
public class RegionController {

    private final RegionService service;

    public RegionController(RegionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Region> getAll(@RequestParam(required = false) String state) {
        return state != null ? service.getByState(state) : service.getAll();
    }

    @GetMapping("/{id}")
    public Region getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Region> create(@RequestBody RegionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public Region update(@PathVariable int id, @RequestBody RegionDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<Void> updateState(@PathVariable int id, @RequestBody Map<String, String> body) {
        service.updateState(id, body.get("estReg"));
        return ResponseEntity.ok().build();
    }
}
