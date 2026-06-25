package com.nyamnyam.coach;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class NyamNyamCoachApplicationTests {

    @Test
    void contextLoads() {
        // Spring ApplicationContext 정상 로딩 확인
    }
}
