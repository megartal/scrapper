package com.name.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * @Author Akbar
 * @DATE 8/8/2018.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Proxy {
    private String id = UUID.randomUUID().toString();
    private String ip;
    private String port;
    private String protocol;
}
