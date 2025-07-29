package bjs.zangbu.deal.dto.response;

import bjs.zangbu.deal.vo.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
//건축물 대장 리턴 형식
public class DownloadUrlResponse {
    private String downloadUrl;   // (임시) S3 퍼블릭 URL
    private String fileName;      // ex) building-register-123.pdf
    private DocumentType type;    // BUILDING_REGISTER …
}
