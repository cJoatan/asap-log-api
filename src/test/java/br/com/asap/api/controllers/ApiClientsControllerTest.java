package br.com.asap.api.controllers;

import br.com.asap.api.models.Client;
import br.com.asap.api.models.Policy;
import br.com.asap.api.services.entities_services.ClientsService;
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

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiClientsControllerTest {

    private final MockMvc mockMvc;
    private final ClientsService clientsService;
    private final ObjectMapper objectMapper;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public ApiClientsControllerTest(MockMvc mockMvc, ClientsService clientsService,
                                    ObjectMapper objectMapper, MongoTemplate mongoTemplate) {
        this.mockMvc = mockMvc;
        this.clientsService = clientsService;
        this.objectMapper = objectMapper;
        this.mongoTemplate = mongoTemplate;
    }

    private final String validParams = "{" +
        "\"name\": \"Manuel Vinicius da Mota\"," +
        "\"cpf\": \"728.687.544-22\"," +
        "\"city\": \"Uruguaiana\"," +
        "\"uf\": \"RS\"" +
    "}";

    private final String invalidParams = "{" +
        "\"name\": \"\"," +
        "\"cpf\": \"\"," +
        "\"city\": \"\"," +
        "\"uf\": \"\"" +
    "}";

    private final String newParams = "{" +
        "\"name\": \"Roberto Otávio Filipe da Silva\"," +
        "\"cpf\": \"629.840.675-17\"," +
        "\"city\": \"Arapiraca\"," +
        "\"uf\": \"AL\"" +
    "}";

    @BeforeEach
    public void clearDb() {
        mongoTemplate.getDb().drop();
    }

    public Client createClient() throws JsonProcessingException {
        final Client client = objectMapper.readValue(validParams, Client.class);
        return clientsService.create(client);
    }


    public Client createOtherClient() throws JsonProcessingException {
        final Client client = objectMapper.readValue(newParams, Client.class);
        return clientsService.create(client);
    }

    @Nested
    class list {

        private ResultActions performRequestWith() throws Exception {
            return mockMvc.perform(get("/api/clients"));
        }

        @Test
        public void shouldReturnHttpStatusOk() throws Exception {
            performRequestWith().andExpect(status().isOk());
        }

    }

    @Nested
    class show {

        private ResultActions performRequestWith(String id) throws Exception {
            return mockMvc.perform(get("/api/clients/" + id));
        }

        @Nested
        class existing {

            @Test
            public void shouldReturnHttpStatusOk() throws Exception {

                final Client client = createClient();

                performRequestWith(client.getId())
                    .andExpect(status().isOk());

            }

            @Test
            public void shouldReturnPolicy() throws Exception {

                final Client client = createClient();

                performRequestWith(client.getId())
                    .andExpect(jsonPath("$.id", is(notNullValue())))
                    .andExpect(jsonPath("$.name", is("Manuel Vinicius da Mota")))
                    .andExpect(jsonPath("$.city", is("Uruguaiana")))
                    .andExpect(jsonPath("$.cpf", is("72868754422")))
                    .andExpect(jsonPath("$.uf", is("RS")));
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
    class create {

        private ResultActions performRequestWith(String params) throws Exception {
            return mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(params)
            );
        }

        @Nested
        class withValidParams {

            @Test
            public void shouldReturnHttpStatusCreated() throws Exception {
                performRequestWith(validParams)
                    .andExpect(status().isCreated());
            }

            @Test
            public void shouldReturnClientCreated() throws Exception {
                performRequestWith(validParams)
                    .andExpect(jsonPath("$.id", is(notNullValue())))
                    .andExpect(jsonPath("$.name", is("Manuel Vinicius da Mota")))
                    .andExpect(jsonPath("$.city", is("Uruguaiana")))
                    .andExpect(jsonPath("$.cpf", is("72868754422")))
                    .andExpect(jsonPath("$.uf", is("RS")));

            }

            @Test
            public void shouldCreateClient() throws Exception {
                performRequestWith(validParams);

                final List<Client> all = clientsService.findAll();
                assertThat(all, hasSize(1));

                final Client client = all.get(0);
                assertThat(client.getName(), is("Manuel Vinicius da Mota"));
                assertThat(client.getCity(), is("Uruguaiana"));
                assertThat(client.getCpf(), is("72868754422"));
                assertThat(client.getUf(), is("RS"));
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
                    .andExpect(jsonPath("$", hasSize(5)))
                    .andExpect(jsonPath("$[*].defaultMessage", hasItem("Nome é obrigatório")))
                    .andExpect(jsonPath("$[*].defaultMessage", hasItem("CPF é obrigatório")))
                    .andExpect(jsonPath("$[*].defaultMessage", hasItem("CPF inválido")))
                    .andExpect(jsonPath("$[*].defaultMessage", hasItem("Cidade é obrigatória")))
                    .andExpect(jsonPath("$[*].defaultMessage", hasItem("Estado é obrigatório")));
            }

            @Test
            public void shouldNotCreateClient() throws Exception {
                performRequestWith(invalidParams);

                final List<Client> all = clientsService.findAll();
                assertThat(all, hasSize(0));
            }

        }

        @Nested
        class withCPFRepeated {

            @Test
            public void shouldReturnHttpStatusUnprocessableEntity() throws Exception {
                final Client newClient = objectMapper.readValue(validParams, Client.class);
                clientsService.create(newClient);

                performRequestWith(validParams)
                    .andExpect(status().isUnprocessableEntity());
            }

            @Test
            public void shouldReturnFormErrors() throws Exception {
                final Client newClient = objectMapper.readValue(validParams, Client.class);
                clientsService.create(newClient);

                performRequestWith(validParams)
                    .andExpect(status().isUnprocessableEntity());
            }

            @Test
            public void shouldNotCreateClient() throws Exception {
                final Client newClient = objectMapper.readValue(validParams, Client.class);
                clientsService.create(newClient);

                performRequestWith(validParams);

                final List<Client> all = clientsService.findAll();
                assertThat(all, hasSize(1));

            }
        }

    }
    
    @Nested
    class update {

        private ResultActions performRequestWith(String id, String params) throws Exception {
            return mockMvc.perform(put("/api/clients/" + id)
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
                    performRequestWith(client.getId(), newParams)
                        .andExpect(status().isOk());
                }

                @Test
                public void shouldReturnUpdatedClient() throws Exception {
                    final Client client = createClient();
                    
                    performRequestWith(client.getId(), newParams)
                        .andExpect(jsonPath("$.id", is(client.getId())))
                        .andExpect(jsonPath("$.name", is("Roberto Otávio Filipe da Silva")))
                        .andExpect(jsonPath("$.city", is("Arapiraca")))
                        .andExpect(jsonPath("$.cpf", is("62984067517")))
                        .andExpect(jsonPath("$.uf", is("AL")));
                }

                @Test
                public void shouldUpdateClient() throws Exception {

                    final Client client = createClient();

                    performRequestWith(client.getId(), newParams);

                    final List<Client> all = clientsService.findAll();
                    assertThat(all, hasSize(1));

                    final Client client1 = all.get(0);
                    assertThat(client1.getId(), is(client.getId()));
                    assertThat(client1.getName(), is("Roberto Otávio Filipe da Silva"));
                    assertThat(client1.getCity(), is("Arapiraca"));
                    assertThat(client1.getCpf(), is("62984067517"));
                    assertThat(client1.getUf(), is("AL"));

                }

            }

            @Nested
            class withInvalidParams {

                @Test
                public void shouldReturnHttpStatusUnprocessableEntity() throws Exception {

                    final Client client = createClient();

                    performRequestWith(client.getId(), invalidParams)
                        .andExpect(status().isUnprocessableEntity());
                }

                @Test
                public void shouldReturnFormErrors() throws Exception {

                    final Client client = createClient();

                    performRequestWith(client.getId(), invalidParams)
                        .andExpect(jsonPath("$", hasSize(5)))
                        .andExpect(jsonPath("$[*].defaultMessage", hasItem("Nome é obrigatório")))
                        .andExpect(jsonPath("$[*].defaultMessage", hasItem("CPF é obrigatório")))
                        .andExpect(jsonPath("$[*].defaultMessage", hasItem("CPF inválido")))
                        .andExpect(jsonPath("$[*].defaultMessage", hasItem("Cidade é obrigatória")))
                        .andExpect(jsonPath("$[*].defaultMessage", hasItem("Estado é obrigatório")));
                }

                @Test
                public void shouldNotUpdateClient() throws Exception {

                    final Client client = createClient();

                    performRequestWith(client.getId(), invalidParams);

                    final List<Client> all = clientsService.findAll();
                    assertThat(all, hasSize(1));

                    final Client client1 = all.get(0);
                    assertThat(client1.getId(), is(client.getId()));
                    assertThat(client1.getName(), is("Manuel Vinicius da Mota"));
                    assertThat(client1.getCity(), is("Uruguaiana"));
                    assertThat(client1.getCpf(), is("72868754422"));
                    assertThat(client1.getUf(), is("RS"));
                }

            }

            @Nested
            class withCPFRepeated {

                @Test
                public void shouldReturnHttpStatusUnprocessableEntity() throws Exception {
                    final Client client = createClient();
                    final Client otherClient = createOtherClient();

                    performRequestWith(client.getId(), newParams)
                        .andExpect(status().isUnprocessableEntity());
                }

                @Test
                public void shouldReturnFormErrors() throws Exception {
                    final Client client = createClient();
                    final Client otherClient = createOtherClient();

                    performRequestWith(client.getId(), newParams)
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[*].defaultMessage", hasItem("Cpf já é usado por outro usuário")));
                }

                @Test
                public void shouldNotUpdateClient() throws Exception {
                    final Client client = createClient();
                    final Client otherClient = createOtherClient();

                    performRequestWith(client.getId(), newParams);

                    final List<Client> all = clientsService.findAll();
                    assertThat(all, hasSize(2));

                    final Client client1 = all.get(0);
                    assertThat(client1.getName(), is("Manuel Vinicius da Mota"));
                    assertThat(client1.getCity(), is("Uruguaiana"));
                    assertThat(client1.getCpf(), is("72868754422"));
                    assertThat(client1.getUf(), is("RS"));

                }
            }

        }

        @Nested
        class notExisting {

            @Test
            public void shouldReturnHttpStatusNotFound() throws Exception {

                final Client client = createClient();

                performRequestWith("123", validParams)
                    .andExpect(status().isNotFound());
            }

            @Test
            public void shouldNotUpdateClient() throws Exception {

                final Client client = createClient();

                performRequestWith("123", validParams)
                    .andExpect(status().isNotFound());

                final List<Client> all = clientsService.findAll();
                final Client client1 = all.get(0);

                assertThat(client1.getId(), is(client.getId()));
                assertThat(client1.getName(), is("Manuel Vinicius da Mota"));
                assertThat(client1.getCity(), is("Uruguaiana"));
                assertThat(client1.getCpf(), is("72868754422"));
                assertThat(client1.getUf(), is("RS"));
            }

        }

    }

    @Nested
    class delete {

        private ResultActions performRequestWith(String id) throws Exception {
            return mockMvc.perform(delete("/api/clients/" + id));
        }

        @Nested
        class existing {

            @Test
            public void shouldReturnHttpStatusOk() throws Exception {
                final Client client = createClient();
                performRequestWith(client.getId())
                    .andExpect(status().isOk());
            }

            @Test
            public void shouldDelete() throws Exception {
                final Client client = createClient();
                performRequestWith(client.getId());

                final List<Client> all = clientsService.findAll();
                assertThat(all, hasSize(0));

            }

        }

        @Nested
        class notExisting {

            @Test
            public void shouldReturnHttpStatusNotFound() throws Exception {
                final Client client = createClient();
                performRequestWith("123")
                    .andExpect(status().isNotFound());
            }

            @Test
            public void shouldNotDelete() throws Exception {
                final Client client = createClient();
                performRequestWith("123");

                final List<Client> all = clientsService.findAll();
                assertThat(all, hasSize(1));

            }

        }

    }
}