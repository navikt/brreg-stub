package no.nav.brregstub.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Egenskap {
    Deltaker,
    Komplementar,
    Kontaktperson,
    Sameier,
    Styre
}
