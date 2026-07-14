package com.solidtoxic.service;

import com.solidtoxic.exception.ValidationException;
import com.solidtoxic.model.ResiduoConstituyente;
import com.solidtoxic.model.dto.ResiduoConstituyenteDTO;
import com.solidtoxic.repository.ConstituyenteRepository;
import com.solidtoxic.repository.ResiduoConstituyenteRepository;
import com.solidtoxic.repository.ResiduoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ResiduoConstituyenteService {

    private final ResiduoConstituyenteRepository repo;
    private final ResiduoRepository residuoRepo;
    private final ConstituyenteRepository constituyenteRepo;

    public ResiduoConstituyenteService(ResiduoConstituyenteRepository repo,
                                        ResiduoRepository residuoRepo,
                                        ConstituyenteRepository constituyenteRepo) {
        this.repo = repo;
        this.residuoRepo = residuoRepo;
        this.constituyenteRepo = constituyenteRepo;
    }

    public List<ResiduoConstituyente> getAll() {
        return repo.findAll();
    }

    public List<ResiduoConstituyente> getByState(String state) {
        return repo.findByState(state);
    }

    public List<ResiduoConstituyente> getByResiduo(String codResiduo) {
        return repo.findByResiduo(codResiduo);
    }

    public ResiduoConstituyente getByKey(String codResiduo, String codConstituyente) {
        return repo.findByCompositeKey(codResiduo, codConstituyente)
                .orElseThrow(() -> new NoSuchElementException(
                        "Residuo_Constituyente (" + codResiduo + ", " + codConstituyente + ") not found"));
    }

    public ResiduoConstituyente create(ResiduoConstituyenteDTO dto) {
        List<String> errors = new ArrayList<>();
        if (dto.getCodResiduo() == null || dto.getCodResiduo().isBlank()) errors.add("Cod_Residuo is required");
        if (dto.getCodConstituyente() == null || dto.getCodConstituyente().isBlank()) errors.add("Cod_Constituyente is required");
        if (dto.getCantidad() == null) errors.add("Cantidad is required");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (repo.existsByCompositeKey(dto.getCodResiduo(), dto.getCodConstituyente())) {
            throw new ValidationException("Record (" + dto.getCodResiduo() + ", " + dto.getCodConstituyente() + ") already exists");
        }
        if (!residuoRepo.existsByIdAndActive(dto.getCodResiduo())) {
            errors.add("Cod_Residuo " + dto.getCodResiduo() + " does not exist or is not active");
        }
        if (!constituyenteRepo.existsByIdAndActive(dto.getCodConstituyente())) {
            errors.add("Cod_Constituyente " + dto.getCodConstituyente() + " does not exist or is not active");
        }
        if (!errors.isEmpty()) throw new ValidationException(errors);

        repo.insert(new ResiduoConstituyente(dto.getCodResiduo(), dto.getCodConstituyente(), dto.getCantidad(), "A"));
        return repo.findByCompositeKey(dto.getCodResiduo(), dto.getCodConstituyente()).orElseThrow();
    }

    public ResiduoConstituyente update(String codResiduo, String codConstituyente, ResiduoConstituyenteDTO dto) {
        if (!repo.existsByCompositeKey(codResiduo, codConstituyente)) {
            throw new NoSuchElementException("Residuo_Constituyente (" + codResiduo + ", " + codConstituyente + ") not found");
        }
        if (dto.getCantidad() == null) throw new ValidationException("Cantidad is required");

        repo.update(new ResiduoConstituyente(codResiduo, codConstituyente, dto.getCantidad(), null));
        return repo.findByCompositeKey(codResiduo, codConstituyente).orElseThrow();
    }

    public void updateState(String codResiduo, String codConstituyente, String newState) {
        if (!repo.existsByCompositeKey(codResiduo, codConstituyente)) {
            throw new NoSuchElementException("Residuo_Constituyente (" + codResiduo + ", " + codConstituyente + ") not found");
        }
        if (!List.of("A", "I", "*").contains(newState))
            throw new ValidationException("Invalid state. Allowed: A, I, *");
        repo.updateState(codResiduo, codConstituyente, newState);
    }
}
