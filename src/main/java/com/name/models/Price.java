package com.name.models;

import lombok.*;

import java.util.Date;

/**
 * @Author Akbar
 * @DATE 4/28/2018.
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Price {
    private Date date;
    private int value;
    private boolean available;
}
