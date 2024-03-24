package com.kph.demo.house.controller;

import com.kph.demo.house.model.House;
import com.kph.demo.house.repository.HouseMongoRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@Slf4j
public class HouseController {

    private static final int DELAY_PER_ITEM_MS = 100;

    private final HouseMongoRepository houseMongoRepository;

    public HouseController(final HouseMongoRepository houseMongoRepository) {
        this.houseMongoRepository = houseMongoRepository;
    }

    @PostMapping(value = { "/save", "/"})
    @ResponseStatus(code = HttpStatus.CREATED)
    //@ResponseBody --> if use @RestController, included as default! So, no need it here
    public Mono<String> save(@RequestBody House house) {
        log.info("Saving a house object to db: " + house.getAddress());
        house.setCreDateTime(LocalDateTime.now(ZoneId.of("America/New_York")));
        return houseMongoRepository.save(house).map(h -> "saved: " + h.getAddress());
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