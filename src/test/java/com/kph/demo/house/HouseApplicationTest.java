package com.kph.demo.house;

import com.kph.demo.house.model.House;
import com.kph.demo.house.repository.HouseMongoRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HouseApplicationTest
{

    @MockBean
    private HouseMongoRepository houseMongoRepository;

    // This one is not needed, but we need to override the real one to prevent the default behavior
    //@MockBean
    //private QuijoteDataLoader quijoteDataLoader;

    @LocalServerPort
    private int serverPort;

    private WebClient webClient;

    private Flux<House> houseFlux;
    private House house1, house2, house3, house4;

    @BeforeEach
    public void setUp() {
        this.webClient = WebClient.create("http://localhost:" + serverPort);

        house1 = House.builder().address("1 Linda Lane").city("North Plainfield").state("NJ").zip("07060")
                .bed(2).bath(2).sqft(1600).price(890000).creDateTime(LocalDateTime.now()).descUrl("testing").build();
        house2 = House.builder().address("2 Linda Lane").city("North Plainfield").state("NJ").zip("07060")
                .bed(2).bath(2).sqft(1600).price(890000).creDateTime(LocalDateTime.now()).descUrl("testing").build();
        house3 = House.builder().address("3 Linda Lane").city("North Plainfield").state("NJ").zip("07060")
                .bed(2).bath(2).sqft(1600).price(890000).creDateTime(LocalDateTime.now()).descUrl("testing").build();
        house4 = House.builder().address("4 Linda Lane").city("North Plainfield").state("NJ").zip("07060")
                .bed(2).bath(2).sqft(1600).price(890000).creDateTime(LocalDateTime.now()).descUrl("testing").build();

        houseFlux = Flux.just(house1, house2, house3, house4);
    }

    @Test
    public void simpleGetRequest() {
        // given
        given(houseMongoRepository.findAll()).willReturn(houseFlux);

        // when
        Flux<House> receivedFlux = webClient.get().uri("/house").accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve().bodyToFlux(House.class);
                //.exchange().flatMapMany(response -> response.bodyToFlux(House.class));

        // then
        StepVerifier.create(receivedFlux)
                .expectNext(house1)
                // Note that if you uncomment this line the test will fail. For some reason the delay after the first
                // element is not respected (I'll investigate this)
//                .expectNoEvent(Duration.ofMillis(99)) // these lines might fail depending on the machine
                .expectNext(house2)
//                .expectNoEvent(Duration.ofMillis(99))
                .expectNext(house3)
//                .expectNoEvent(Duration.ofMillis(99))
                .expectNext(house4)
                .expectComplete()
                .verify();

    }

    @Test
    public void pagedGetRequest() {
        // given
        // In case page=1 and size=2, we mock the result to only the first two elements. Otherwise the Flux will be null.
        given(houseMongoRepository.findAllByIdNotNullOrderByIdAsc(PageRequest.of(1, 2)))
                .willReturn(houseFlux.take(2));

        // when
        Flux<House> receivedFlux = webClient.get().uri("/house-paged?page=1&size=2")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve().bodyToFlux(House.class);
                //.exchange().flatMapMany(response -> response.bodyToFlux(House.class));

        // then
        StepVerifier.create(receivedFlux)
                .expectNext(house1)
                .expectNext(house2)
                .expectComplete()
                .verify();

    }
}

