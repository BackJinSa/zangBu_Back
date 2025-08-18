package bjs.zangbu.documentReport.service;
import bjs.zangbu.building.mapper.BuildingMapper;
import bjs.zangbu.deal.dto.join.DealDocumentInfo;
import bjs.zangbu.deal.mapper.DealMapper;
import bjs.zangbu.deal.vo.DocumentType;
import bjs.zangbu.documentReport.dto.request.DocumentReportRequest.DocumentReportRequestElement;
import bjs.zangbu.documentReport.dto.request.EstateRegisterData;
import bjs.zangbu.documentReport.dto.response.DocumentReportResponse.DocumentReportElement;
import bjs.zangbu.documentReport.mapper.DocumentReportMapper;
import bjs.zangbu.documentReport.util.CodefCalcUtil;
import bjs.zangbu.documentReport.util.CodefCalcUtil.*;
import bjs.zangbu.documentReport.vo.DocumentReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DocumentReportServiceImpl implements DocumentReportService {

  private final DocumentReportMapper reportMapper;
  private final DealMapper dealMapper;
  private final BuildingMapper buildingMapper;


  @Override
  @Transactional
  public Long createFromEstate(Long dealId, EstateRegisterData data) {
    /* FK 준비  */
    Long buildingId   = dealMapper.getBuildingIdByDealId(dealId);
    Long complexId   = dealMapper.getComplexIdByDealId(dealId);
    DealDocumentInfo info = dealMapper.getDocumentInfo(dealId);

    /*  분석 값 계산 */
    Integer dealAmount = buildingMapper.selectCurrentPrice(buildingId); //거래금액 , 빌딩의 가격으로 조회
    Integer deposit = buildingMapper.getDeposit(buildingId);/*보증금*/
      long priority  = CodefCalcUtil.calcPriorityDebt(data);
    OwnerInfo owner = CodefCalcUtil.extractOwner(data);
    TrustInfo trust = CodefCalcUtil.detectTrust(data);
    int myDeposit = 0; // todo : 내 보증금, 프론트 등에서 가져와야 함

    /* VO 생성  */
    DocumentReport report = new DocumentReport(
            null,
            data.getCommUniqueNo(),
            dealAmount,
            deposit,
            0/*todo : 월세인데 분석리포트에 필요할지는 모르겠음*/,
            (int)(priority / 10_000)/*선순위채권액*/,
            myDeposit/*내 보증금 todo : 프론트에서 가져와야 되나..? */,
            dealAmount- (int)priority,
            dealAmount- (int)priority - myDeposit,
            owner.getMaskedRegNo(),
            trust.isTrustee(),
            trust.getTrustType(),
            LocalDateTime.now(),
            buildingId,
            info.getIdentity() ,  // memberId
            complexId
    ); //todo : 나머지 빈 부분 채워야 됌

    /* ③ INSERT : createDocumentReport */
    reportMapper.createDocumentReport(report);

    return report.getReportId();                 // keyProperty 로 PK 세팅됨
  }


  // 분석 리포트 조회  - 특정 reportId 에 대한 조회
  @Override
  public DocumentReportElement getDocumentReportByReportId(Long reportId) {
    return DocumentReportElement.toDto(reportMapper.getDocumentReportByReportId(reportId));
  }

  // 분석 리포트 조회  - buildingId, userId 에 대한 최근 리포트 1개 조회
  @Override
  public DocumentReportElement getDocumentReportByUserIdAndBuildingId(String memberId,
      Long buildingId) {
    return DocumentReportElement.toDto(
            reportMapper.getDocumentReportByUserIdAndBuildingId(memberId, buildingId));
  }

  // 분석 리포트 저장 - 저장 후 저장된 값 반환
  @Override
  public DocumentReportElement createDocumentReport(String memberId,
      DocumentReportRequestElement request) {
    return DocumentReportElement.toDto(
            reportMapper.createDocumentReport(
            DocumentReportRequestElement.toVo(memberId, request)
        )
    );
  }

  /** 없으면 null */
  @Override
  public String getLatestUrlOrNull(String memberId, Long buildingId, DocumentType type) {
    return reportMapper.selectUrl(memberId, buildingId, type.name());
  }
  /** upsert */
  @Override
  public void saveLatestUrl(String memberId, Long buildingId, DocumentType type, String url) {
    reportMapper.upsert(memberId, buildingId, type.name(), url);
  }

  @Override
  public void deleteLatestUrl(String memberId, Long buildingId, DocumentType type) {
    reportMapper.delete(memberId, buildingId, type.name());
  }

}
