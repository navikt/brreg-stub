package no.nav.brregstub.api.common;

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
