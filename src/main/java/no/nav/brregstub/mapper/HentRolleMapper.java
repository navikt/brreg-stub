package no.nav.brregstub.mapper;

import lombok.SneakyThrows;
import no.nav.brregstub.api.OrganisasjonTo;
import no.nav.brregstub.api.PersonOgRolleTo;
import no.nav.brregstub.api.SamendringTo;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata.Melding;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata.Melding.Eierkommune.Samendring;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata.ResponseHeader;
import no.nav.brregstub.tjenestekontrakter.hentroller.Grunndata.ResponseHeader.UnderStatus.UnderStatusMelding;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_DATE;

@Component
public class HentRolleMapper {


    public static final String TJENESTE_NAVN = "hentRoller";

    public Grunndata map(OrganisasjonTo to) {
        var grunndata = new Grunndata();
        var responseHeader = mapTilResponseHeader(to);
        var melding = mapTilMelding(to);
        grunndata.setResponseHeader(responseHeader);
        grunndata.setMelding(melding);
        return grunndata;
    }

    private static ResponseHeader mapTilResponseHeader(OrganisasjonTo to) {
        var responseHeader = new ResponseHeader();
        responseHeader.setProssessDato(localDateToXmlGregorianCalendar(LocalDate.now()));
        responseHeader.setTjeneste(TJENESTE_NAVN);
        responseHeader.setOrgnr(to.getOrgnr());
        responseHeader.setHovedStatus(to.getHovedstatus() == null ? 0 : to.getHovedstatus());
        if (to.getUnderstatuser() != null) {
            var underStatus = new ResponseHeader.UnderStatus();
            for (Integer understatus : to.getUnderstatuser()) {
                var underStatusMelding = new UnderStatusMelding();
                underStatusMelding.setKode(understatus);
                underStatusMelding.setValue(understatus.toString());

                underStatus.getUnderStatusMelding().add(underStatusMelding);
            }
            responseHeader.setUnderStatus(underStatus);
        }
        return responseHeader;
    }

    private static Melding mapTilMelding(OrganisasjonTo to) {
        var melding = new Melding();
        melding.setOrganisasjonsnummer(mapTilOrganisasjonsNummer(to));
        melding.setKontaktperson(mapTilKontaktperson(to));
        melding.setStyre(mapTilStyre(to));
        melding.setDeltakere(mapTilDeltakere(to));
        melding.setKomplementar(mapTilKomplementar(to));
        melding.setSameiere(mapTilSameiere(to));
        melding.setTjeneste(TJENESTE_NAVN);

        return melding;
    }

    private static Melding.Kontaktperson mapTilKontaktperson(OrganisasjonTo to) {
        var kontaktperson = new Melding.Kontaktperson();
        kontaktperson.getSamendring().add(mapTilSamendring(to.getKontaktperson()));
        return kontaktperson;
    }

    private static Melding.Styre mapTilStyre(OrganisasjonTo to) {
        var styre = new Melding.Styre();
        styre.getSamendring().add(mapTilSamendring(to.getStyre()));
        return styre;
    }

    private static Melding.Deltakere mapTilDeltakere(OrganisasjonTo to) {
        var deltakere = new Melding.Deltakere();
        deltakere.getSamendring().add(mapTilSamendring(to.getDeltakere()));
        return deltakere;
    }

    private static Melding.Komplementar mapTilKomplementar(OrganisasjonTo to) {
        var komplementar = new Melding.Komplementar();
        komplementar.getSamendring().add(mapTilSamendring(to.getKomplementar()));
        return komplementar;
    }

    private static Melding.Sameiere mapTilSameiere(OrganisasjonTo to) {
        var sameier = new Melding.Sameiere();
        sameier.getSamendring().add(mapTilSamendring(to.getSameier()));
        return sameier;
    }

    private static Samendring mapTilSamendring(SamendringTo to) {
        var samendring = new Samendring();
        samendring.setSamendringstype(to.getType());
        samendring.setBeskrivelse(to.getBeskrivelse());
        samendring.setRegistreringsDato(localDateToXmlGregorianCalendar(to.getRegistringsDato()));

        for (PersonOgRolleTo rolle : to.getRoller()) {
            samendring.getRolle().add(mapTilSamendringRolle(rolle));
        }

        return samendring;
    }

    private static Samendring.Rolle mapTilSamendringRolle(PersonOgRolleTo to) {
        var rolle = new Samendring.Rolle();
        rolle.setBeskrivelse(to.getRolle().getBeskrivelse());
        rolle.setRolletype(to.getRolle().name());

        var person = new Samendring.Rolle.Person();
        person.setFodselsnr(to.getFodselsnr());
        person.setFornavn(to.getFornavn());
        person.setSlektsnavn(to.getSlektsnavn());
        person.setAdresse1(to.getAdresse1());
        person.setFratraadt(to.isFratraadt() ? "J" : "N");
        person.setPoststed(to.getPoststed());
        var land = new Samendring.Rolle.Person.Land();
        land.setLandkode4("NOR");
        land.setValue("Norge");
        person.setLand(land);
        rolle.getPerson().add(person);
        return rolle;
    }

    private static Melding.Organisasjonsnummer mapTilOrganisasjonsNummer(OrganisasjonTo to) {
        var orgnr = new Melding.Organisasjonsnummer();
        orgnr.setRegistreringsDato(localDateToXmlGregorianCalendar(to.getRegistreringsdato()));
        orgnr.setValue(String.valueOf(to.getOrgnr()));
        return orgnr;
    }

    @SneakyThrows
    private static XMLGregorianCalendar localDateToXmlGregorianCalendar(LocalDate date) {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.format(ISO_DATE));
    }

}
