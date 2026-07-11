package com.solidtoxic.service;

import com.solidtoxic.exception.ValidationException;
import com.solidtoxic.model.Traslado;
import com.solidtoxic.model.dto.TrasladoDTO;
import com.solidtoxic.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TrasladoService {

    private final TrasladoRepository repo;
    private final ResiduoRepository residuoRepo;
    private final DestinoRepository destinoRepo;
    private final TipoEnvaseRepository envaseRepo;
    private final TipoTratamientoRepository tratamientoRepo;

    public TrasladoService(TrasladoRepository repo,
                           ResiduoRepository residuoRepo,
                           DestinoRepository destinoRepo,
                           TipoEnvaseRepository envaseRepo,
                           TipoTratamientoRepository tratamientoRepo) {
        this.repo = repo;
        this.residuoRepo = residuoRepo;
        this.destinoRepo = destinoRepo;
        this.envaseRepo = envaseRepo;
        this.tratamientoRepo = tratamientoRepo;
    }

    public List<Traslado> getByState(String state) {
        return repo.findByState(state);
    }

    public Traslado getById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Traslado with ID " + id + " not found"));
    }

    public Traslado create(TrasladoDTO dto) {
        List<String> errors = validate(dto, true);
        validateForeignKeys(dto, errors);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (repo.existsById(dto.getIdTraslado())) {
            throw new ValidationException("ID_Traslado " + dto.getIdTraslado() + " already exists");
        }
        try {
            repo.insert(new Traslado(dto.getIdTraslado(), dto.getCodResiduo(), dto.getCodDestino(),
                    dto.getFechaEnvio(), dto.getCantidadTrasladada(), dto.getIdEnvase(),
                    dto.getIdTratamiento(), dto.getFechaLlegada(), dto.getOtrosDatos(), "A"));
        } catch (DataIntegrityViolationException ex) {
            throw new ValidationException(
                    "A shipment for this waste, destination and date already exists (unique constraint).");
        }
        return repo.findById(dto.getIdTraslado()).orElseThrow();
    }

    public Traslado update(int id, TrasladoDTO dto) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Traslado with ID " + id + " not found");
        List<String> errors = validate(dto, false);
        validateForeignKeys(dto, errors);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        try {
            repo.update(new Traslado(id, dto.getCodResiduo(), dto.getCodDestino(),
                    dto.getFechaEnvio(), dto.getCantidadTrasladada(), dto.getIdEnvase(),
                    dto.getIdTratamiento(), dto.getFechaLlegada(), dto.getOtrosDatos(), null));
        } catch (DataIntegrityViolationException ex) {
            throw new ValidationException(
                    "A shipment for this waste, destination and date already exists (unique constraint).");
        }
        return repo.findById(id).orElseThrow();
    }

    public void updateState(int id, String newState) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Traslado with ID " + id + " not found");
        if (!List.of("A", "I", "*").contains(newState))
            throw new ValidationException("Invalid state. Allowed: A, I, *");
        repo.updateState(id, newState);
    }

    private List<String> validate(TrasladoDTO dto, boolean isCreate) {
        List<String> errors = new ArrayList<>();
        if (isCreate && dto.getIdTraslado() == null) errors.add("ID_Traslado is required");
        if (dto.getCodResiduo() == null || dto.getCodResiduo().isBlank()) errors.add("Cod_Residuo is required");
        if (dto.getCodDestino() == null || dto.getCodDestino().isBlank()) errors.add("Cod_Destino is required");
        if (dto.getFechaEnvio() == null) errors.add("Fecha_Envio is required");
        if (dto.getCantidadTrasladada() == null) errors.add("Cantidad_Trasladada is required");
        if (dto.getIdEnvase() == null) errors.add("ID_Envase is required");
        if (dto.getIdTratamiento() == null) errors.add("ID_Tratamiento is required");
        return errors;
    }

    private void validateForeignKeys(TrasladoDTO dto, List<String> errors) {
        if (dto.getCodResiduo() != null && !residuoRepo.existsByIdAndActive(dto.getCodResiduo())) {
            errors.add("Cod_Residuo " + dto.getCodResiduo() + " does not exist or is not active");
        }
        if (dto.getCodDestino() != null && !destinoRepo.existsByIdAndActive(dto.getCodDestino())) {
            errors.add("Cod_Destino " + dto.getCodDestino() + " does not exist or is not active");
        }
        if (dto.getIdEnvase() != null && !envaseRepo.existsByIdAndActive(dto.getIdEnvase())) {
            errors.add("ID_Envase " + dto.getIdEnvase() + " does not exist or is not active");
        }
        if (dto.getIdTratamiento() != null && !tratamientoRepo.existsByIdAndActive(dto.getIdTratamiento())) {
            errors.add("ID_Tratamiento " + dto.getIdTratamiento() + " does not exist or is not active");
        }
    }
}
