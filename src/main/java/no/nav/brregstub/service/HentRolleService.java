package no.nav.brregstub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import no.nav.brregstub.api.OrganisasjonTo;
import no.nav.brregstub.database.domene.HentRolle;
import no.nav.brregstub.database.repository.HentRolleRepository;
import no.nav.brregstub.mapper.HentRolleMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class HentRolleService {

    private HentRolleRepository hentRolleRepository;

    private HentRolleMapper hentRolleMapper;

    private ObjectMapper objectMapper;

    @SneakyThrows
    public Optional<OrganisasjonTo> lagreEllerOppdaterDataForHentRolle(OrganisasjonTo request) {
        hentRolleMapper.map(request);

        var rollutskrift = hentRolleRepository.findByOrgnr(request.getOrgnr())
                                              .orElseGet(() -> {
                                                  var hentRolle = new HentRolle();
                                                  hentRolle.setOrgnr(request.getOrgnr());
                                                  return hentRolle;
                                              });

        rollutskrift.setJson(objectMapper.writeValueAsString(request));

        hentRolleRepository.save(rollutskrift);
        return Optional.of(request);
    }

    @SneakyThrows
    public Optional<OrganisasjonTo> hentRolle(Integer ident) {
        var hentRolle = hentRolleRepository.findByOrgnr(ident);

        if (hentRolle.isPresent()) {
            var to = objectMapper.readValue(hentRolle.get().getJson(), OrganisasjonTo.class);
            return Optional.of(to);
        }
        return Optional.empty();
    }

    public void slettHentRolle(Integer ident) {
        hentRolleRepository.findByOrgnr(ident).ifPresent(ru -> hentRolleRepository.delete(ru));
    }

}
