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
public class Type {
    private String name;
    Set<OTAData> OTAs = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Type type = (Type) o;

        return getName().equals(type.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
