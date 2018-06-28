package com.name.documents;

import com.name.models.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document
@Getter
@Setter
public class Hotel {
    private String id = UUID.randomUUID().toString();
    private String name;
    private Set<Name> names = new HashSet<>();
    private String district;
    private String city;
    private String mainImage;
    private List<Image> images = new ArrayList<>();
    private String address;
    private Integer stars;
    private String description;
    private Set<Amenity> amenities = new HashSet<>();
    private Location location = new Location("", "");
    private String mealPlan;
    private String cancelPolicy;
    private String accomType;
    private Set<EachRoom> eachRooms = new HashSet<>();
    private Set<Type> type1 = new HashSet<>();
    private Set<Type> type2 = new HashSet<>();
    private Set<Type> type3 = new HashSet<>();
    private Set<Type> type4 = new HashSet<>();
    private Set<Type> type5 = new HashSet<>();
    private boolean crawl;
    private boolean crawled;

    public Set<Type> callTypeMethod(String type){
        switch (type){
            case "type1":
                return type1;
            case "type2":
                return type2;
            case "type3":
                return type3;
            case "type4":
                return type4;
            case "type5":
                return type5;
        }
        return null;
    }

}
