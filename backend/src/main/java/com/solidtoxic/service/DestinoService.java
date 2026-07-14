package com.solidtoxic.service;

import com.solidtoxic.exception.ValidationException;
import com.solidtoxic.model.Destino;
import com.solidtoxic.model.dto.DestinoDTO;
import com.solidtoxic.repository.DestinoRepository;
import com.solidtoxic.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DestinoService {

    private final DestinoRepository repo;
    private final RegionRepository regionRepo;

    public DestinoService(DestinoRepository repo, RegionRepository regionRepo) {
        this.repo = repo;
        this.regionRepo = regionRepo;
    }

    public List<Destino> getAll() {
        return repo.findAll();
    }

    public List<Destino> getByState(String state) {
        return repo.findByState(state);
    }

    public Destino getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Destino with code " + id + " not found"));
    }

    public Destino create(DestinoDTO dto) {
        List<String> errors = validate(dto, true);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (repo.existsById(dto.getCodDestino())) {
            throw new ValidationException("Cod_Destino " + dto.getCodDestino() + " already exists");
        }
        if (!regionRepo.existsByIdAndActive(dto.getIdRegion())) {
            throw new ValidationException("ID_Region " + dto.getIdRegion() + " does not exist or is not active");
        }
        repo.insert(new Destino(dto.getCodDestino(), dto.getIdRegion(), dto.getNombreDestino(),
                dto.getCiudadDestino(), dto.getCapacidadMaxima(), dto.getCapacidadActual(),
                dto.getOtrosDatos(), "A"));
        return repo.findById(dto.getCodDestino()).orElseThrow();
    }

    public Destino update(String id, DestinoDTO dto) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Destino with code " + id + " not found");
        List<String> errors = validate(dto, false);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (dto.getIdRegion() != null && !regionRepo.existsByIdAndActive(dto.getIdRegion())) {
            throw new ValidationException("ID_Region " + dto.getIdRegion() + " does not exist or is not active");
        }
        repo.update(new Destino(id, dto.getIdRegion(), dto.getNombreDestino(), dto.getCiudadDestino(),
                dto.getCapacidadMaxima(), dto.getCapacidadActual(), dto.getOtrosDatos(), null));
        return repo.findById(id).orElseThrow();
    }

    public void updateState(String id, String newState) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Destino with code " + id + " not found");
        if (!List.of("A", "I", "*").contains(newState))
            throw new ValidationException("Invalid state. Allowed: A, I, *");
        repo.updateState(id, newState);
    }

    private List<String> validate(DestinoDTO dto, boolean isCreate) {
        List<String> errors = new ArrayList<>();
        if (isCreate && (dto.getCodDestino() == null || dto.getCodDestino().isBlank()))
            errors.add("Cod_Destino is required");
        if (isCreate && dto.getCodDestino() != null && dto.getCodDestino().length() > 20)
            errors.add("Cod_Destino must not exceed 20 characters");
        if (dto.getIdRegion() == null) errors.add("ID_Region is required");
        if (dto.getNombreDestino() == null || dto.getNombreDestino().isBlank())
            errors.add("Nombre_Destino is required");
        if (dto.getNombreDestino() != null && dto.getNombreDestino().length() > 100)
            errors.add("Nombre_Destino must not exceed 100 characters");
        if (dto.getCiudadDestino() == null || dto.getCiudadDestino().isBlank())
            errors.add("Ciudad_Destino is required");
        if (dto.getCiudadDestino() != null && dto.getCiudadDestino().length() > 50)
            errors.add("Ciudad_Destino must not exceed 50 characters");
        if (dto.getCapacidadMaxima() == null) errors.add("Capacidad_Maxima is required");
        if (dto.getCapacidadActual() == null) errors.add("Capacidad_Actual is required");
        return errors;
    }
}
