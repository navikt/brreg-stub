package no.nav.brregstub.api.v2;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

import no.nav.brregstub.api.RolleKode;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RsPersonOgRolle {

    @ApiModelProperty(dataType = "java.lang.String", example = "010176100000", required = true)
    @NotBlank
    private String fodselsnr;


    @ApiModelProperty(dataType = "java.lang.String", example = "DAGL", required = true)
    @NotBlank
    private RolleKode rolle;

    @ApiModelProperty(dataType = "java.lang.String", example = "Navn")
    @NotBlank
    private String fornavn;

    @ApiModelProperty(dataType = "java.lang.String", example = "Navnesen")
    @NotBlank
    private String slektsnavn;

    private String adresse1;

    private String postnr;

    private String poststed;
    @ApiModelProperty(example = "false", required = true)
    private boolean fratraadt;
}
