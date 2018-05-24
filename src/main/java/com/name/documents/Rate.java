package com.name.documents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

/**
 * @Author Akbar
 * @DATE 5/24/2018.
 */
@Document
@Getter
@Setter
public class Rate {
    private String id = UUID.randomUUID().toString();
    private Date date;
    private String OTA;
    private String hotel;
    private String room;
    private boolean changed;

    public Rate(Date date, String OTA, String hotel, String room, boolean changed) {
        this.date = date;
        this.OTA = OTA;
        this.hotel = hotel;
        this.room = room;
        this.changed = changed;
    }
}
