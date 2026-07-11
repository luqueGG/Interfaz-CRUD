package com.solidtoxic.service;

import com.solidtoxic.exception.ValidationException;
import com.solidtoxic.model.ResiduoEstandarizado;
import com.solidtoxic.model.dto.ResiduoEstandarizadoDTO;
import com.solidtoxic.repository.ResiduoEstandarizadoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ResiduoEstandarizadoService {

    private final ResiduoEstandarizadoRepository repo;

    public ResiduoEstandarizadoService(ResiduoEstandarizadoRepository repo) {
        this.repo = repo;
    }

    public List<ResiduoEstandarizado> getByState(String state) {
        return repo.findByState(state);
    }

    public ResiduoEstandarizado getById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Residuo_Estandarizado with ID " + id + " not found"));
    }

    public ResiduoEstandarizado create(ResiduoEstandarizadoDTO dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getCodEstandar() == null) errors.add("Cod_Estandar is required");
        if (dto.getNombreEstandar() == null || dto.getNombreEstandar().isBlank()) errors.add("Nombre_Estandar is required");
        if (dto.getNombreEstandar() != null && dto.getNombreEstandar().length() > 100) errors.add("Nombre_Estandar must not exceed 100 characters");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (repo.existsById(dto.getCodEstandar())) {
            throw new ValidationException("Cod_Estandar " + dto.getCodEstandar() + " already exists");
        }
        repo.insert(new ResiduoEstandarizado(dto.getCodEstandar(), dto.getNombreEstandar(), "A"));
        return repo.findById(dto.getCodEstandar()).orElseThrow();
    }

    public ResiduoEstandarizado update(int id, ResiduoEstandarizadoDTO dto) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Residuo_Estandarizado with ID " + id + " not found");
        List<String> errors = new ArrayList<>();
        if (dto.getNombreEstandar() == null || dto.getNombreEstandar().isBlank()) errors.add("Nombre_Estandar is required");
        if (dto.getNombreEstandar() != null && dto.getNombreEstandar().length() > 100) errors.add("Nombre_Estandar must not exceed 100 characters");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        repo.update(new ResiduoEstandarizado(id, dto.getNombreEstandar(), null));
        return repo.findById(id).orElseThrow();
    }

    public void updateState(int id, String newState) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Residuo_Estandarizado with ID " + id + " not found");
        if (!List.of("A", "I", "*").contains(newState)) throw new ValidationException("Invalid state value. Allowed: A, I, *");
        repo.updateState(id, newState);
    }
}
