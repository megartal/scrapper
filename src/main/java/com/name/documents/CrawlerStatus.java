package com.name.documents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

/**
 * @Author Akbar
 * @DATE 8/23/2018.
 */
@Document(collection = "crawler")
@Getter
@Setter
public class CrawlerStatus {
    private String id = UUID.randomUUID().toString();
    private String hotel;
    private String nameOTA;
    private String status;
    private String exception;
    private Date date;
}
