package no.nav.brregstub.api.v2;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

import no.nav.brregstub.api.Egenskap;

@Getter
@NoArgsConstructor
public class RsRolleStatus {

    @ApiModelProperty(dataType = "java.lang.String", example = "Deltaker", required = true)
    @NotBlank
    private Egenskap egenskap;

    @ApiModelProperty(example = "false", required = true)
    private boolean fratraadt;
}
