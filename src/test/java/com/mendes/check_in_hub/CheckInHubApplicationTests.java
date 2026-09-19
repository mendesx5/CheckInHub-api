package com.mendes.check_in_hub;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Import(TestContainersConfig.class)
@ActiveProfiles("test")
class CheckInHubApplicationTests {

	@Test
	void contextLoads() {
	}

}
