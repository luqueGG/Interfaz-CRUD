package com.solidtoxic.service;

import com.solidtoxic.exception.ValidationException;
import com.solidtoxic.model.EmpresaTransportista;
import com.solidtoxic.model.dto.EmpresaTransportistaDTO;
import com.solidtoxic.repository.EmpresaTransportistaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EmpresaTransportistaService {

    private final EmpresaTransportistaRepository repo;

    public EmpresaTransportistaService(EmpresaTransportistaRepository repo) {
        this.repo = repo;
    }

    public List<EmpresaTransportista> getAll() {
        return repo.findAll();
    }

    public List<EmpresaTransportista> getByState(String state) {
        return repo.findByState(state);
    }

    public EmpresaTransportista getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empresa_Transportista with NIF " + id + " not found"));
    }

    public EmpresaTransportista create(EmpresaTransportistaDTO dto) {
        List<String> errors = validate(dto, true);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (repo.existsById(dto.getNifTransportista())) {
            throw new ValidationException("NIF_Transportista " + dto.getNifTransportista() + " already exists");
        }
        repo.insert(new EmpresaTransportista(
                dto.getNifTransportista(), dto.getNombreTransportista(),
                dto.getCiudadTransportista(), dto.getOtrosDatos(), "A"));
        return repo.findById(dto.getNifTransportista()).orElseThrow();
    }

    public EmpresaTransportista update(String id, EmpresaTransportistaDTO dto) {
        if (!repo.existsById(id))
            throw new NoSuchElementException("Empresa_Transportista with NIF " + id + " not found");
        List<String> errors = validate(dto, false);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        repo.update(new EmpresaTransportista(id, dto.getNombreTransportista(),
                dto.getCiudadTransportista(), dto.getOtrosDatos(), null));
        return repo.findById(id).orElseThrow();
    }

    public void updateState(String id, String newState) {
        if (!repo.existsById(id))
            throw new NoSuchElementException("Empresa_Transportista with NIF " + id + " not found");
        if (!List.of("A", "I", "*").contains(newState))
            throw new ValidationException("Invalid state. Allowed: A, I, *");
        repo.updateState(id, newState);
    }

    private List<String> validate(EmpresaTransportistaDTO dto, boolean isCreate) {
        List<String> errors = new ArrayList<>();
        if (isCreate && (dto.getNifTransportista() == null || dto.getNifTransportista().isBlank()))
            errors.add("NIF_Transportista is required");
        if (isCreate && dto.getNifTransportista() != null && dto.getNifTransportista().length() > 20)
            errors.add("NIF_Transportista must not exceed 20 characters");
        if (dto.getNombreTransportista() == null || dto.getNombreTransportista().isBlank())
            errors.add("Nombre_Transportista is required");
        if (dto.getNombreTransportista() != null && dto.getNombreTransportista().length() > 100)
            errors.add("Nombre_Transportista must not exceed 100 characters");
        if (dto.getCiudadTransportista() == null || dto.getCiudadTransportista().isBlank())
            errors.add("Ciudad_Transportista is required");
        if (dto.getCiudadTransportista() != null && dto.getCiudadTransportista().length() > 50)
            errors.add("Ciudad_Transportista must not exceed 50 characters");
        return errors;
    }
}
