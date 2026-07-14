package com.solidtoxic.service;

import com.solidtoxic.exception.ValidationException;
import com.solidtoxic.model.Region;
import com.solidtoxic.model.dto.RegionDTO;
import com.solidtoxic.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RegionService {

    private final RegionRepository repo;

    public RegionService(RegionRepository repo) {
        this.repo = repo;
    }

    public List<Region> getAll() {
        return repo.findAll();
    }

    public List<Region> getByState(String state) {
        return repo.findByState(state);
    }

    public Region getById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Region with ID " + id + " not found"));
    }

    public Region create(RegionDTO dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getIdRegion() == null) errors.add("ID_Region is required");
        if (dto.getNombreRegion() == null || dto.getNombreRegion().isBlank()) errors.add("Nombre_Region is required");
        if (dto.getNombreRegion() != null && dto.getNombreRegion().length() > 50) errors.add("Nombre_Region must not exceed 50 characters");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (repo.existsById(dto.getIdRegion())) {
            throw new ValidationException("ID_Region " + dto.getIdRegion() + " already exists");
        }
        repo.insert(new Region(dto.getIdRegion(), dto.getNombreRegion(), "A"));
        return repo.findById(dto.getIdRegion()).orElseThrow();
    }

    public Region update(int id, RegionDTO dto) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Region with ID " + id + " not found");
        List<String> errors = new ArrayList<>();
        if (dto.getNombreRegion() == null || dto.getNombreRegion().isBlank()) errors.add("Nombre_Region is required");
        if (dto.getNombreRegion() != null && dto.getNombreRegion().length() > 50) errors.add("Nombre_Region must not exceed 50 characters");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        repo.update(new Region(id, dto.getNombreRegion(), null));
        return repo.findById(id).orElseThrow();
    }

    public void updateState(int id, String newState) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Region with ID " + id + " not found");
        if (!List.of("A", "I", "*").contains(newState)) throw new ValidationException("Invalid state value. Allowed: A, I, *");
        repo.updateState(id, newState);
    }
}
