package bjs.zangbu.documentReport.service;

import bjs.zangbu.deal.vo.DocumentType;
import bjs.zangbu.documentReport.dto.request.DocumentReportRequest.DocumentReportRequestElement;
import bjs.zangbu.documentReport.dto.request.EstateRegisterData;
import bjs.zangbu.documentReport.dto.response.DocumentReportResponse.DocumentReportElement;

public interface DocumentReportService {

  public Long createFromEstate(Long dealId, EstateRegisterData data);

  // 분석 리포트 조회  - 특정 reportId 에 대한 조회
  DocumentReportElement getDocumentReportByReportId(Long reportId);

  // 분석 리포트 조회  - buildingId, userId 에 대한 최근 리포트 1개 조회
  DocumentReportElement getDocumentReportByUserIdAndBuildingId(String memberId, Long buildingId);

  // 분석 리포트 저장 - 저장 후 저장된 값 반환
  DocumentReportElement createDocumentReport(String userId,
      DocumentReportRequestElement request);

  String getLatestUrlOrNull(String memberId, Long buildingId, DocumentType type);

  void saveLatestUrl(String memberId, Long buildingId, DocumentType type, String url);

  void deleteLatestUrl(String memberId, Long buildingId, DocumentType type);
}
