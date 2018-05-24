package com.name.services;

import com.name.documents.Rate;
import com.name.repositories.RateRepository;
import org.springframework.stereotype.Service;

/**
 * @Author Akbar
 * @DATE 5/24/2018.
 */
@Service
public class RateService {
    private final RateRepository rateRepository;

    public RateService(RateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    public void add(Rate rate) {
        rateRepository.save(rate);
    }
}
