package com.name.repositories.hotel;

import com.name.documents.Hotel;

import java.util.List;

/**
 * @Author Akbar
 * @DATE 7/1/2018.
 */
public interface HotelRepositoryCustom {
    List<Hotel> findAllByCities(List<String> cities);

    List<Hotel> findLastUpdated(int limit, String otaName);
}
