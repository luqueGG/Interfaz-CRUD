package com.solidtoxic.service;

import com.solidtoxic.exception.ValidationException;
import com.solidtoxic.model.TipoTratamiento;
import com.solidtoxic.model.dto.TipoTratamientoDTO;
import com.solidtoxic.repository.TipoTratamientoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TipoTratamientoService {

    private final TipoTratamientoRepository repo;

    public TipoTratamientoService(TipoTratamientoRepository repo) {
        this.repo = repo;
    }

    public List<TipoTratamiento> getAll() {
        return repo.findAll();
    }

    public List<TipoTratamiento> getByState(String state) {
        return repo.findByState(state);
    }

    public TipoTratamiento getById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("TR_Tipo_Tratamiento with ID " + id + " not found"));
    }

    public TipoTratamiento create(TipoTratamientoDTO dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getIdTratamiento() == null) errors.add("ID_Tratamiento is required");
        if (dto.getNombreTratamiento() == null || dto.getNombreTratamiento().isBlank()) errors.add("Nombre_Tratamiento is required");
        if (dto.getNombreTratamiento() != null && dto.getNombreTratamiento().length() > 50) errors.add("Nombre_Tratamiento must not exceed 50 characters");
        if (dto.getDescripcion() != null && dto.getDescripcion().length() > 250) errors.add("Descripcion must not exceed 250 characters");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (repo.existsById(dto.getIdTratamiento())) {
            throw new ValidationException("ID_Tratamiento " + dto.getIdTratamiento() + " already exists");
        }
        repo.insert(new TipoTratamiento(dto.getIdTratamiento(), dto.getNombreTratamiento(), dto.getDescripcion(), "A"));
        return repo.findById(dto.getIdTratamiento()).orElseThrow();
    }

    public TipoTratamiento update(int id, TipoTratamientoDTO dto) {
        if (!repo.existsById(id)) throw new NoSuchElementException("TR_Tipo_Tratamiento with ID " + id + " not found");
        List<String> errors = new ArrayList<>();
        if (dto.getNombreTratamiento() == null || dto.getNombreTratamiento().isBlank()) errors.add("Nombre_Tratamiento is required");
        if (dto.getNombreTratamiento() != null && dto.getNombreTratamiento().length() > 50) errors.add("Nombre_Tratamiento must not exceed 50 characters");
        if (dto.getDescripcion() != null && dto.getDescripcion().length() > 250) errors.add("Descripcion must not exceed 250 characters");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        repo.update(new TipoTratamiento(id, dto.getNombreTratamiento(), dto.getDescripcion(), null));
        return repo.findById(id).orElseThrow();
    }

    public void updateState(int id, String newState) {
        if (!repo.existsById(id)) throw new NoSuchElementException("TR_Tipo_Tratamiento with ID " + id + " not found");
        if (!List.of("A", "I", "*").contains(newState)) throw new ValidationException("Invalid state value. Allowed: A, I, *");
        repo.updateState(id, newState);
    }
}
