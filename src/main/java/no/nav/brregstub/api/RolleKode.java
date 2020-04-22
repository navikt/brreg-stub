package no.nav.brregstub.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RolleKode {
    DAGL("Daglig leder/ adm direktør"),
    STYR("Styre"),
    LEDE("Styrets leder"),
    MEDL("Styremedlem"),
    L("Lever"),
    OBS("Observatør"),
    REVI("Revisor"),
    REGN("Regnskapsfører");

    private final String beskrivelse;
}
