package com.name.repositories.hotel;

import com.name.documents.Hotel;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;

import static com.name.documents.Hotel.CITY;


/**
 * @Author Akbar
 * @DATE 7/1/2018.
 */
public class HotelRepositoryCustomImpl implements HotelRepositoryCustom {
    private final MongoTemplate template;

    public HotelRepositoryCustomImpl(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public List<Hotel> findAllByCities(List<String> cities) {
        Query query = new Query();
        query.addCriteria(Criteria.where(CITY).in(cities));
        return template.find(query, Hotel.class);
    }

    @Override
    public List<Hotel> findLastUpdated(int limit, String otaName) {
        Date date = new Date();
        date = DateUtils.addHours(date, -15);
        Query query = new Query();
//        query.limit(limit);
//        query.addCriteria(Criteria.where("scrapInfo").elemMatch(Criteria.where("OTAName").is(otaName).
//                andOperator(Criteria.where("scrapInfo").elemMatch(Criteria.where("crawlDate").lte(date)))));
        query.addCriteria(Criteria.where("scrapInfo").elemMatch(Criteria.where("crawlDate").lte(date)));
//        query.with(new Sort(Sort.Direction.DESC, "scrapInfo.crawlDate"));
        List<Hotel> hotels = template.find(query, Hotel.class);
//        hotels.stream().forEach(x -> System.out.println(x.getName()));
        return hotels;
    }
}
