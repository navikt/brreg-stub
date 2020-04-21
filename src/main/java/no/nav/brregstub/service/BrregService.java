package no.nav.brregstub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import no.nav.brregstub.api.RolleutskriftTo;
import no.nav.brregstub.database.domene.Rolleutskrift;
import no.nav.brregstub.database.repository.RolleutskriftRepository;
import no.nav.brregstub.mapper.RolleutskriftMapper;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXB;
import java.util.Optional;

@Service
public class BrregService {

    @Autowired
    private RolleutskriftRepository rolleutskriftRepository;

    @Autowired
    private RolleutskriftMapper rolleutskriftMapper;

    @Autowired
    private ObjectMapper objectMapper;


    public Grunndata hentRoller(String orgnummer) {
        var in = this.getClass().getResourceAsStream("/response/HentRollerResponse.xml");
        var grunndata = JAXB.unmarshal(in, Grunndata.class);
        grunndata.getResponseHeader().setOrgnr(Integer.parseInt(orgnummer));
        return grunndata;
    }

    @SneakyThrows
    public no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata hentRolleutskrift(String requestId) {
        var rolleutskrift =  rolleutskriftRepository.findByIdent(requestId);
        if (rolleutskrift.isPresent()) {
            var d = objectMapper.readValue(rolleutskrift.get().getJson(), RolleutskriftTo.class);
            return rolleutskriftMapper.map(d);
        }

        var in = this.getClass().getResourceAsStream("/response/HentRolleutskriftResponse.xml");
        var grunndata = JAXB.unmarshal(in, no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.class);
        grunndata.getResponseHeader().setFodselsnr(requestId);
        return grunndata;
    }

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
