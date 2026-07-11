package com.solidtoxic.service;

import com.solidtoxic.exception.ValidationException;
import com.solidtoxic.model.TipoTransporte;
import com.solidtoxic.model.dto.TipoTransporteDTO;
import com.solidtoxic.repository.TipoTransporteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TipoTransporteService {

    private final TipoTransporteRepository repo;

    public TipoTransporteService(TipoTransporteRepository repo) {
        this.repo = repo;
    }

    public List<TipoTransporte> getByState(String state) {
        return repo.findByState(state);
    }

    public TipoTransporte getById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("TR_Tipo_Transporte with ID " + id + " not found"));
    }

    public TipoTransporte create(TipoTransporteDTO dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getIdTipoTransporte() == null) errors.add("ID_Tipo_Transporte is required");
        if (dto.getNombreTransporte() == null || dto.getNombreTransporte().isBlank()) errors.add("Nombre_Transporte is required");
        if (dto.getNombreTransporte() != null && dto.getNombreTransporte().length() > 50) errors.add("Nombre_Transporte must not exceed 50 characters");
        if (dto.getDescripcion() != null && dto.getDescripcion().length() > 250) errors.add("Descripcion must not exceed 250 characters");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (repo.existsById(dto.getIdTipoTransporte())) {
            throw new ValidationException("ID_Tipo_Transporte " + dto.getIdTipoTransporte() + " already exists");
        }
        repo.insert(new TipoTransporte(dto.getIdTipoTransporte(), dto.getNombreTransporte(), dto.getDescripcion(), "A"));
        return repo.findById(dto.getIdTipoTransporte()).orElseThrow();
    }

    public TipoTransporte update(int id, TipoTransporteDTO dto) {
        if (!repo.existsById(id)) throw new NoSuchElementException("TR_Tipo_Transporte with ID " + id + " not found");
        List<String> errors = new ArrayList<>();
        if (dto.getNombreTransporte() == null || dto.getNombreTransporte().isBlank()) errors.add("Nombre_Transporte is required");
        if (dto.getNombreTransporte() != null && dto.getNombreTransporte().length() > 50) errors.add("Nombre_Transporte must not exceed 50 characters");
        if (dto.getDescripcion() != null && dto.getDescripcion().length() > 250) errors.add("Descripcion must not exceed 250 characters");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        repo.update(new TipoTransporte(id, dto.getNombreTransporte(), dto.getDescripcion(), null));
        return repo.findById(id).orElseThrow();
    }

    public void updateState(int id, String newState) {
        if (!repo.existsById(id)) throw new NoSuchElementException("TR_Tipo_Transporte with ID " + id + " not found");
        if (!List.of("A", "I", "*").contains(newState)) throw new ValidationException("Invalid state value. Allowed: A, I, *");
        repo.updateState(id, newState);
    }
}
