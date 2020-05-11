package no.nav.brregstub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Optional;

import no.nav.brregstub.api.v1.RolleoversiktTo;
import no.nav.brregstub.api.v2.RsRolleoversikt;
import no.nav.brregstub.database.domene.Rolleoversikt;
import no.nav.brregstub.database.repository.RolleoversiktRepository;
import no.nav.brregstub.mapper.RolleoversiktMapper;

@Service
@RequiredArgsConstructor
public class RolleoversiktService {

    private final RolleoversiktRepository rolleoversiktRepository;

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public Optional<RolleoversiktTo> opprettRolleoversiktV1(RolleoversiktTo rolleoversiktTo) {
        RolleoversiktMapper.map(rolleoversiktTo); //Sjekker om object kan mappes

        var rolleoversikt = rolleoversiktRepository.findByIdent(rolleoversiktTo.getFnr())
                .orElseGet(() -> {
                    var rolleutskrift = new Rolleoversikt();
                    rolleutskrift.setIdent(rolleoversiktTo.getFnr());
                    return rolleutskrift;
                });

        rolleoversikt.setJson(objectMapper.writeValueAsString(rolleoversiktTo));

        rolleoversiktRepository.save(rolleoversikt);
        return Optional.of(rolleoversiktTo);
    }

    @SneakyThrows
    public Optional<RsRolleoversikt> opprettRolleoversiktV2(RsRolleoversikt rsRolleoversikt) {
        RolleoversiktMapper.map(rsRolleoversikt); //Sjekker om object kan mappes

        setRollebeskrivelse(rsRolleoversikt);

        var rolleoversikt = rolleoversiktRepository.findByIdent(rsRolleoversikt.getFnr())
                .orElseGet(() -> {
                    var rolleutskrift = new Rolleoversikt();
                    rolleutskrift.setIdent(rsRolleoversikt.getFnr());
                    return rolleutskrift;
                });

        rolleoversikt.setJson(objectMapper.writeValueAsString(rsRolleoversikt));

        rolleoversiktRepository.save(rolleoversikt);
        return Optional.of(rsRolleoversikt);
    }

    @SneakyThrows
    public Optional<RolleoversiktTo> hentRolleoversiktV1(String ident) {
        var rolleoversikt = rolleoversiktRepository.findByIdent(ident);

        if (rolleoversikt.isPresent()) {
            var to = objectMapper.readValue(rolleoversikt.get().getJson(), RolleoversiktTo.class);
            return Optional.of(to);
        }
        return Optional.empty();
    }

    @SneakyThrows
    public Optional<RsRolleoversikt> hentRolleoversiktV2(String ident) {
        var rolleoversikt = rolleoversiktRepository.findByIdent(ident);

        if (rolleoversikt.isPresent()) {
            var rsRolleoversikt = objectMapper.readValue(rolleoversikt.get().getJson(), RsRolleoversikt.class);
            return Optional.of(rsRolleoversikt);
        }
        return Optional.empty();
    }

    public void slettRolleoversikt(String ident) {
        rolleoversiktRepository.findByIdent(ident).ifPresent(rolleoversiktRepository::delete);
    }

    private void setRollebeskrivelse(RsRolleoversikt rsRolleoversikt) {
        for (var enhet : rsRolleoversikt.getEnheter()) {
            if (enhet.getRollebeskrivelse() == null || enhet.getRollebeskrivelse().isBlank()) {
                enhet.setRollebeskrivelse(enhet.getRolle().getBeskrivelse());
            }
        }
    }
}
