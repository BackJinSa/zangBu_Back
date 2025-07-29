package bjs.zangbu.deal.S3;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// S3Config.java
@Configuration
@EnableConfigurationProperties
@RequiredArgsConstructor
public class S3Config {

    @Value("${cloud.aws.credentials.access-key}")  private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")  private String secretKey;
    @Value("${cloud.aws.region.static}")           private String region;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .withRegion(region)
                .build();
    }
}
