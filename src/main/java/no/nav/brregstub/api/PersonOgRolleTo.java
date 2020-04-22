package no.nav.brregstub.api;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PersonOgRolleTo {

    private String fodselsnr;
    private RolleKode rolle;
    private String beskrivelse;
    private String fornavn;
    private String slektsnavn;
    private String adresse1;
    private String postnr;
    private String poststed;
    private boolean fratraadt;
}
