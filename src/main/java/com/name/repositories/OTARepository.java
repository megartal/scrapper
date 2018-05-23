package com.name.repositories;


import com.name.documents.OTAEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * @Author Akbar
 * @DATE 5/3/2018.
 */
public interface OTARepository extends MongoRepository<OTAEntity, String> {
    Optional<OTAEntity> findByName(String name);

    Optional<OTAEntity> deleteByName(String name);
}
