package org.surest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.surest.dto.AuthRequest;
import org.surest.dto.AuthResponse;
import org.surest.dto.MemberDto;
import org.surest.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void cleanDb() {
        memberRepository.deleteAll();
    }

    private String obtainAccessToken() throws Exception {
        return loginAndGetJwt("admin", "admin@123");
    }

    private String loginAndGetJwt(String username, String password) throws Exception {
        AuthRequest request = new AuthRequest(username, password);
        String json = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(body, AuthResponse.class);
        return authResponse.getToken();
    }

    @Test
    void createAndListMembers_success() throws Exception {
        String token = obtainAccessToken();

        MemberDto dto = MemberDto.builder()
                .firstName("Rahul")
                .lastName("Chowdhury")
                .dateOfBirth(LocalDate.of(2001, 2, 16))
                .email("Rahul.it+" + System.currentTimeMillis() + "@example.com")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                        post("/api/v1/members")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        get("/api/v1/members")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andExpect(status().isOk());
    }

    @Test
    void createMember_duplicateEmail_returnsConflict() throws Exception {
        String token = obtainAccessToken();

        String email = "duplicate+" + System.currentTimeMillis() + "@example.com";

        MemberDto dto = MemberDto.builder()
                .firstName("Rittik")
                .lastName("Chowdhury")
                .dateOfBirth(LocalDate.of(1999, 1, 1))
                .email(email)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                        post("/api/v1/members")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/api/v1/members")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void testUserAccessingAdminEndpoint() throws Exception {
        String token = loginAndGetJwt("user", "user@123");

        mockMvc.perform(
                        delete("/api/v1/members/{id}", UUID.randomUUID())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void testInvalidJwtToken() throws Exception {
        mockMvc.perform(
                        get("/api/v1/members")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.jwt.token")
                )
                .andExpect(status().isUnauthorized());
    }
}
