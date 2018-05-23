package com.name.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author Akbar
 * @DATE 4/28/2018.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomMatch {
    private String Droom;
    private String name;
    private String type;

    public RoomMatch(String name) {
        this.name = name;
    }
}
