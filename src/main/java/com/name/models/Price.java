package com.name.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Author Akbar
 * @DATE 4/28/2018.
 */
@Getter
@Setter
@EqualsAndHashCode
public class Price {
    private Date date;
    private int value;
    private boolean available;
}
