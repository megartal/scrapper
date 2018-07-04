package com.name.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author Akbar
 * @DATE 7/1/2018.
 */
@Getter
@Setter
@AllArgsConstructor
public class OTAData {
    private String OTAName;
    private String redirect;
    private Set<Room> rooms = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OTAData type = (OTAData) o;

        return getOTAName().equals(type.getOTAName());
    }

    @Override
    public int hashCode() {
        return getOTAName().hashCode();
    }
}
