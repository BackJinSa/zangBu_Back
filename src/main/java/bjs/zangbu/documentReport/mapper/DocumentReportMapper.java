package bjs.zangbu.documentReport.mapper;

import bjs.zangbu.documentReport.vo.DocumentReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DocumentReportMapper {

  /**
   * 분석 리포트 조회  - 특정 reportId 에 대한 조회
   */
  DocumentReport getDocumentReportByReportId(Long reportId);

  /**
   * 분석 리포트 조회  - buildingId, userId 에 대한 최근 리포트 1개 조회
   */
  DocumentReport getDocumentReportByUserIdAndBuildingId(@Param("userId") String userId,
      @Param("buildingId") Long buildingId);

  // 분석 리포트 저장 - 저장 후 저장된 값 반환
  DocumentReport createDocumentReport(DocumentReport documentReport);

}
