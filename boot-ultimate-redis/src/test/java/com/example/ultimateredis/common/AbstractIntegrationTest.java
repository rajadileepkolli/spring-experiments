package com.example.ultimateredis.common;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.example.ultimateredis.utils.AppConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles({AppConstants.PROFILE_CLUSTER})
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = {TestcontainersConfiguration.class})
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    @Autowired protected MockMvc mockMvc;

    @Autowired protected ObjectMapper objectMapper;
}
