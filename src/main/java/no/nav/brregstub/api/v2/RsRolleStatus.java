package no.nav.brregstub.api.v2;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

import no.nav.brregstub.api.RolleKode;

@Data
@NoArgsConstructor
public class RsRolleStatus {

    @ApiModelProperty(dataType = "java.lang.String", example = "DAGL", required = true)
    @NotBlank
    private RolleKode rolle;

    @ApiModelProperty(example = "false", required = true)
    private boolean fratraadt;
}
