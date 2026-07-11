package com.solidtoxic.controller;

import com.solidtoxic.model.TrasladoTransportista;
import com.solidtoxic.model.dto.TrasladoTransportistaDTO;
import com.solidtoxic.service.TrasladoTransportistaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Composite PK endpoints:
 *   GET    /api/v1/traslado-transportista?state=A
 *   GET    /api/v1/traslado-transportista/{idTraslado}/{nifTransportista}
 *   GET    /api/v1/traslado-transportista/traslado/{idTraslado}
 *   POST   /api/v1/traslado-transportista
 *   PUT    /api/v1/traslado-transportista/{idTraslado}/{nifTransportista}
 *   PATCH  /api/v1/traslado-transportista/{idTraslado}/{nifTransportista}/state
 */
@RestController
@RequestMapping("/api/v1/traslado-transportista")
public class TrasladoTransportistaController {

    private final TrasladoTransportistaService service;

    public TrasladoTransportistaController(TrasladoTransportistaService service) {
        this.service = service;
    }

    @GetMapping
    public List<TrasladoTransportista> getAll(@RequestParam(defaultValue = "A") String state) {
        return service.getByState(state);
    }

    @GetMapping("/traslado/{idTraslado}")
    public List<TrasladoTransportista> getByTraslado(@PathVariable int idTraslado) {
        return service.getByTraslado(idTraslado);
    }

    @GetMapping("/{idTraslado}/{nifTransportista}")
    public TrasladoTransportista getByKey(@PathVariable int idTraslado,
                                           @PathVariable String nifTransportista) {
        return service.getByKey(idTraslado, nifTransportista);
    }

    @PostMapping
    public ResponseEntity<TrasladoTransportista> create(@RequestBody TrasladoTransportistaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{idTraslado}/{nifTransportista}")
    public TrasladoTransportista update(@PathVariable int idTraslado,
                                         @PathVariable String nifTransportista,
                                         @RequestBody TrasladoTransportistaDTO dto) {
        return service.update(idTraslado, nifTransportista, dto);
    }

    @PatchMapping("/{idTraslado}/{nifTransportista}/state")
    public ResponseEntity<Void> updateState(@PathVariable int idTraslado,
                                             @PathVariable String nifTransportista,
                                             @RequestBody Map<String, String> body) {
        service.updateState(idTraslado, nifTransportista, body.get("estReg"));
        return ResponseEntity.ok().build();
    }
}
