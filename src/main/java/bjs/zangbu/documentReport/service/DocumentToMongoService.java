package bjs.zangbu.documentReport.service;

import bjs.zangbu.deal.vo.DocumentType;

public interface DocumentToMongoService {

    /** CODEF DTO(JSON) 저장: resOriGinalData 제거 후 (memberId, buildingId, docType) 업서트 */
    void saveJson(String memberId, Long buildingId, DocumentType type, Object dto);

    /** PDF 업로드 후 메타(url/size/md5) 갱신 */
    void updatePdfMeta(String memberId, Long buildingId, DocumentType type,
                       String url, long size, String md5);
}