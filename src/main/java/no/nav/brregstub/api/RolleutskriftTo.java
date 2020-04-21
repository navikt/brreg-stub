package no.nav.brregstub.api;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RolleutskriftTo {

    private String fnr;
    @ApiModelProperty(dataType = "java.lang.String", example = "2020-01-01")
    private LocalDate fodselsdato;
    private NavnTo navn;
    private AdresseTo adresse;
    List<RolleEnhetTo> enheter;
}
