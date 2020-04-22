package no.nav.brregstub.rs;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.brregstub.service.BrregService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/grunndata")
@AllArgsConstructor
public class GrunndataController {

    private BrregService brregService;
    private ObjectMapper objectMapper;

    @GetMapping("/hentRolleutskrift")
    @SneakyThrows
    public ResponseEntity<String> hentRolleutskriftSomGrunndata(@RequestHeader(name = "Nav-Personident") String ident) {
        var grunndata = brregService.hentRolleutskrift(ident);
        if (grunndata == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fant ikke data");
        }
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(grunndata));
    }

    @GetMapping("/hentRoller")
    @SneakyThrows
    public ResponseEntity<String> hentRollerSomGrunndata(@RequestHeader(name = "orgnr") String orgnr) {
        var grunndata = brregService.hentRoller(orgnr);
        if (grunndata == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fant ikke data");
        }
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(grunndata));
    }

}
