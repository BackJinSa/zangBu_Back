// src/main/java/bjs/zangbu/report/service/ReportDocumentMongoService.java
package bjs.zangbu.report.service;

import bjs.zangbu.deal.vo.DocumentType;
import bjs.zangbu.documentReport.service.DocumentToMongoService;
import bjs.zangbu.mongo.Dao.ReportDocumentDao;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentToMongoServiceImpl implements DocumentToMongoService {

    private final ReportDocumentDao dao;
    private final ObjectMapper om;

    /** CODEF DTO를 Mongo에 JSON으로 업서트 (resOriGinalData 제거) */
    public void saveJson(String memberId, Long buildingId, DocumentType type, Object dto) {
        Map<String,Object> parsed = om.convertValue(dto, new TypeReference<Map<String, Object>>(){});
        // CODEF의 base64 PDF 필드 제거(철자 주의)
        parsed.remove("resOriGinalData");
        // issuedAt 있으면 Date로 변환 시도(없으면 now)
        Date issuedAt = resolveIssuedAt(parsed);
        dao.upsertParsed(memberId, buildingId, type.name(), parsed, issuedAt);
    }

    /** PDF 업로드 후 메타 갱신 */
    public void updatePdfMeta(String memberId, Long buildingId, DocumentType type,
                              String url, long size, String md5) {
        dao.updatePdfMeta(memberId, buildingId, type.name(), url, size, md5);
    }

    private Date resolveIssuedAt(Map<String,Object> parsed) {
        Object s = parsed.get("issuedAt");
        if (s instanceof String str) {
            try { return Date.from(Instant.parse(str)); } catch (Exception ignore) {}
        }
        return new Date();
    }
}
