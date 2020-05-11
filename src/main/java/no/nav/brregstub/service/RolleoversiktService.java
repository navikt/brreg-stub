package no.nav.brregstub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import no.nav.brregstub.api.common.Egenskap;
import no.nav.brregstub.api.common.RsNavn;
import no.nav.brregstub.api.v1.OrganisasjonTo;
import no.nav.brregstub.api.v1.PersonOgRolleTo;
import no.nav.brregstub.api.v1.RolleoversiktTo;
import no.nav.brregstub.api.v1.SamendringTo;
import no.nav.brregstub.api.v2.RsRolle;
import no.nav.brregstub.api.v2.RsRolleoversikt;
import no.nav.brregstub.database.domene.HentRolle;
import no.nav.brregstub.database.domene.Rolleoversikt;
import no.nav.brregstub.database.repository.HentRolleRepository;
import no.nav.brregstub.database.repository.RolleoversiktRepository;
import no.nav.brregstub.mapper.HentRolleMapper;
import no.nav.brregstub.mapper.RolleoversiktMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolleoversiktService {

    private final RolleoversiktRepository rolleoversiktRepository;
    private final HentRolleRepository rolleRepository;

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public Optional<RolleoversiktTo> opprettRolleoversiktV1(RolleoversiktTo rolleoversiktTo) {
        RolleoversiktMapper.map(rolleoversiktTo); //Sjekker om object kan mappes

        var rolleoversikt = rolleoversiktRepository.findByIdent(rolleoversiktTo.getFnr())
                .orElseGet(() -> {
                    var rolleutskrift = new Rolleoversikt();
                    rolleutskrift.setIdent(rolleoversiktTo.getFnr());
                    return rolleutskrift;
                });

        rolleoversikt.setJson(objectMapper.writeValueAsString(rolleoversiktTo));

        rolleoversiktRepository.save(rolleoversikt);
        return Optional.of(rolleoversiktTo);
    }

    @SneakyThrows
    public Optional<RsRolleoversikt> opprettRolleoversiktV2(RsRolleoversikt rsRolleoversikt) {
        RolleoversiktMapper.map(rsRolleoversikt); //Sjekker om object kan mappes

        setRollebeskrivelse(rsRolleoversikt);

        var organisasjoner = byggOrganisasjoner(rsRolleoversikt);

        var rolleoversikt = rolleoversiktRepository.findByIdent(rsRolleoversikt.getFnr())
                .orElseGet(() -> {
                    var rolleutskrift = new Rolleoversikt();
                    rolleutskrift.setIdent(rsRolleoversikt.getFnr());
                    return rolleutskrift;
                });

        rolleoversikt.setJson(objectMapper.writeValueAsString(rsRolleoversikt));

        rolleoversiktRepository.save(rolleoversikt);

        for (var organisasjon : organisasjoner) {
            lagreEllerOppdaterOrganisasjon(organisasjon);
        }

        return Optional.of(rsRolleoversikt);
    }

    @SneakyThrows
    public Optional<RolleoversiktTo> hentRolleoversiktV1(String ident) {
        var rolleoversikt = rolleoversiktRepository.findByIdent(ident);

        if (rolleoversikt.isPresent()) {
            var to = objectMapper.readValue(rolleoversikt.get().getJson(), RolleoversiktTo.class);
            return Optional.of(to);
        }
        return Optional.empty();
    }

    @SneakyThrows
    public Optional<RsRolleoversikt> hentRolleoversiktV2(String ident) {
        var rolleoversikt = rolleoversiktRepository.findByIdent(ident);

        if (rolleoversikt.isPresent()) {
            var rsRolleoversikt = objectMapper.readValue(rolleoversikt.get().getJson(), RsRolleoversikt.class);
            return Optional.of(rsRolleoversikt);
        }
        return Optional.empty();
    }

    public void slettRolleoversikt(String ident) {
        rolleoversiktRepository.findByIdent(ident).ifPresent(rolleoversiktRepository::delete);
    }

    private List<OrganisasjonTo> byggOrganisasjoner(RsRolleoversikt rsRolleoversikt) {
        var understatuser = rsRolleoversikt.getUnderstatuser();
        var hovedstatus = rsRolleoversikt.getHovedstatus();
        var enheter = rsRolleoversikt.getEnheter();

        List<OrganisasjonTo> organisasjoner = new ArrayList<>();
        for (var enhet : enheter) {
            var orgNr = enhet.getOrgNr();
            var registreringsdato = enhet.getRegistreringsdato();
            var personRoller = enhet.getPersonRolle();

            OrganisasjonTo organisasjon = null;
            for (var eksisterendeOrganisasjon : organisasjoner) {
                if (orgNr.equals(eksisterendeOrganisasjon.getOrgnr())) {
                    organisasjon = eksisterendeOrganisasjon;
                    break;
                }
            }
            if (organisasjon == null) {
                organisasjon = OrganisasjonTo.builder()
                        .orgnr(orgNr)
                        .hovedstatus(hovedstatus)
                        .understatuser(understatuser)
                        .registreringsdato(registreringsdato)
                        .build();
                organisasjoner.add(organisasjon);
            }

            for (var personRolle : personRoller) {
                var egenskap = personRolle.getEgenskap();
                if (egenskap.equals(Egenskap.Deltager)) {
                    var deltakere = organisasjon.getDeltakere();
                    oppdaterSamendringsliste(deltakere, enhet, rsRolleoversikt.getFnr(), rsRolleoversikt.getNavn(), personRolle.isFratraadt());
                    organisasjon.setDeltakere(deltakere);
                } else if (egenskap.equals(Egenskap.Komplementar)) {
                    var komplementar = organisasjon.getKomplementar();
                    oppdaterSamendringsliste(komplementar, enhet, rsRolleoversikt.getFnr(), rsRolleoversikt.getNavn(), personRolle.isFratraadt());
                    organisasjon.setKomplementar(komplementar);
                } else if (egenskap.equals(Egenskap.Kontaktperson)) {
                    var kontaktperson = organisasjon.getKontaktperson();
                    oppdaterSamendringsliste(kontaktperson, enhet, rsRolleoversikt.getFnr(), rsRolleoversikt.getNavn(), personRolle.isFratraadt());
                    organisasjon.setKontaktperson(kontaktperson);
                } else if (egenskap.equals(Egenskap.Sameier)) {
                    var sameier = organisasjon.getSameier();
                    oppdaterSamendringsliste(sameier, enhet, rsRolleoversikt.getFnr(), rsRolleoversikt.getNavn(), personRolle.isFratraadt());
                    organisasjon.setSameier(sameier);
                } else if (egenskap.equals(Egenskap.Styre)) {
                    var styre = organisasjon.getStyre();
                    oppdaterSamendringsliste(styre, enhet, rsRolleoversikt.getFnr(), rsRolleoversikt.getNavn(), personRolle.isFratraadt());
                    organisasjon.setStyre(styre);
                }
            }
        }

        return organisasjoner;
    }

    private void oppdaterSamendringsliste(
            SamendringTo samendring,
            RsRolle enhet,
            String fnr,
            RsNavn navn,
            boolean fratraadt
    ) {
        if (samendring == null) {
            samendring = new SamendringTo();
            samendring.setRegistringsDato(enhet.getRegistreringsdato());
        }
        if (samendring.getRoller() == null) {
            samendring.setRoller(new ArrayList<>());
        }
        samendring.getRoller().add(PersonOgRolleTo.builder()
                .fodselsnr(fnr)
                .rolle(enhet.getRolle().name())
                .rollebeskrivelse(enhet.getRolle().getBeskrivelse())
                .fornavn(navn.getNavn1())
                .slektsnavn(navn.getNavn3())
                .adresse1(enhet.getForretningsAdresse().getAdresse1())
                .postnr(enhet.getForretningsAdresse().getPostnr())
                .poststed(enhet.getForretningsAdresse().getPoststed())
                .fratraadt(fratraadt)
                .build());
    }

    private void lagreEllerOppdaterOrganisasjon(OrganisasjonTo nyOrganisasjon) {
        HentRolleMapper.map(nyOrganisasjon); //sjekker om input kan mappes før lagring

        var rolleutskrift = rolleRepository.findByOrgnr(nyOrganisasjon.getOrgnr())
                .orElseGet(() -> {
                    var hentRolle = new HentRolle();
                    hentRolle.setOrgnr(nyOrganisasjon.getOrgnr());
                    return hentRolle;
                });

        try {
            boolean oppdatert;
            var json = rolleutskrift.getJson();
            if (json == null) {
                log.info("Organisasjon eksisterer ikke fra før. Må opprettes.");
                rolleutskrift.setJson(objectMapper.writeValueAsString(nyOrganisasjon));
                oppdatert = true;
            } else {
                var eksisterendeOrganisasjon = objectMapper.readValue(json, OrganisasjonTo.class);
                log.info("Oppdaterer organisasjon med orgnummer {}", nyOrganisasjon.getOrgnr());
                oppdatert = oppdaterEksisterendeOrganisasjon(eksisterendeOrganisasjon, nyOrganisasjon);
                rolleutskrift.setJson(objectMapper.writeValueAsString(eksisterendeOrganisasjon));
            }
            if (oppdatert) {
                rolleRepository.save(rolleutskrift);
            }
        } catch (JsonProcessingException e) {
            log.error("Kunne ikke lagre organisasjon med orgnummer {}", nyOrganisasjon.getOrgnr(), e);
        }
    }

    private boolean oppdaterEksisterendeOrganisasjon(
            OrganisasjonTo eksisterende,
            OrganisasjonTo ny
    ) {
        boolean oppdatert = false;
        if (ny.getKontaktperson() != null) {
            if (eksisterende.getKontaktperson() != null) {
                oppdatert = leggTilHvisIkkeDuplikat("kontaktperson", eksisterende.getOrgnr(), eksisterende.getKontaktperson().getRoller(), ny.getKontaktperson().getRoller());
            } else {
                eksisterende.setKontaktperson(ny.getKontaktperson());
                oppdatert = true;
            }
        }
        if (ny.getSameier() != null) {
            if (eksisterende.getSameier() != null) {
                oppdatert = leggTilHvisIkkeDuplikat("sameier", eksisterende.getOrgnr(), eksisterende.getSameier().getRoller(), ny.getSameier().getRoller());
            } else {
                eksisterende.setSameier(ny.getSameier());
                oppdatert = true;
            }
        }
        if (ny.getStyre() != null) {
            if (eksisterende.getStyre() != null) {
                oppdatert = leggTilHvisIkkeDuplikat("styre", eksisterende.getOrgnr(), eksisterende.getStyre().getRoller(), ny.getStyre().getRoller());
            } else {
                eksisterende.setStyre(ny.getStyre());
                oppdatert = true;
            }
        }
        if (ny.getKomplementar() != null) {
            if (eksisterende.getKomplementar() != null) {
                oppdatert = leggTilHvisIkkeDuplikat("komplementar", eksisterende.getOrgnr(), eksisterende.getKomplementar().getRoller(), ny.getKomplementar().getRoller());
            } else {
                eksisterende.setKomplementar(ny.getKomplementar());
                oppdatert = true;
            }
        }
        if (ny.getDeltakere() != null) {
            if (eksisterende.getDeltakere() != null) {
                oppdatert = leggTilHvisIkkeDuplikat("deltaker", eksisterende.getOrgnr(), eksisterende.getDeltakere().getRoller(), ny.getDeltakere().getRoller());
            } else {
                eksisterende.setDeltakere(ny.getDeltakere());
                oppdatert = true;
            }
        }
        return oppdatert;
    }

    private boolean leggTilHvisIkkeDuplikat(
            String typeSamendring,
            Integer orgnr,
            List<PersonOgRolleTo> eksisterendeRoller,
            List<PersonOgRolleTo> nyeRoller
    ) {
        boolean oppdatert = false;
        for (var nyRolle : nyeRoller) {
            if (!isRolleDuplikat(eksisterendeRoller, nyRolle)) {
                eksisterendeRoller.add(nyRolle);
                oppdatert = true;
            } else {
                log.info("Samendring av type '{}': Person med fnr '{}', rolle '{}' og fratredelsestatus '{}' finnes allerede i organisasjon '{}'.",
                        typeSamendring,
                        nyRolle.getFodselsnr(),
                        nyRolle.getRolle(),
                        nyRolle.isFratraadt(),
                        orgnr);
            }
        }
        return oppdatert;
    }

    private boolean isRolleDuplikat(
            List<PersonOgRolleTo> eksisterendeRoller,
            PersonOgRolleTo nyRolle
    ) {
        var fodselsnr = nyRolle.getFodselsnr();
        var rolle = nyRolle.getRolle();
        var fratraadt = nyRolle.isFratraadt();
        for (var eksisterendePerson : eksisterendeRoller) {
            if (fodselsnr.equals(eksisterendePerson.getFodselsnr())
                    && rolle.equals(eksisterendePerson.getRolle())
                    && fratraadt == eksisterendePerson.isFratraadt()) {
                return true;
            }
        }
        return false;
    }

    private void setRollebeskrivelse(RsRolleoversikt rsRolleoversikt) {
        for (var enhet : rsRolleoversikt.getEnheter()) {
            if (enhet.getRollebeskrivelse() == null || enhet.getRollebeskrivelse().isBlank()) {
                enhet.setRollebeskrivelse(enhet.getRolle().getBeskrivelse());
            }
        }
    }
}
