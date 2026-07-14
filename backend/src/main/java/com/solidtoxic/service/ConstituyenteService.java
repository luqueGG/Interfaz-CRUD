package com.solidtoxic.service;

import com.solidtoxic.exception.ValidationException;
import com.solidtoxic.model.Constituyente;
import com.solidtoxic.model.dto.ConstituyenteDTO;
import com.solidtoxic.repository.ConstituyenteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ConstituyenteService {

    private final ConstituyenteRepository repo;

    public ConstituyenteService(ConstituyenteRepository repo) {
        this.repo = repo;
    }

    public List<Constituyente> getAll() {
        return repo.findAll();
    }

    public List<Constituyente> getByState(String state) {
        return repo.findByState(state);
    }

    public Constituyente getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Constituyente with ID " + id + " not found"));
    }

    public Constituyente create(ConstituyenteDTO dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getCodConstituyente() == null || dto.getCodConstituyente().isBlank()) errors.add("Cod_Constituyente is required");
        if (dto.getCodConstituyente() != null && dto.getCodConstituyente().length() > 20) errors.add("Cod_Constituyente must not exceed 20 characters");
        if (dto.getNombreConstituyente() == null || dto.getNombreConstituyente().isBlank()) errors.add("Nombre_Constituyente is required");
        if (dto.getNombreConstituyente() != null && dto.getNombreConstituyente().length() > 100) errors.add("Nombre_Constituyente must not exceed 100 characters");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (repo.existsById(dto.getCodConstituyente())) {
            throw new ValidationException("Cod_Constituyente " + dto.getCodConstituyente() + " already exists");
        }
        repo.insert(new Constituyente(dto.getCodConstituyente(), dto.getNombreConstituyente(), dto.getOtrosDatos(), "A"));
        return repo.findById(dto.getCodConstituyente()).orElseThrow();
    }

    public Constituyente update(String id, ConstituyenteDTO dto) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Constituyente with ID " + id + " not found");
        List<String> errors = new ArrayList<>();
        if (dto.getNombreConstituyente() == null || dto.getNombreConstituyente().isBlank()) errors.add("Nombre_Constituyente is required");
        if (dto.getNombreConstituyente() != null && dto.getNombreConstituyente().length() > 100) errors.add("Nombre_Constituyente must not exceed 100 characters");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        repo.update(new Constituyente(id, dto.getNombreConstituyente(), dto.getOtrosDatos(), null));
        return repo.findById(id).orElseThrow();
    }

    public void updateState(String id, String newState) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Constituyente with ID " + id + " not found");
        if (!List.of("A", "I", "*").contains(newState)) throw new ValidationException("Invalid state value. Allowed: A, I, *");
        repo.updateState(id, newState);
    }
}
