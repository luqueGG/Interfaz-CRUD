package com.solidtoxic.service;

import com.solidtoxic.exception.ValidationException;
import com.solidtoxic.model.Residuo;
import com.solidtoxic.model.dto.ResiduoDTO;
import com.solidtoxic.repository.EmpresaProductoraRepository;
import com.solidtoxic.repository.NivelToxicidadRepository;
import com.solidtoxic.repository.ResiduoEstandarizadoRepository;
import com.solidtoxic.repository.ResiduoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ResiduoService {

    private final ResiduoRepository repo;
    private final EmpresaProductoraRepository empresaRepo;
    private final ResiduoEstandarizadoRepository estandarRepo;
    private final NivelToxicidadRepository toxicidadRepo;

    public ResiduoService(ResiduoRepository repo,
                          EmpresaProductoraRepository empresaRepo,
                          ResiduoEstandarizadoRepository estandarRepo,
                          NivelToxicidadRepository toxicidadRepo) {
        this.repo = repo;
        this.empresaRepo = empresaRepo;
        this.estandarRepo = estandarRepo;
        this.toxicidadRepo = toxicidadRepo;
    }

    public List<Residuo> getAll() {
        return repo.findAll();
    }

    public List<Residuo> getByState(String state) {
        return repo.findByState(state);
    }

    public Residuo getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Residuo with code " + id + " not found"));
    }

    public Residuo create(ResiduoDTO dto) {
        List<String> errors = validate(dto, true);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (repo.existsById(dto.getCodResiduo())) {
            throw new ValidationException("Cod_Residuo " + dto.getCodResiduo() + " already exists");
        }
        validateForeignKeys(dto, errors);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        repo.insert(new Residuo(dto.getCodResiduo(), dto.getNifEmpresa(), dto.getCodEstandar(),
                dto.getIdToxicidad(), dto.getCantidadTotal(), dto.getOtrosDatos(), "A"));
        return repo.findById(dto.getCodResiduo()).orElseThrow();
    }

    public Residuo update(String id, ResiduoDTO dto) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Residuo with code " + id + " not found");
        List<String> errors = validate(dto, false);
        validateForeignKeys(dto, errors);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        repo.update(new Residuo(id, dto.getNifEmpresa(), dto.getCodEstandar(),
                dto.getIdToxicidad(), dto.getCantidadTotal(), dto.getOtrosDatos(), null));
        return repo.findById(id).orElseThrow();
    }

    public void updateState(String id, String newState) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Residuo with code " + id + " not found");
        if (!List.of("A", "I", "*").contains(newState))
            throw new ValidationException("Invalid state. Allowed: A, I, *");
        repo.updateState(id, newState);
    }

    private List<String> validate(ResiduoDTO dto, boolean isCreate) {
        List<String> errors = new ArrayList<>();
        if (isCreate && (dto.getCodResiduo() == null || dto.getCodResiduo().isBlank()))
            errors.add("Cod_Residuo is required");
        if (isCreate && dto.getCodResiduo() != null && dto.getCodResiduo().length() > 20)
            errors.add("Cod_Residuo must not exceed 20 characters");
        if (dto.getNifEmpresa() == null || dto.getNifEmpresa().isBlank())
            errors.add("NIF_Empresa is required");
        if (dto.getCodEstandar() == null) errors.add("Cod_Estandar is required");
        if (dto.getIdToxicidad() == null) errors.add("ID_Toxicidad is required");
        if (dto.getCantidadTotal() == null) errors.add("Cantidad_Total is required");
        return errors;
    }

    private void validateForeignKeys(ResiduoDTO dto, List<String> errors) {
        if (dto.getNifEmpresa() != null && !empresaRepo.existsByIdAndActive(dto.getNifEmpresa())) {
            errors.add("NIF_Empresa " + dto.getNifEmpresa() + " does not exist or is not active");
        }
        if (dto.getCodEstandar() != null && !estandarRepo.existsByIdAndActive(dto.getCodEstandar())) {
            errors.add("Cod_Estandar " + dto.getCodEstandar() + " does not exist or is not active");
        }
        if (dto.getIdToxicidad() != null) {
            boolean toxExists = toxicidadRepo.findById(dto.getIdToxicidad())
                    .map(t -> "A".equals(t.getEstReg()))
                    .orElse(false);
            if (!toxExists) errors.add("ID_Toxicidad " + dto.getIdToxicidad() + " does not exist or is not active");
        }
    }
}
