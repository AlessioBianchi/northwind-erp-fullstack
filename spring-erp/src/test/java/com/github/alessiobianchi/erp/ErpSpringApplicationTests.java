package com.github.alessiobianchi.erp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ErpSpringApplicationTests {

    @Test
    void contextLoads() {
    }

}
