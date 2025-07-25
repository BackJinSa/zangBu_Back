package bjs.zangbu.documentReport.service;

import bjs.zangbu.documentReport.dto.request.DocumentReportRequest.DocumentReportRequestElement;
import bjs.zangbu.documentReport.dto.response.DocumentReportResponse.DocumentReportElement;
import bjs.zangbu.documentReport.mapper.DocumentReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentReportServiceImpl implements DocumentReportService {

  private static DocumentReportMapper documentReportMapper;

  // 분석 리포트 조회  - 특정 reportId 에 대한 조회
  @Override
  public DocumentReportElement getDocumentReportByReportId(Long reportId) {
    return DocumentReportElement.toDto(documentReportMapper.getDocumentReportByReportId(reportId));
  }

  // 분석 리포트 조회  - buildingId, userId 에 대한 최근 리포트 1개 조회
  @Override
  public DocumentReportElement getDocumentReportByUserIdAndBuildingId(String memeberId,
      Long buildingId) {
    return DocumentReportElement.toDto(
        documentReportMapper.getDocumentReportByUserIdAndBuildingId(memeberId, buildingId));
  }

  // 분석 리포트 저장 - 저장 후 저장된 값 반환
  @Override
  public DocumentReportElement createDocumentReport(String memberId,
      DocumentReportRequestElement request) {
    return DocumentReportElement.toDto(
        documentReportMapper.createDocumentReport(
            DocumentReportRequestElement.toVo(memberId, request)
        )
    );
  }
}
