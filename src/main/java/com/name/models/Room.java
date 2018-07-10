package com.name.models;

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
public class Room {
    private String roomName;
    private int roomType;
    private String roomId;
    private String meta;
    private Set<Price> prices = new HashSet<>();
}
