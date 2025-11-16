package com.dockerino.demo;

import com.dockerino.demo.api.ShortUrlApi;
import com.dockerino.demo.config.NoSecurityConfig;
import com.dockerino.demo.service.ShortUrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ShortUrlApi.class)
@Import(NoSecurityConfig.class)
class DemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShortUrlService shortUrlService;

    @Test
    void load() throws Exception {
        String invalidRequest = """
                {
                  "originalUrl": "not-a-valid-url"
                }
                """;

        ResultActions res = mockMvc.perform(post("/api/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest));

        res.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fields").isMap())
                .andExpect(jsonPath("$.fields.originalUrl").value("Please provide a valid URL"));
    }

}
