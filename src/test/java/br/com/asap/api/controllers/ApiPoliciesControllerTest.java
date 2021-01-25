package br.com.asap.api.controllers;

import br.com.asap.api.models.Client;
import br.com.asap.api.models.Policy;
import br.com.asap.api.services.entities_services.PoliciesService;
import br.com.asap.api.services.repositories.ClientsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiPoliciesControllerTest {

    private final MockMvc mockMvc;
    private final MongoTemplate mongoTemplate;
    private final ClientsRepository policiesRepository;
    private final PoliciesService policiesService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ApiPoliciesControllerTest(MockMvc mockMvc, MongoTemplate mongoTemplate,
                                     ClientsRepository policiesRepository,
                                     PoliciesService policiesService, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.mongoTemplate = mongoTemplate;
        this.policiesRepository = policiesRepository;
        this.policiesService = policiesService;
        this.objectMapper = objectMapper;
    }


    private String validParams(Client client) {
        return "{" +
            "\"effectiveDateStartAt\": \"2020-01-01T00:00:00\"," +
            "\"effectiveDateEndAt\": \"2020-01-31T23:59:59\"," +
            "\"vehiclePlate\": \"NEQ-1857\"," +
            "\"client\": {" +
                "\"id\": \"" + client.getId() + "\"" +
            "}" +
        "}";
    }

    private String newParams(Client client) {
        return "{" +
            "\"effectiveDateStartAt\": \"2020-02-01T00:00:00\"," +
            "\"effectiveDateEndAt\": \"2020-02-25T23:59:59\"," +
            "\"vehiclePlate\": \"ABC-1234\"," +
            "\"client\": {" +
                "\"id\": \"" + client.getId() + "\"" +
            "}" +
        "}";
    }

    private final String invalidParams = "{" +
        "\"effectiveDateStartAt\": \"\"," +
        "\"effectiveDateEndAt\": \"\"," +
        "\"vehiclePlate\": \"\"" +
    "}";

    private final String clientIdNullParams = "{" +
            "\"effectiveDateStartAt\": \"2020-01-01T00:00:00\"," +
            "\"effectiveDateEndAt\": \"2020-01-31T23:59:59\"," +
            "\"vehiclePlate\": \"NEQ-1857\"," +
            "\"client\": {" +
        "}" +
    "}";

    private Policy createPolicy(Client client) throws JsonProcessingException {
        final Integer number = policiesService.generateNumber();
        final Policy policy = objectMapper.readValue(validParams(client), Policy.class);
        policy.setNumber(number);
        return policiesService.create(policy);
    }

    private Client createClient() {
        final Client client = new Client()
            .setCpf("728.687.544-22")
            .setName("Manuel Vinicius da Mota")
            .setUf("CE")
            .setCity("Fortaleza");
        return policiesRepository.save(client);
    }

    private Client createOtherClient() {
        final Client client = new Client()
            .setCpf("831.685.877-02")
            .setName("Tânia Regina Viana")
            .setUf("MS")
            .setCity("Campo Grande");
        return policiesRepository.save(client);
    }

    @BeforeEach
    public void clearDb() {
        mongoTemplate.getDb().drop();
    }

    @Nested
    class list {

        private ResultActions performRequestWith() throws Exception {
            return mockMvc.perform(get("/api/policies"));
        }

        @Test
        public void shouldReturnHttpStatusOk() throws Exception {
            performRequestWith().andExpect(status().isOk());
        }

    }

    @Nested
    class show {

        private ResultActions performRequestWith(String id) throws Exception {
            return mockMvc.perform(get("/api/policies/" + id));
        }

        @Nested
        class existing {

            @Test
            public void shouldReturnHttpStatusOk() throws Exception {

                final Client client = createClient();
                final Policy policy = createPolicy(client);

                performRequestWith(policy.getId())
                    .andExpect(status().isOk());

            }

            @Test
            public void shouldReturnPolicy() throws Exception {

                final Client client = createClient();
                final Policy policy = createPolicy(client);

                performRequestWith(policy.getId())
                    .andExpect(jsonPath("$.id", is(notNullValue())))
                    .andExpect(jsonPath("$.effectiveDateStartAt", is("2020-01-01T00:00:00")))
                    .andExpect(jsonPath("$.effectiveDateEndAt", is("2020-01-31T23:59:59")))
                    .andExpect(jsonPath("$.vehiclePlate", is("NEQ-1857")))
                    .andExpect(jsonPath("$.number", is(notNullValue())));
            }

        }

        @Nested
        class notExisting {

            @Test
            public void shouldReturnHttpStatusNotFound() throws Exception {

                performRequestWith("123")
                    .andExpect(status().isNotFound());
            }

        }

    }

    @Nested
    class showByNumber {

        private ResultActions performRequestWith(Policy policy) throws Exception {
            return mockMvc.perform(get("/api/policies/number/" + policy.getNumber()));
        }

        @Nested
        class existing {

            private Policy createPolicy(Client client, Integer number) {
                final Policy policy = new Policy()
                    .setClient(client)
                    .setVehiclePlate("1234-abc")
                    .setEffectiveDateStartAt(LocalDateTime.now().minusDays(10))
                    .setEffectiveDateEndAt(LocalDateTime.now().minusDays(5))
                    .setNumber(number);
                return policiesService.create(policy);
            }

            @Nested
            class expired {

                @Test
                public void shouldReturnHttpStatusOk() throws Exception {

                    final Client client = createClient();
                    final Integer number = policiesService.generateNumber();
                    final Policy policy = createPolicy(client, number);

                    performRequestWith(policy)
                        .andExpect(status().isOk());
                }

                @Test
                public void shouldShowExpiredDays() throws Exception {

                    final Client client = createClient();
                    final Integer number = policiesService.generateNumber();
                    final Policy policy = createPolicy(client, number);

                    performRequestWith(policy)
                        .andExpect(jsonPath("$.id", is(notNullValue())))
                        .andExpect(jsonPath("$.effectiveDateStartAt", is(notNullValue())))
                        .andExpect(jsonPath("$.effectiveDateEndAt", is(notNullValue())))
                        .andExpect(jsonPath("$.vehiclePlate", is("1234-abc")))
                        .andExpect(jsonPath("$.expired", is(true)))
                        .andExpect(jsonPath("$.daysExpired", is(5)))
                        .andExpect(jsonPath("$.number", is(notNullValue())));
                }

            }

            @Nested
            class notExpired {

                private Policy createPolicy(Client client, Integer number) {
                    final Policy policy = new Policy()
                        .setClient(client)
                        .setVehiclePlate("1234-abc")
                        .setEffectiveDateStartAt(LocalDateTime.now().minusDays(10))
                        .setEffectiveDateEndAt(LocalDateTime.now().plusDays(6))
                        .setNumber(number);
                    return policiesService.create(policy);
                }


                @Test
                public void shouldReturnHttpStatusOk() throws Exception {

                    final Client client = createClient();
                    final Integer number = policiesService.generateNumber();
                    final Policy policy = createPolicy(client, number);

                    performRequestWith(policy)
                        .andExpect(status().isOk());
                }

                @Test
                public void shouldShowDaysToExpire() throws Exception {

                    final Client client = createClient();
                    final Integer number = policiesService.generateNumber();
                    final Policy policy = createPolicy(client, number);

                    performRequestWith(policy)
                        .andExpect(jsonPath("$.id", is(notNullValue())))
                        .andExpect(jsonPath("$.effectiveDateStartAt", is(notNullValue())))
                        .andExpect(jsonPath("$.effectiveDateEndAt", is(notNullValue())))
                        .andExpect(jsonPath("$.vehiclePlate", is("1234-abc")))
                        .andExpect(jsonPath("$.expired", is(false)))
                        .andExpect(jsonPath("$.daysToExpire", is(5)))
                        .andExpect(jsonPath("$.number", is(notNullValue())));
                }

            }

        }

        @Nested
        class notExisting {

            @Test
            public void shouldReturnHttpStatusNotFound() throws Exception {

                final Policy policy = new Policy()
                    .setNumber(123);
                policy.setId("123");
                performRequestWith(policy)
                    .andExpect(status().isNotFound());
            }
        }

    }

    @Nested
    class create {

        private ResultActions performRequestWith(String params) throws Exception {
            return mockMvc.perform(post("/api/policies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(params)
            );
        }

        @Nested
        class withValidParams {

            @Test
            public void shouldReturnHttpStatusCreated() throws Exception {
                final Client client = createClient();

                performRequestWith(validParams(client))
                    .andExpect(status().isCreated());
            }

            @Test
            public void shouldReturnPolicyCreated() throws Exception {
                final Client client = createClient();

                performRequestWith(validParams(client))
                    .andExpect(jsonPath("$.id", is(notNullValue())))
                    .andExpect(jsonPath("$.effectiveDateStartAt", is("2020-01-01T00:00:00")))
                    .andExpect(jsonPath("$.effectiveDateEndAt", is("2020-01-31T23:59:59")))
                    .andExpect(jsonPath("$.vehiclePlate", is("NEQ-1857")))
                    .andExpect(jsonPath("$.number", is(notNullValue())));

            }

            @Test
            public void shouldCreatePolicy() throws Exception {
                final Client client = createClient();

                performRequestWith(validParams(client));

                final List<Policy> all = policiesService.findAll();
                assertThat(all, hasSize(1));

                final Policy policy = all.get(0);
                assertThat(policy.getClient().getId(), is(client.getId()));
                assertThat(policy.getEffectiveDateStartAt().toString(), is("2020-01-01T00:00"));
                assertThat(policy.getEffectiveDateEndAt().toString(), is("2020-01-31T23:59:59"));
                assertThat(policy.getVehiclePlate(), is("NEQ-1857"));
                assertThat(policy.getNumber(), is(notNullValue()));
            }

        }

        @Nested
        class withInvalidParams {

            @Test
            public void shouldReturnHttpStatusUnprocessableEntity() throws Exception {
                performRequestWith(invalidParams)
                    .andExpect(status().isUnprocessableEntity());
            }

            @Test
            public void shouldReturnFormErrors() throws Exception {
                performRequestWith(invalidParams)
                    .andExpect(jsonPath("$", hasSize(4)))
                    .andExpect(jsonPath("$[*].defaultMessage", hasItem("Data inicial de vigência é obrigatória")))
                    .andExpect(jsonPath("$[*].defaultMessage", hasItem("Data final de vigência é obrigatória")))
                    .andExpect(jsonPath("$[*].defaultMessage", hasItem("Placa do veículo é oobrigatória")))
                    .andExpect(jsonPath("$[*].defaultMessage", hasItem("Cliente é obrigatório")));
            }

            @Test
            public void shouldNotCreatePolicy() throws Exception {
                performRequestWith(invalidParams);

                final List<Policy> all = policiesService.findAll();
                assertThat(all, hasSize(0));
            }

        }

        @Nested
        class withClientNotFound {

            @Test
            public void shouldReturnHttpStatusUnprocessableEntity() throws Exception {
                final Client client = new Client();
                client.setId("123234");

                performRequestWith(validParams(client))
                    .andExpect(status().isUnprocessableEntity());
            }

            @Test
            public void shouldReturnFormErrors() throws Exception {
                final Client client = new Client();
                client.setId("123234");

                performRequestWith(validParams(client))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[*].defaultMessage", hasItem("Cliente não encontrado")));
            }

            @Test
            public void shouldNotCreatePolicy() throws Exception {
                final Client client = new Client();
                client.setId("123234");

                performRequestWith(validParams(client));

                final List<Policy> all = policiesService.findAll();
                assertThat(all, hasSize(0));

            }
        }

        @Nested
        class withClientIdNull {

            @Test
            public void shouldReturnHttpStatusUnprocessableEntity() throws Exception {
                performRequestWith(clientIdNullParams)
                    .andExpect(status().isUnprocessableEntity());
            }

            @Test
            public void shouldReturnFormErrors() throws Exception {

                performRequestWith(clientIdNullParams)
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[*].defaultMessage", hasItem("Id do Cliente é obrigatório")));
            }

            @Test
            public void shouldNotCreatePolicy() throws Exception {


                performRequestWith(clientIdNullParams);

                final List<Policy> all = policiesService.findAll();
                assertThat(all, hasSize(0));

            }

        }

    }

    @Nested
    class update {

        private ResultActions performRequestWith(String id, String params) throws Exception {
            return mockMvc.perform(put("/api/policies/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(params)
            );
        }

        @Nested
        class existing {

            @Nested
            class withValidParams {

                @Test
                public void shouldReturnHttpStatusOk() throws Exception {

                    final Client client = createClient();
                    final Policy policy = createPolicy(client);

                    final Client otherClient = createOtherClient();

                    performRequestWith(policy.getId(), newParams(otherClient))
                        .andExpect(status().isOk());
                }

                @Test
                public void shouldReturnUpdatedPolicy() throws Exception {

                    final Client client = createClient();
                    final Policy policy = createPolicy(client);
                    final Client otherClient = createOtherClient();

                    performRequestWith(policy.getId(), newParams(otherClient))
                        .andExpect(jsonPath("$.id", is(policy.getId())))
                        .andExpect(jsonPath("$.effectiveDateStartAt", is("2020-02-01T00:00:00")))
                        .andExpect(jsonPath("$.effectiveDateEndAt", is("2020-02-25T23:59:59")))
                        .andExpect(jsonPath("$.vehiclePlate", is("ABC-1234")))
                        .andExpect(jsonPath("$.number", is(policy.getNumber())));

                }

                @Test
                public void shouldUpdatePolicy() throws Exception {

                    final Client client = createClient();
                    final Policy policy = createPolicy(client);
                    final Client otherClient = createOtherClient();

                    performRequestWith(policy.getId(), newParams(otherClient));

                    final List<Policy> all = policiesService.findAll();
                    final Policy policy1 = all.get(0);

                    assertThat(policy1.getClient().getId(), is(otherClient.getId()));
                    assertThat(policy1.getEffectiveDateStartAt().toString(), is("2020-02-01T00:00"));
                    assertThat(policy1.getEffectiveDateEndAt().toString(), is("2020-02-25T23:59:59"));
                    assertThat(policy1.getVehiclePlate(), is("ABC-1234"));
                    assertThat(policy1.getNumber(), is(notNullValue()));
                }

            }

            @Nested
            class withInvalidParams {

                @Test
                public void shouldReturnHttpStatusUnprocessableEntity() throws Exception {

                    final Client client = createClient();
                    final Policy policy = createPolicy(client);

                    performRequestWith(policy.getId(), invalidParams)
                        .andExpect(status().isUnprocessableEntity());
                }

                @Test
                public void shouldReturnFormErrors() throws Exception {

                    final Client client = createClient();
                    final Policy policy = createPolicy(client);

                    performRequestWith(policy.getId(), invalidParams)
                        .andExpect(jsonPath("$", hasSize(4)))
                        .andExpect(jsonPath("$[*].defaultMessage", hasItem("Data inicial de vigência é obrigatória")))
                        .andExpect(jsonPath("$[*].defaultMessage", hasItem("Data final de vigência é obrigatória")))
                        .andExpect(jsonPath("$[*].defaultMessage", hasItem("Placa do veículo é oobrigatória")))
                        .andExpect(jsonPath("$[*].defaultMessage", hasItem("Cliente é obrigatório")));
                }

                @Test
                public void shouldNotUpdatePolicy() throws Exception {

                    final Client client = createClient();
                    final Policy policy = createPolicy(client);

                    performRequestWith(policy.getId(), invalidParams);

                    final List<Policy> all = policiesService.findAll();
                    final Policy policy1 = all.get(0);

                    assertThat(policy1.getClient().getId(), is(client.getId()));
                    assertThat(policy1.getEffectiveDateStartAt().toString(), is("2020-01-01T00:00"));
                    assertThat(policy1.getEffectiveDateEndAt().toString(), is("2020-01-31T23:59:59"));
                    assertThat(policy1.getVehiclePlate(), is("NEQ-1857"));
                    assertThat(policy1.getNumber(), is(notNullValue()));
                }

            }

            @Nested
            class withClientNotFound {

                @Test
                public void shouldReturnHttpStatusUnprocessableEntity() throws Exception {

                    final Client client = createClient();
                    final Policy policy = createPolicy(client);

                    final Client notExistingClient = new Client();
                    notExistingClient.setId("123234");

                    final String params = newParams(notExistingClient);
                    performRequestWith(policy.getId(), params)
                        .andExpect(status().isUnprocessableEntity());
                }

                @Test
                public void shouldReturnFormErrors() throws Exception {

                    final Client client = createClient();
                    final Policy policy = createPolicy(client);

                    final Client notExistingClient = new Client();
                    notExistingClient.setId("123234");

                    final String params = newParams(notExistingClient);
                    performRequestWith(policy.getId(), params)
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[*].defaultMessage", hasItem("Cliente não encontrado")));
                }

                @Test
                public void shouldNotUpdatePolicy() throws Exception {

                    final Client client = createClient();
                    final Policy policy = createPolicy(client);

                    final Client notExistingClient = new Client();
                    notExistingClient.setId("123234");

                    final String params = newParams(notExistingClient);
                    performRequestWith(policy.getId(), params);

                    final List<Policy> all = policiesService.findAll();
                    assertThat(all, hasSize(1));

                    final Policy policy1 = all.get(0);
                    assertThat(policy1.getClient().getId(), is(client.getId()));
                    assertThat(policy1.getEffectiveDateStartAt().toString(), is("2020-01-01T00:00"));
                    assertThat(policy1.getEffectiveDateEndAt().toString(), is("2020-01-31T23:59:59"));
                    assertThat(policy1.getVehiclePlate(), is("NEQ-1857"));
                    assertThat(policy1.getNumber(), is(notNullValue()));

                }
            }

            @Nested
            class withClientIdNull {

                @Test
                public void shouldReturnHttpStatusUnprocessableEntity() throws Exception {

                    final Client client = createClient();
                    final Policy policy = createPolicy(client);

                    performRequestWith(policy.getId(), clientIdNullParams)
                        .andExpect(status().isUnprocessableEntity());
                }

                @Test
                public void shouldReturnFormErrors() throws Exception {

                    final Client client = createClient();
                    final Policy policy = createPolicy(client);

                    performRequestWith(policy.getId(), clientIdNullParams)
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[*].defaultMessage", hasItem("Id do Cliente é obrigatório")));
                }

                @Test
                public void shouldNotUpdatePolicy() throws Exception {

                    final Client client = createClient();
                    final Policy policy = createPolicy(client);

                    performRequestWith(policy.getId(), clientIdNullParams);

                    final List<Policy> all = policiesService.findAll();
                    assertThat(all, hasSize(1));

                    final Policy policy1 = all.get(0);
                    assertThat(policy1.getClient().getId(), is(client.getId()));
                    assertThat(policy1.getEffectiveDateStartAt().toString(), is("2020-01-01T00:00"));
                    assertThat(policy1.getEffectiveDateEndAt().toString(), is("2020-01-31T23:59:59"));
                    assertThat(policy1.getVehiclePlate(), is("NEQ-1857"));
                    assertThat(policy1.getNumber(), is(notNullValue()));
                }

            }

        }

        @Nested
        class notExisting {

            @Test
            public void shouldReturnHttpStatusNotFound() throws Exception {

                final Client client = createClient();
                final Policy policy = createPolicy(client);

                performRequestWith("123", validParams(client))
                    .andExpect(status().isNotFound());
            }

            @Test
            public void shouldNotUpdatePolicy() throws Exception {

                final Client client = createClient();
                final Policy policy = createPolicy(client);

                performRequestWith("123", validParams(client))
                    .andExpect(status().isNotFound());

                final List<Policy> all = policiesService.findAll();
                final Policy policy1 = all.get(0);

                assertThat(policy1.getClient().getId(), is(client.getId()));
                assertThat(policy1.getEffectiveDateStartAt().toString(), is("2020-01-01T00:00"));
                assertThat(policy1.getEffectiveDateEndAt().toString(), is("2020-01-31T23:59:59"));
                assertThat(policy1.getVehiclePlate(), is("NEQ-1857"));
                assertThat(policy1.getNumber(), is(notNullValue()));
            }

        }

    }


    @Nested
    class delete {

        private ResultActions performRequestWith(String id) throws Exception {
            return mockMvc.perform(delete("/api/policies/" + id));
        }

        @Nested
        class existing {

            @Test
            public void shouldReturnHttpStatusOk() throws Exception {
                final Client client = createClient();
                final Policy policy = createPolicy(client);

                performRequestWith(policy.getId())
                    .andExpect(status().isOk());
            }

            @Test
            public void shouldDelete() throws Exception {
                final Client client = createClient();
                performRequestWith(client.getId());

                final List<Policy> all = policiesService.findAll();
                assertThat(all, hasSize(0));

            }

        }

        @Nested
        class notExisting {

            @Test
            public void shouldReturnHttpStatusNotFound() throws Exception {
                final Client client = createClient();
                final Policy policy = createPolicy(client);
                performRequestWith("123")
                    .andExpect(status().isNotFound());
            }

            @Test
            public void shouldNotDelete() throws Exception {
                final Client client = createClient();
                final Policy policy = createPolicy(client);

                performRequestWith("123");

                final List<Policy> all = policiesService.findAll();
                assertThat(all, hasSize(1));

            }

        }

    }
}