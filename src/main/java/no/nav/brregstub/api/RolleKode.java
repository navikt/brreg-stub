package no.nav.brregstub.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RolleKode {
    BEST("Bestyrende reder"),
    BOBE("Bostyrer"),
    DAGL("Daglig leder/ adm direktør"),
    DELT("Deltaker"),
    DTPR("Deltaker med proratarisk ansvar (delt ansvar)"),
    DTSO("Deltaker med solidarisk ansvar (fullt ansvarlig)"),
    FFØR("Forretningsfører"),
    INNH("Innhaver"),
    KOMP("Komplementar"),
    KONT("Kontaktperson"),
    LEDE("Styrets leder"),
    MEDL("Styremedlem"),
    NEST("Nestleder"),
    OBS("Observatør"),
    REGN("Regnskapsfører"),
    STYR("Styre"),
    L("Lever"),
    SAM("Sameiere");

    private final String beskrivelse;
}
