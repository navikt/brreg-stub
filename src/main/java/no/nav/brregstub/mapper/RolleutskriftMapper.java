package no.nav.brregstub.mapper;

import lombok.SneakyThrows;
import no.nav.brregstub.api.AdresseTo;
import no.nav.brregstub.api.NavnTo;
import no.nav.brregstub.api.RolleEnhetTo;
import no.nav.brregstub.api.RolleutskriftTo;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.AdresseType1;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.AdresseType2;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.Melding;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.Melding.RolleInnehaver;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.Melding.RolleInnehaver.Fodselsdato;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.Melding.Roller.Enhet;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.ResponseHeader;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.ResponseHeader.UnderStatus;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.Grunndata.ResponseHeader.UnderStatus.UnderStatusMelding;
import no.nav.brregstub.tjenestekontrakter.rolleutskrift.NavnType;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_DATE;

@Component
public class RolleutskriftMapper {


    public Grunndata map(RolleutskriftTo to) {
        var grunndata = new Grunndata();
        var responseHeader = mapTilResponseHeader(to.getFnr());
        var melding = mapTilMelding(to);
        grunndata.setResponseHeader(responseHeader);
        grunndata.setMelding(melding);


        return grunndata;
    }

    private static ResponseHeader mapTilResponseHeader(String fødselsnummer) {
        var responseHeader = new ResponseHeader();
        responseHeader.setProssessDato(localDateToXmlGregorianCalendar(LocalDate.now()));
        responseHeader.setTjeneste("hentRolleutskrift");
        responseHeader.setFodselsnr(fødselsnummer);
        responseHeader.setHovedStatus(0);
        var underStatus = new UnderStatus();
        var underStatusMelding = new UnderStatusMelding();
        underStatusMelding.setKode(0);
        underStatusMelding.setValue("Data returnert");
        underStatus.getUnderStatusMelding().add(underStatusMelding);
        responseHeader.setUnderStatus(underStatus);
        return responseHeader;
    }

    private Melding mapTilMelding(RolleutskriftTo to) {
        var melding = new Melding();
        melding.setRolleInnehaver(mapTilRolleInnhaver(to));
        melding.setRoller(mapTilRoller(to));

        return melding;
    }

    private static Melding.Roller mapTilRoller(RolleutskriftTo to) {
        var roller = new Melding.Roller();
        if (to.getEnheter() != null) {
            int count = 1;
            for (RolleEnhetTo enhetTo : to.getEnheter()) {
                var enhet = new Enhet();
                enhet.setNr(count);
                count++;
                enhet.setNavn(mapTilNavntype(enhetTo.getNavn()));
                enhet.setOrgnr(mapTilOrganisasjonsNummer(enhetTo.getOrgNr()));
                enhet.setAdresse(mapTilAdresseEnhet(enhetTo));
                enhet.setRegistreringsDato(localDateToXmlGregorianCalendar(enhetTo.getRegistreringsdato()));
                var beskrivelse = new Enhet.RolleBeskrivelse();
                beskrivelse.setValue(enhetTo.getBeskrivelse());
                enhet.setRolleBeskrivelse(beskrivelse);
                roller.getEnhet().add(enhet);
            }
        }
        return roller;

    }

    public static Enhet.Orgnr mapTilOrganisasjonsNummer(Integer orgnrTo) {
        var orgnr = new Enhet.Orgnr();
        orgnr.setValue(orgnrTo);
        return orgnr;
    }

    public static NavnType mapTilNavntype(NavnTo to) {
        var navn = new NavnType();
        navn.setNavn1(to.getNavn1());
        navn.setNavn2(to.getNavn2());
        navn.setNavn3(to.getNavn3());
        return navn;
    }

    private static RolleInnehaver mapTilRolleInnhaver(RolleutskriftTo to) {
        var rolleInnehaver = new RolleInnehaver();
        rolleInnehaver.setNavn(mapTilNavntype(to.getNavn()));
        Fodselsdato fødselsdato = new Fodselsdato();
        fødselsdato.setValue(localDateToXmlGregorianCalendar(to.getFodselsdato()));
        rolleInnehaver.setFodselsdato(fødselsdato);
        rolleInnehaver.setAdresse(mapTilAdresse(to.getAdresse()));
        return rolleInnehaver;
    }


    private static AdresseType1 mapTilAdresse(AdresseTo to) {
        var adresse = new AdresseType1();
        adresse.setAdresse1(to.getAdresse1());
        adresse.setAdresse2(to.getAdresse2());
        adresse.setAdresse3(to.getAdresse3());
        adresse.setPostnr(to.getPostnr());
        adresse.setPoststed(to.getPoststed());
        var land = new AdresseType1.Land();
        land.setLandkode1(to.getLandKode());
        land.setValue(to.getLand());
        adresse.setLand(land);
        return adresse;
    }

    private static AdresseType2 mapTilAdresse2(AdresseTo to) {
        var adresse = new AdresseType2();
        adresse.setAdresse1(to.getAdresse1());
        adresse.setAdresse2(to.getAdresse2());
        adresse.setAdresse3(to.getAdresse3());
        adresse.setPostnr(to.getPostnr());
        adresse.setPoststed(to.getPoststed());
        var kommune = new AdresseType2.Kommune();
        kommune.setValue(to.getKommune());
        adresse.setKommune(kommune);
        var land = new AdresseType2.Land();
        land.setLandkode1(to.getLandKode());
        land.setValue(to.getLand());
        adresse.setLand(land);
        return adresse;
    }

    private static Enhet.Adresse mapTilAdresseEnhet(RolleEnhetTo to) {
        var adresse = new Enhet.Adresse();
        adresse.setForretningsAdresse(mapTilAdresse2(to.getForretningsAdresse()));
        adresse.setPostAdresse(mapTilAdresse2(to.getPostAdresse()));
        return adresse;
    }

    @SneakyThrows
    private static XMLGregorianCalendar localDateToXmlGregorianCalendar(LocalDate date) {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.format(ISO_DATE));
    }

}
