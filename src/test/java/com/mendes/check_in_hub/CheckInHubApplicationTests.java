package com.mendes.check_in_hub;

import com.mendes.check_in_hub.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(TestContainersConfig.class)
@ActiveProfiles("test")
class CheckInHubApplicationTests {

	@Test
	void contextLoads() {
	}

}