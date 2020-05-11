package no.nav.brregstub.api.v1;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganisasjonTo {

    @ApiModelProperty(example = "998877665", required = true)
    @NotNull
    private Integer orgnr;
    @ApiModelProperty(example = "0", required = true)
    private Integer hovedstatus = 0;
    private List<Integer> understatuser = new LinkedList<>();
    @ApiModelProperty(dataType = "java.lang.String", example = "1976-01-01", required = true)
    @NotNull
    private LocalDate registreringsdato;
    @Valid private SamendringTo kontaktperson;
    @Valid private SamendringTo sameier;
    @Valid private SamendringTo styre;
    @Valid private SamendringTo komplementar;
    @Valid private SamendringTo deltakere;
}
