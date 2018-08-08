package com.name.repositories;

import com.name.documents.Proxy;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Author Akbar
 * @DATE 8/8/2018.
 */
public interface ProxyRepository extends MongoRepository<Proxy, String> {
    List<Proxy> findByProtocol(String https);

    List<Proxy> findByProtocolAndStatus(String https, boolean status);
}
