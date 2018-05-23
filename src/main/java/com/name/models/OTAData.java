package com.name.models;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author Akbar
 * @DATE 4/28/2018.
 */
@Getter
@Setter
public class OTAData {
    private String name;
    private String redirect;
    private Set<Price> prices = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OTAData otaData = (OTAData) o;

        return getName().equals(otaData.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
