package com.name.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author Akbar
 * @DATE 4/28/2018.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScrapInfo {
    private String OTAName;
    private Date crawlDate;
    private String hotelName;
    private boolean isReady;
    private Set<RoomType> roomTypes = new HashSet<>();

    public ScrapInfo(String OTAName, String hotelName) {
        this.OTAName = OTAName;
        this.hotelName = hotelName;
    }

    public ScrapInfo(String OTAName, String hotelName, Set<RoomType> roomTypes, boolean isReady) {
        this.OTAName = OTAName;
        this.hotelName = hotelName;
        this.roomTypes = roomTypes;
        this.isReady = isReady;
    }

    public ScrapInfo(String otaName, String hotelName, boolean isReady) {
        this.OTAName = otaName;
        this.hotelName = hotelName;
        this.isReady = isReady;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScrapInfo type = (ScrapInfo) o;

        return getOTAName().equals(type.getOTAName());
    }

    @Override
    public int hashCode() {
        return getOTAName().hashCode();
    }
}
