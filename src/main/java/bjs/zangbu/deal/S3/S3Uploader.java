package bjs.zangbu.deal.S3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3;          // @Bean 등록되어 있다고 가정
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /** PDF 바이트 업로드 후 public URL 반환 */
    public String uploadPdf(byte[] bytes, String key) {

        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("application/pdf")
                .contentLength((long) bytes.length)
                .acl("public-read")         // presigned URL 전략이면 생략
                .build();

        s3.putObject(put, RequestBody.fromBytes(bytes));

        // presigned URL 을 쓰신다면 별도 로직 필요
        return "https://" + bucket + ".s3.amazonaws.com/" + key;
    }
}