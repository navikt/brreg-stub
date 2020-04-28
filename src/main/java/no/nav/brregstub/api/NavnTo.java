package no.nav.brregstub.api;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NavnTo {

    @ApiModelProperty(example = "Navn", required = true)
    @NotBlank private String navn1;
    private String navn2;
    @ApiModelProperty(example = "Navnesen")
    private String navn3;

}
