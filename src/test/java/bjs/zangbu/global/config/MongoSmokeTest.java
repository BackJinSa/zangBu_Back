package bjs.zangbu.global.config;

import bjs.zangbu.mongo.Dao.ReportDocumentDao;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
class MongoSmokeTest {
    @Autowired
    ReportDocumentDao dao;

    @Test
    @DisplayName("mongodb 연동 테스트")
    void upsert_find_updatePdf_ok() {
        String memberId = String.valueOf(1L);
        Long buildingId = 101L;
        String docType = "REGISTER";

        // 1) 업서트(JSON 저장)
        dao.upsertParsed(memberId, buildingId, docType,
                Map.of("hello", true, "issuedAt", "2025-08-16T09:00:00Z"), new Date());

        // 2) 조회 확인
        Document d = dao.findOne(memberId, buildingId, docType);
        assertNotNull(d, "문서가 저장되어야 합니다");
        assertTrue(((Document)d.get("parsed")).getBoolean("hello"));

        // 3) PDF 메타 갱신
        dao.updatePdfMeta(memberId, buildingId, docType,
                "https://ncp/bucket/estate-register/11111.pdf", 12345L, null);

        // 4) 다시 조회하여 pdf 확인
        Document d2 = dao.findOne(memberId, buildingId, docType);
        assertNotNull(d2.get("pdf"), "pdf 메타가 있어야 합니다");
        assertEquals(12345L, ((Document) d2.get("pdf")).getLong("size"));
    }

}