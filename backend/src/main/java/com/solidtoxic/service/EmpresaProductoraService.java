package com.solidtoxic.service;

import com.solidtoxic.exception.ValidationException;
import com.solidtoxic.model.EmpresaProductora;
import com.solidtoxic.model.dto.EmpresaProductoraDTO;
import com.solidtoxic.repository.EmpresaProductoraRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EmpresaProductoraService {

    private final EmpresaProductoraRepository repo;

    public EmpresaProductoraService(EmpresaProductoraRepository repo) {
        this.repo = repo;
    }

    public List<EmpresaProductora> getByState(String state) {
        return repo.findByState(state);
    }

    public EmpresaProductora getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empresa_Productora with NIF " + id + " not found"));
    }

    public EmpresaProductora create(EmpresaProductoraDTO dto) {
        List<String> errors = validate(dto, true);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (repo.existsById(dto.getNifEmpresa())) {
            throw new ValidationException("NIF_Empresa " + dto.getNifEmpresa() + " already exists");
        }
        repo.insert(new EmpresaProductora(
                dto.getNifEmpresa(), dto.getNombreEmpresa(), dto.getCiudadEmpresa(),
                dto.getActividad(), dto.getOtrosDatos(), "A"));
        return repo.findById(dto.getNifEmpresa()).orElseThrow();
    }

    public EmpresaProductora update(String id, EmpresaProductoraDTO dto) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Empresa_Productora with NIF " + id + " not found");
        List<String> errors = validate(dto, false);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        repo.update(new EmpresaProductora(id, dto.getNombreEmpresa(), dto.getCiudadEmpresa(),
                dto.getActividad(), dto.getOtrosDatos(), null));
        return repo.findById(id).orElseThrow();
    }

    public void updateState(String id, String newState) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Empresa_Productora with NIF " + id + " not found");
        if (!List.of("A", "I", "*").contains(newState)) throw new ValidationException("Invalid state. Allowed: A, I, *");
        repo.updateState(id, newState);
    }

    private List<String> validate(EmpresaProductoraDTO dto, boolean isCreate) {
        List<String> errors = new ArrayList<>();
        if (isCreate && (dto.getNifEmpresa() == null || dto.getNifEmpresa().isBlank()))
            errors.add("NIF_Empresa is required");
        if (isCreate && dto.getNifEmpresa() != null && dto.getNifEmpresa().length() > 20)
            errors.add("NIF_Empresa must not exceed 20 characters");
        if (dto.getNombreEmpresa() == null || dto.getNombreEmpresa().isBlank())
            errors.add("Nombre_Empresa is required");
        if (dto.getNombreEmpresa() != null && dto.getNombreEmpresa().length() > 100)
            errors.add("Nombre_Empresa must not exceed 100 characters");
        if (dto.getCiudadEmpresa() == null || dto.getCiudadEmpresa().isBlank())
            errors.add("Ciudad_Empresa is required");
        if (dto.getCiudadEmpresa() != null && dto.getCiudadEmpresa().length() > 50)
            errors.add("Ciudad_Empresa must not exceed 50 characters");
        if (dto.getActividad() == null || dto.getActividad().isBlank())
            errors.add("Actividad is required");
        if (dto.getActividad() != null && dto.getActividad().length() > 100)
            errors.add("Actividad must not exceed 100 characters");
        return errors;
    }
}
