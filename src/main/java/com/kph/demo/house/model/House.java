package com.kph.demo.house.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document("house")
public final class House {

    private @Id String id;

    private String address;
    private String city;
    private String state;
    private String zip;
    private int bed;
    private int bath; // div by 10, e.g. 15=1.5, 50=5
    private int sqft;
    private int price;
    private LocalDateTime creDateTime;
    private LocalDateTime openHouse;
    private String descUrl;
    private String status; // Active, Sold, Pending, Inactive, Unknown
}
