package com.name.documents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * @author Akbar
 * @since 4/23/2018
 */
@Document(collection = "city")
@Getter
@Setter
public class City{
    private String id = UUID.randomUUID().toString();
    private String city;
    private String district;
    private boolean crawl;

    public City(String city, String district) {
        this.city = city;
        this.district = district;
    }
}
