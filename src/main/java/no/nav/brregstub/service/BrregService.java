package no.nav.brregstub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import no.nav.brregstub.api.OrganisasjonTo;
import no.nav.brregstub.api.RolleutskriftTo;
import no.nav.brregstub.database.repository.HentRolleRepository;
import no.nav.brregstub.database.repository.RolleutskriftRepository;
import no.nav.brregstub.mapper.HentRolleMapper;
import no.nav.brregstub.mapper.RolleutskriftMapper;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXB;

@Service
@AllArgsConstructor
public class BrregService {

    private RolleutskriftRepository rolleutskriftRepository;

    private HentRolleRepository hentRolleRepository;

    private RolleutskriftMapper rolleutskriftMapper;

    private HentRolleMapper hentRolleMapper;

    private ObjectMapper objectMapper;


    @SneakyThrows
    public Grunndata hentRoller(String orgnummer) {
        var hentRolle = hentRolleRepository.findByOrgnr(Integer.parseInt(orgnummer));
        if (hentRolle.isPresent()) {
            var fromDb = objectMapper.readValue(hentRolle.get().getJson(), OrganisasjonTo.class);
            return hentRolleMapper.map(fromDb);
        }
        var in = this.getClass().getResourceAsStream("/response/HentRollerResponse.xml");
        var grunndata = JAXB.unmarshal(in, Grunndata.class);
        grunndata.getResponseHeader().setOrgnr(Integer.parseInt(orgnummer));
        return grunndata;
    }

    @SneakyThrows
    public no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata hentRolleutskrift(String requestId) {
        var rolleutskrift = rolleutskriftRepository.findByIdent(requestId);
        if (rolleutskrift.isPresent()) {
            var d = objectMapper.readValue(rolleutskrift.get().getJson(), RolleutskriftTo.class);
            return rolleutskriftMapper.map(d);
        }

        var in = this.getClass().getResourceAsStream("/response/HentRolleutskriftResponse.xml");
        var grunndata = JAXB.unmarshal(in, no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.class);
        grunndata.getResponseHeader().setFodselsnr(requestId);
        return grunndata;
    }
}
