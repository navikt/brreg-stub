package no.nav.brregstub.rs;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.brregstub.api.OrganisasjonTo;
import no.nav.brregstub.api.RolleKode;
import no.nav.brregstub.exception.CouldNotCreateStubException;
import no.nav.brregstub.exception.NotFoundException;
import no.nav.brregstub.service.HentRolleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/hentrolle")
@AllArgsConstructor
public class HentRolleController {

    private HentRolleService service;


    @PostMapping
    public ResponseEntity<OrganisasjonTo> lagreEllerOppdaterHentRolleStub(@RequestBody OrganisasjonTo request) {
        var organisasjonTo = service.lagreEllerOppdaterDataForHentRolle(request)
                                    .orElseThrow(() -> new CouldNotCreateStubException(""));
        return ResponseEntity.status(HttpStatus.CREATED).body(organisasjonTo);
    }

    @GetMapping("/{orgnr}")
    public ResponseEntity<OrganisasjonTo> hentGrunndata(@PathVariable Integer orgnr) {
        var grunndata = service.hentRolle(orgnr)
                               .orElseThrow(() -> new NotFoundException(String.format("Kunne ikke finne roller for :%s",
                                                                                      orgnr)));
        return ResponseEntity.status(HttpStatus.OK).body(grunndata);
    }

    @DeleteMapping("/{orgnr}")
    public ResponseEntity deleteGrunndata(@PathVariable Integer orgnr) {
        service.slettHentRolle(orgnr);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/rollekode")
    public ResponseEntity<EnumMap<RolleKode, String>> hentRollekoder() {
        var returValue  = new EnumMap<RolleKode, String>(RolleKode.class);
        for (RolleKode rolleKode: RolleKode.values()) {
            returValue.put(rolleKode, rolleKode.getBeskrivelse());
        }

        return ResponseEntity.status(HttpStatus.OK).body(returValue);
    }

}
