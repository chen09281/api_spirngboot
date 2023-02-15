package com.api;

import com.api.service.ApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiSpringbootApplicationTests {

    @Autowired
    private ApiService apiService;
    @Test
    void contextLoads() {
        apiService.selectUser();

    }

}
