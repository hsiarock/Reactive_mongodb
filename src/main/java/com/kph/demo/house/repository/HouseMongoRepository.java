package com.kph.demo.house.repository;

import com.kph.demo.house.model.House;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface HouseMongoRepository extends ReactiveSortingRepository<House, String> {

    Flux<House> findAllByIdNotNullOrderByIdAsc(final Pageable page);

    @DeleteQuery
    void deleteByAddress(String address);

    @DeleteQuery
    void deleteById(Long id);

}
