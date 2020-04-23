package no.nav.brregstub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import no.nav.brregstub.api.RolleutskriftTo;
import no.nav.brregstub.database.domene.Rolleutskrift;
import no.nav.brregstub.database.repository.RolleutskriftRepository;
import no.nav.brregstub.mapper.RolleutskriftMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HentRolleutskriftService {

    @Autowired
    private RolleutskriftRepository rolleutskriftRepository;

    @Autowired
    private RolleutskriftMapper rolleutskriftMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    public Optional<RolleutskriftTo> opprettRolleutskriftGrunndata(RolleutskriftTo rolleinnhaver) {
        rolleutskriftMapper.map(rolleinnhaver);

        var rollutskrift = rolleutskriftRepository.findByIdent(rolleinnhaver.getFnr())
                                                  .orElseGet(() -> {
                                                      var rolleutskrift = new Rolleutskrift();
                                                      rolleutskrift.setIdent(rolleinnhaver.getFnr());
                                                      return rolleutskrift;
                                                  });

        rollutskrift.setJson(objectMapper.writeValueAsString(rolleinnhaver));

        rolleutskriftRepository.save(rollutskrift);
        return Optional.of(rolleinnhaver);
    }

    @SneakyThrows
    public Optional<RolleutskriftTo> hentRolleinnhaverTo(String ident) {
        var rolleutskrift = rolleutskriftRepository.findByIdent(ident);

        if (rolleutskrift.isPresent()) {
            var to = objectMapper.readValue(rolleutskrift.get().getJson(), RolleutskriftTo.class);
            return Optional.of(to);
        }
        return Optional.empty();
    }

    public void slettRolleutskriftGrunndata(String ident) {
        rolleutskriftRepository.findByIdent(ident).ifPresent(ru -> rolleutskriftRepository.delete(ru));
    }
}
