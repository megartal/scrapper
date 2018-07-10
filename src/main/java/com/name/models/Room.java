package com.name.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class Room {
    private String roomName;
    private int roomType;
    //    private String roomId;
//    private String meta;
    private Set<Price> prices = new HashSet<>();
}
