package com.solidtoxic.service;

import com.solidtoxic.exception.ValidationException;
import com.solidtoxic.model.NivelToxicidad;
import com.solidtoxic.model.dto.NivelToxicidadDTO;
import com.solidtoxic.repository.NivelToxicidadRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NivelToxicidadService {

    private final NivelToxicidadRepository repo;

    public NivelToxicidadService(NivelToxicidadRepository repo) {
        this.repo = repo;
    }

    public List<NivelToxicidad> getAll() {
        return repo.findAll();
    }

    public List<NivelToxicidad> getByState(String state) {
        return repo.findByState(state);
    }

    public NivelToxicidad getById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("TR_Nivel_Toxicidad with ID " + id + " not found"));
    }

    public NivelToxicidad create(NivelToxicidadDTO dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getIdToxicidad() == null) errors.add("ID_Toxicidad is required");
        if (dto.getNivel() == null || dto.getNivel().isBlank()) errors.add("Nivel is required");
        if (dto.getNivel() != null && dto.getNivel().length() > 20) errors.add("Nivel must not exceed 20 characters");
        if (dto.getDescripcion() != null && dto.getDescripcion().length() > 250) errors.add("Descripcion must not exceed 250 characters");

        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (repo.existsById(dto.getIdToxicidad())) {
            throw new ValidationException("ID_Toxicidad " + dto.getIdToxicidad() + " already exists");
        }

        NivelToxicidad entity = new NivelToxicidad(dto.getIdToxicidad(), dto.getNivel(), dto.getDescripcion(), "A");
        repo.insert(entity);
        return repo.findById(dto.getIdToxicidad()).orElseThrow();
    }

    public NivelToxicidad update(int id, NivelToxicidadDTO dto) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("TR_Nivel_Toxicidad with ID " + id + " not found");
        }
        List<String> errors = new ArrayList<>();
        if (dto.getNivel() == null || dto.getNivel().isBlank()) errors.add("Nivel is required");
        if (dto.getNivel() != null && dto.getNivel().length() > 20) errors.add("Nivel must not exceed 20 characters");
        if (dto.getDescripcion() != null && dto.getDescripcion().length() > 250) errors.add("Descripcion must not exceed 250 characters");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        NivelToxicidad entity = new NivelToxicidad(id, dto.getNivel(), dto.getDescripcion(), null);
        repo.update(entity);
        return repo.findById(id).orElseThrow();
    }

    public void updateState(int id, String newState) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("TR_Nivel_Toxicidad with ID " + id + " not found");
        }
        if (!List.of("A", "I", "*").contains(newState)) {
            throw new ValidationException("Invalid state value. Allowed: A, I, *");
        }
        repo.updateState(id, newState);
    }
}
