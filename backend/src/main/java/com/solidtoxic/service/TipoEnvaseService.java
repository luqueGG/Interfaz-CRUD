package com.solidtoxic.service;

import com.solidtoxic.exception.ValidationException;
import com.solidtoxic.model.TipoEnvase;
import com.solidtoxic.model.dto.TipoEnvaseDTO;
import com.solidtoxic.repository.TipoEnvaseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TipoEnvaseService {

    private final TipoEnvaseRepository repo;

    public TipoEnvaseService(TipoEnvaseRepository repo) {
        this.repo = repo;
    }

    public List<TipoEnvase> getByState(String state) {
        return repo.findByState(state);
    }

    public TipoEnvase getById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("TR_Tipo_Envase with ID " + id + " not found"));
    }

    public TipoEnvase create(TipoEnvaseDTO dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getIdEnvase() == null) errors.add("ID_Envase is required");
        if (dto.getNombreEnvase() == null || dto.getNombreEnvase().isBlank()) errors.add("Nombre_Envase is required");
        if (dto.getNombreEnvase() != null && dto.getNombreEnvase().length() > 50) errors.add("Nombre_Envase must not exceed 50 characters");
        if (dto.getDescripcion() != null && dto.getDescripcion().length() > 250) errors.add("Descripcion must not exceed 250 characters");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (repo.existsById(dto.getIdEnvase())) {
            throw new ValidationException("ID_Envase " + dto.getIdEnvase() + " already exists");
        }
        TipoEnvase entity = new TipoEnvase(dto.getIdEnvase(), dto.getNombreEnvase(), dto.getDescripcion(), "A");
        repo.insert(entity);
        return repo.findById(dto.getIdEnvase()).orElseThrow();
    }

    public TipoEnvase update(int id, TipoEnvaseDTO dto) {
        if (!repo.existsById(id)) throw new NoSuchElementException("TR_Tipo_Envase with ID " + id + " not found");
        List<String> errors = new ArrayList<>();
        if (dto.getNombreEnvase() == null || dto.getNombreEnvase().isBlank()) errors.add("Nombre_Envase is required");
        if (dto.getNombreEnvase() != null && dto.getNombreEnvase().length() > 50) errors.add("Nombre_Envase must not exceed 50 characters");
        if (dto.getDescripcion() != null && dto.getDescripcion().length() > 250) errors.add("Descripcion must not exceed 250 characters");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        repo.update(new TipoEnvase(id, dto.getNombreEnvase(), dto.getDescripcion(), null));
        return repo.findById(id).orElseThrow();
    }

    public void updateState(int id, String newState) {
        if (!repo.existsById(id)) throw new NoSuchElementException("TR_Tipo_Envase with ID " + id + " not found");
        if (!List.of("A", "I", "*").contains(newState)) throw new ValidationException("Invalid state value. Allowed: A, I, *");
        repo.updateState(id, newState);
    }
}
