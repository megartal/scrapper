package com.name.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Akbar
 * @DATE 4/28/2018.
 */
@Getter
@Setter
@EqualsAndHashCode
public class Price {
    private String date;
    private String value;
    private boolean available;
}
