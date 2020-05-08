package no.nav.brregstub.api.v2;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import no.nav.brregstub.api.RolleKode;

@Data
@NoArgsConstructor
public class RsRolle {

    @ApiModelProperty(dataType = "java.lang.String", example = "2020-01-01")
    @NotNull
    private LocalDate registreringsdato;

    @ApiModelProperty(dataType = "java.lang.Enum", example = "INNH", required = true)
    @NotBlank
    private RolleKode rolle;

    @ApiModelProperty(example = "998877665", required = true)
    @NotNull
    private Integer orgNr;

    private RsNavn foretaksNavn;

    private RsAdresse forretningsAdresse;

    private RsAdresse postAdresse;

    private List<RsRolleStatus> personRolle;
}
