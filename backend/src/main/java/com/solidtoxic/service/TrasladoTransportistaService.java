package com.solidtoxic.service;

import com.solidtoxic.exception.ValidationException;
import com.solidtoxic.model.TrasladoTransportista;
import com.solidtoxic.model.dto.TrasladoTransportistaDTO;
import com.solidtoxic.repository.EmpresaTransportistaRepository;
import com.solidtoxic.repository.TipoTransporteRepository;
import com.solidtoxic.repository.TrasladoRepository;
import com.solidtoxic.repository.TrasladoTransportistaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TrasladoTransportistaService {

    private final TrasladoTransportistaRepository repo;
    private final TrasladoRepository trasladoRepo;
    private final EmpresaTransportistaRepository transportistaRepo;
    private final TipoTransporteRepository tipoTransporteRepo;

    public TrasladoTransportistaService(TrasladoTransportistaRepository repo,
                                         TrasladoRepository trasladoRepo,
                                         EmpresaTransportistaRepository transportistaRepo,
                                         TipoTransporteRepository tipoTransporteRepo) {
        this.repo = repo;
        this.trasladoRepo = trasladoRepo;
        this.transportistaRepo = transportistaRepo;
        this.tipoTransporteRepo = tipoTransporteRepo;
    }

    public List<TrasladoTransportista> getAll() {
        return repo.findAll();
    }

    public List<TrasladoTransportista> getByState(String state) {
        return repo.findByState(state);
    }

    public List<TrasladoTransportista> getByTraslado(int idTraslado) {
        return repo.findByTraslado(idTraslado);
    }

    public TrasladoTransportista getByKey(int idTraslado, String nifTransportista) {
        return repo.findByCompositeKey(idTraslado, nifTransportista)
                .orElseThrow(() -> new NoSuchElementException(
                        "Traslado_Transportista (" + idTraslado + ", " + nifTransportista + ") not found"));
    }

    public TrasladoTransportista create(TrasladoTransportistaDTO dto) {
        List<String> errors = validate(dto, true);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        if (repo.existsByCompositeKey(dto.getIdTraslado(), dto.getNifTransportista())) {
            throw new ValidationException("Record (" + dto.getIdTraslado() + ", " + dto.getNifTransportista() + ") already exists");
        }
        validateForeignKeys(dto, errors);
        if (!errors.isEmpty()) throw new ValidationException(errors);

        repo.insert(new TrasladoTransportista(dto.getIdTraslado(), dto.getNifTransportista(),
                dto.getIdTipoTransporte(), dto.getKmsRecorridos(), dto.getCosto(), "A"));
        return repo.findByCompositeKey(dto.getIdTraslado(), dto.getNifTransportista()).orElseThrow();
    }

    public TrasladoTransportista update(int idTraslado, String nifTransportista, TrasladoTransportistaDTO dto) {
        if (!repo.existsByCompositeKey(idTraslado, nifTransportista)) {
            throw new NoSuchElementException(
                    "Traslado_Transportista (" + idTraslado + ", " + nifTransportista + ") not found");
        }
        List<String> errors = validate(dto, false);
        if (dto.getIdTipoTransporte() != null && !tipoTransporteRepo.existsByIdAndActive(dto.getIdTipoTransporte())) {
            errors.add("ID_Tipo_Transporte " + dto.getIdTipoTransporte() + " does not exist or is not active");
        }
        if (!errors.isEmpty()) throw new ValidationException(errors);

        repo.update(new TrasladoTransportista(idTraslado, nifTransportista,
                dto.getIdTipoTransporte(), dto.getKmsRecorridos(), dto.getCosto(), null));
        return repo.findByCompositeKey(idTraslado, nifTransportista).orElseThrow();
    }

    public void updateState(int idTraslado, String nifTransportista, String newState) {
        if (!repo.existsByCompositeKey(idTraslado, nifTransportista)) {
            throw new NoSuchElementException(
                    "Traslado_Transportista (" + idTraslado + ", " + nifTransportista + ") not found");
        }
        if (!List.of("A", "I", "*").contains(newState))
            throw new ValidationException("Invalid state. Allowed: A, I, *");
        repo.updateState(idTraslado, nifTransportista, newState);
    }

    private List<String> validate(TrasladoTransportistaDTO dto, boolean isCreate) {
        List<String> errors = new ArrayList<>();
        if (isCreate && dto.getIdTraslado() == null) errors.add("ID_Traslado is required");
        if (isCreate && (dto.getNifTransportista() == null || dto.getNifTransportista().isBlank()))
            errors.add("NIF_Transportista is required");
        if (dto.getIdTipoTransporte() == null) errors.add("ID_Tipo_Transporte is required");
        if (dto.getKmsRecorridos() == null) errors.add("Kms_Recorridos is required");
        if (dto.getCosto() == null) errors.add("Costo is required");
        return errors;
    }

    private void validateForeignKeys(TrasladoTransportistaDTO dto, List<String> errors) {
        if (dto.getIdTraslado() != null && !trasladoRepo.existsByIdAndActive(dto.getIdTraslado())) {
            errors.add("ID_Traslado " + dto.getIdTraslado() + " does not exist or is not active");
        }
        if (dto.getNifTransportista() != null && !transportistaRepo.existsByIdAndActive(dto.getNifTransportista())) {
            errors.add("NIF_Transportista " + dto.getNifTransportista() + " does not exist or is not active");
        }
        if (dto.getIdTipoTransporte() != null && !tipoTransporteRepo.existsByIdAndActive(dto.getIdTipoTransporte())) {
            errors.add("ID_Tipo_Transporte " + dto.getIdTipoTransporte() + " does not exist or is not active");
        }
    }
}
