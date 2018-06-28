package com.name.scrappers;

import com.name.documents.City;
import com.name.documents.Hotel;
import com.name.documents.Match;
import com.name.models.Name;
import com.name.models.OTAMatch;
import com.name.models.RoomMatch;
import com.name.repositories.CityRepository;
import com.name.repositories.HotelRepository;
import com.name.repositories.MatchRepository;
import com.name.repositories.RawMatchRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author Akbar
 * @DATE 5/20/2018.
 */
@Service
@Profile({"insert"})
public class InsertData implements Scrapper{
    private final HotelRepository hotelRepository;
    private final MatchRepository matchRepository;
    private final CityRepository cityRepository;
    private final RawMatchRepository rawMatchRepository;

    public InsertData(HotelRepository hotelRepository, MatchRepository matchRepository, CityRepository cityRepository, RawMatchRepository rawMatchRepository) {
        this.hotelRepository = hotelRepository;
        this.matchRepository = matchRepository;
        this.cityRepository = cityRepository;
        this.rawMatchRepository = rawMatchRepository;
    }


    @Override
    public void start() {
//        insertNewHotel();
//        insertMatch();
//        insertCity();
        matchRepository.deleteAll();
        hotelRepository.deleteAll();
    }

    private void insertCity() {
        List<City> cities = new ArrayList<>();
        cities.add(new City("تهران", "تهران"));
        cities.add(new City("تبریز", "آذربایجان شرقی"));
        cityRepository.saveAll(cities);
    }

    private void insertMatch() {
        Match match = new Match();
        match.setHotelName("هتل پارسیان استقلال تهران");
        Set<OTAMatch> otaMatches = new HashSet<>();
        OTAMatch snaptrip = new OTAMatch();
        snaptrip.setOTAname("snapptrip");
        Set<RoomMatch> roomMatches = new HashSet<>();
        roomMatches.add(new RoomMatch("اتاق سوئیت دو تخته رویال برج غربی", "سوئیت دو تخته رویال برج غربی", "type2"));
        roomMatches.add(new RoomMatch("اتاق دو تخته دابل برج غربی", "دو تخته دابل برج غربی", "type2"));
        roomMatches.add(new RoomMatch("اتاق دو تخته دابل برج شرقی", "دو تخته دابل برج شرقی", "type2"));
        roomMatches.add(new RoomMatch("اتاق یک تخته برج غربی", "یک تخته برج غربی", "type1"));
        roomMatches.add(new RoomMatch("اتاق دو تخته تویین برج غربی", "دو تخته تویین برج غربی", "type2"));
        roomMatches.add(new RoomMatch("اتاق سوئیت دو تخته برج شرقی", "سوئیت دو تخته برج شرقی", "type2"));
        roomMatches.add(new RoomMatch("اتاق یک تخته برج شرقی", "یک تخته برج شرقی", "type1"));
        roomMatches.add(new RoomMatch("اتاق سوئیت دو تخته رویال برج شرقی", "سوئیت دو تخته رویال برج شرقی", "type2"));
        roomMatches.add(new RoomMatch("اتاق دو تخته تویین برج شرقی", "دو تخته تویین برج شرقی", "type2"));
        roomMatches.add(new RoomMatch("اتاق سوئیت دو تخته برج غربی", "سوئیت دو تخته برج غربی", "type2"));
        snaptrip.setRooms(roomMatches);
        otaMatches.add(snaptrip);

        OTAMatch jabama = new OTAMatch();
        jabama.setOTAname("jabama");
        Set<RoomMatch> jabamaMaches = new HashSet<>();
        jabamaMaches.add(new RoomMatch("اتاق سوئیت دو تخته رویال برج غربی", "سوییت بزرگ ، برج غربی", "type2"));
        jabamaMaches.add(new RoomMatch("اتاق دو تخته دابل برج غربی", "", "type2"));
        jabamaMaches.add(new RoomMatch("اتاق یک تخته برج غربی", "اتاق یک تخته غربی", "type1"));
        jabamaMaches.add(new RoomMatch("اتاق سوئیت دو تخته برج غربی", "سوئیت کوچک ، برج غربی ", "type2"));
        jabamaMaches.add(new RoomMatch("اتاق دو تخته تویین برج غربی", "اتاق دو تخته غربی", "type2"));
        jabamaMaches.add(new RoomMatch("اتاق سوئیت دو تخته برج شرقی", "سوئیت کوچک ، برج شرقی", "type2"));
        jabamaMaches.add(new RoomMatch("اتاق دو تخته دابل برج شرقی", "دوتخته شرقی", "type2"));
        jabamaMaches.add(new RoomMatch("اتاق یک تخته برج شرقی", "اتاق یک تخته شرقی", "type1"));
        jabamaMaches.add(new RoomMatch("اتاق سوئیت دو تخته رویال برج شرقی", "سوییت بزرگ ، برج شرقی", "type2"));
        jabamaMaches.add(new RoomMatch("اتاق دو تخته تویین برج شرقی", "اتاق دو تخته شرقی", "type2"));
        jabama.setRooms(jabamaMaches);
        otaMatches.add(jabama);
        match.setOTAs(otaMatches);

        matchRepository.save(match);
    }

    private void insertNewHotel() {
        Hotel hotel = new Hotel();
        hotel.setName("هتل پارسیان استقلال تهران");
        hotel.setCity("تهران");
        Name name = new Name();
        name.setName("هتل پارسیان استقلال تهران");
        name.setOTA("snapptrip");
        Set<Name> names = new HashSet<>();
        names.add(name);

        Name name2 = new Name();
        name2.setName("tehran-esteghlal");
        name2.setOTA("jabama");
        names.add(name2);

        hotel.setNames(names);
        hotelRepository.save(hotel);
    }
}
