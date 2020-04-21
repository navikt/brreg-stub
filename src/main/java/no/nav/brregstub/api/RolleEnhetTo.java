package no.nav.brregstub.api;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RolleEnhetTo {
    @ApiModelProperty(dataType = "java.lang.String", example = "2020-01-01")
    private LocalDate registreringsdato;
    private String beskrivelse;
    private Integer orgNr;
    private NavnTo navn;
    private AdresseTo forretningsAdresse;
    private AdresseTo postAdresse;
}
