package no.nav.brregstub.database.domene;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class HentRolle {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "orgnr must not be null")
    @Column(unique = true)
    private Integer orgnr;

    @NotNull(message = "json must not be null")
    @Column(name = "json", columnDefinition = "text")
    private String json;

}
