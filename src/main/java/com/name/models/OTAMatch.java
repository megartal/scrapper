package com.name.models;


import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Akbar
 * @since 4/23/2018
 */
@Getter
@Setter
public class OTAMatch {
    private String OTAname;
    private Set<RoomMatch> rooms = new HashSet<>();
}
