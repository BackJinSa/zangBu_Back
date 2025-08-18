package bjs.zangbu.mongo.Dao;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Date;
import java.util.Map;

import static com.mongodb.client.model.Filters.*;

public class ReportDocumentDao {

    private final MongoCollection<Document> coll;

    public ReportDocumentDao(MongoDatabase db, String collectionName) {
        this.coll = db.getCollection(collectionName); // ← report_documents
    }

    /** 앱 시작 시 1회: 인덱스 보장 */
    public void ensureIndexes() {
        coll.createIndex(
                Indexes.compoundIndex(
                        Indexes.ascending("memberId"),
                        Indexes.ascending("buildingId"),
                        Indexes.ascending("docType")),
                new IndexOptions().unique(true).name("uniq_member_building_type")
        );
        coll.createIndex(
                Indexes.compoundIndex(
                        Indexes.ascending("memberId"),
                        Indexes.ascending("buildingId"),
                        Indexes.ascending("docType"),
                        Indexes.descending("issuedAt")),
                new IndexOptions().name("idx_lookup")
        );
    }

    /** (memberId, buildingId, docType) 최신 1건 업서트 */
    public void upsertParsed(String memberId, Long buildingId, String docType,
                             Map<String,Object> parsed, Date issuedAt) {
        Bson filter = and(eq("memberId", memberId), eq("buildingId", buildingId), eq("docType", docType));
        Document set = new Document()
                .append("memberId", memberId)
                .append("buildingId", buildingId)
                .append("docType", docType)
                .append("issuedAt", issuedAt != null ? issuedAt : new Date())
                .append("parsed", parsed);
        Document update = new Document("$set", set)
                .append("$setOnInsert", new Document("createdAt", new Date()))
                .append("$currentDate", new Document("updatedAt", true));

        coll.updateOne(filter, update, new UpdateOptions().upsert(true));
    }

    /** PDF 메타(url/size/md5) 갱신 */
    public void updatePdfMeta(String memberId, Long buildingId, String docType,
                              String url, Long size, String md5) {
        Bson filter = and(eq("memberId", memberId), eq("buildingId", buildingId), eq("docType", docType));
        Document pdf = new Document("url", url).append("size", size).append("md5", md5);
        Document update = new Document("$set", new Document("pdf", pdf))
                .append("$currentDate", new Document("updatedAt", true));
        var r = coll.updateOne(filter, update);
        if (r.getMatchedCount() == 0) {
            throw new IllegalStateException("먼저 JSON을 upsertParsed로 저장하세요.");
        }
    }

    public Document findOne(String memberId, Long buildingId, String docType) {
        return coll.find(and(eq("memberId", memberId), eq("buildingId", buildingId), eq("docType", docType))).first();
    }
}
