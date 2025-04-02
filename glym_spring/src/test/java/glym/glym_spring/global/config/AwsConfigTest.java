package glym.glym_spring.global.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AwsConfigTest {
    private final AwsConfig awsConfig = new AwsConfig();

    @Test
    void getRegion() {
        awsConfig.amazonS3();
    }

}