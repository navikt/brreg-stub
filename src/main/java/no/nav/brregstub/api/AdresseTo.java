package no.nav.brregstub.api;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdresseTo {

    private String adresse1;
    private String adresse2;
    private String adresse3;
    private String postnr;
    private String poststed;
    private String land;
    private String landKode;
    private String kommune;

}
