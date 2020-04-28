package no.nav.brregstub.api;


import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class AdresseTo {

    @ApiModelProperty(example = "Dollyveien 1", required = true)
    @NotBlank private String adresse1;
    private String adresse2;
    private String adresse3;
    @ApiModelProperty(example = "0576", required = true)
    private String postnr;
    @ApiModelProperty(example = "Oslo", required = true)
    private String poststed;
    @ApiModelProperty(example = "NO", required = true)
    private String landKode;
    @ApiModelProperty(example = "0301", value = "Påkrevd for roller")
    private String kommunenr;

}
