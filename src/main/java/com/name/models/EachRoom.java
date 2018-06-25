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
public class EachRoom {
    private String name;
    private Integer type;

    public EachRoom(String name) {
        this.name = name;
    }
}
