package com.name.repositories.hotel;

import com.name.documents.Hotel;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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
}
