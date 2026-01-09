package com.jiajin.simplerestserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SimpleRestServerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldFetchAndCacheRepository() throws Exception {

        mockMvc.perform(get("/repositories/spring-projects/spring-boot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName")
                        .value("spring-projects/spring-boot"));

        // second call - should hit DB cache
        mockMvc.perform(get("/repositories/spring-projects/spring-boot"))
                .andExpect(status().isOk());
    }

}
