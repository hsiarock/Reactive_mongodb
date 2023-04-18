package com.kph.demo.house.controller;

import com.kph.demo.house.model.House;
import com.kph.demo.house.repository.HouseMongoRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@Slf4j
public class HouseController {

    private static final int DELAY_PER_ITEM_MS = 100;

    private final HouseMongoRepository houseMongoRepository;

    public HouseController(final HouseMongoRepository houseMongoRepository) {
        this.houseMongoRepository = houseMongoRepository;
    }

    /*
     * e.g. http://localhost:8080/house
     */
    @GetMapping("/house")
    public Flux<House> getHouseFlux() {
        log.info("getHouseFlus(): ");
        return houseMongoRepository.findAll().delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
    }

    /*
     * e.g. http://localhost:8080/house-paged?page=1&size=12
     */
    @GetMapping("/house-paged")
    public Flux<House> getHouseFlux(final @RequestParam(name = "page") int page,
                                    final @RequestParam(name = "size") int size) {
        log.info("getHouseFlux with paged:");
        return houseMongoRepository.findAllByIdNotNullOrderByIdAsc(PageRequest.of(page, size))
                .delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
    }

}