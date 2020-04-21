package no.nav.brregstub.rs;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.brregstub.api.RolleutskriftTo;
import no.nav.brregstub.exception.CouldNotCreateStubException;
import no.nav.brregstub.exception.NotFoundException;
import no.nav.brregstub.service.BrregService;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/rolleutskrift")
@RequiredArgsConstructor
public class HentRolleutskriftController {

    @Autowired
    private BrregService brregService;


    @PostMapping
    public ResponseEntity<RolleutskriftTo> opprettGrunndata(@RequestBody RolleutskriftTo rolleinnhaver) {
        var grunndata = brregService.opprettRolleutskriftGrunndata(rolleinnhaver)
                                               .orElseThrow(() -> new CouldNotCreateStubException(""));
        return ResponseEntity.status(HttpStatus.CREATED).body(grunndata);
    }

    @GetMapping
    public ResponseEntity<RolleutskriftTo> hentGrunndata(@RequestHeader(name = "Nav-Personident") String ident) {
        var grunndata = brregService.hentRolleinnhaverTo(ident)
                                             .orElseThrow(() -> new NotFoundException(String.format("Kunne ikke finne person med fnr:%s", ident)));
        return ResponseEntity.status(HttpStatus.OK).body(grunndata);
    }

    @GetMapping("/mock")
    public ResponseEntity<Grunndata> staticMock(@RequestHeader(name = "Nav-Personident") String ident) {
        var grunndata = brregService.hentRolleutskrift(ident);
        return ResponseEntity.status(HttpStatus.OK).body(grunndata);
    }


    @DeleteMapping
    public ResponseEntity deleteGrunndata(@RequestHeader(name = "Nav-Personident") String ident) {
        brregService.slettRolleutskriftGrunndata(ident);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}