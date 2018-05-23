package com.name.documents;

import com.name.models.OTAMatch;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Akbar
 * @since 4/23/2018
 */
@Document
@Getter
@Setter
public class RawMatch {
    private String id = UUID.randomUUID().toString();
    private String hotelName;
    private Set<OTAMatch> OTAs = new HashSet<>();
}
