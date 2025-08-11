package bjs.zangbu.deal.service;

import bjs.zangbu.codef.converter.CodefConverter;
import bjs.zangbu.codef.service.CodefService;
import bjs.zangbu.codef.service.CodefTwoFactorService;
import bjs.zangbu.deal.dto.join.DealDocumentInfo;
import bjs.zangbu.deal.dto.join.DealWithSaleType;
import bjs.zangbu.deal.dto.request.BuildingRegisterRequest;
import bjs.zangbu.deal.dto.request.EstateRegistrationRequest;
import bjs.zangbu.deal.dto.response.BuildingRegisterResponse;
import bjs.zangbu.deal.dto.response.DealResponse;
import bjs.zangbu.deal.dto.response.EstateRegistrationResponse;
import bjs.zangbu.deal.mapper.DealMapper;
import bjs.zangbu.deal.util.PdfUtil;
import bjs.zangbu.deal.vo.DocumentType;
import bjs.zangbu.documentReport.dto.request.EstateRegisterData;
import bjs.zangbu.ncp.service.BinaryUploaderService;
import bjs.zangbu.notification.vo.SaleType;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.ACCESS_KEY;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.BUCKET_NAME;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Log4j2
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final DealMapper dealMapper;
    private final CodefService codefService;
    private final CodefTwoFactorService codefTwoFactorService;
    private final BinaryUploaderService binaryUploaderService;


    @Override
    @Transactional(readOnly = true)
    /** dealId → "/contracts/xxx.pdf" 상대 경로 반환 */
    public String getContractPdf(Long dealId) {
        DealWithSaleType deal = dealMapper.findWithType(dealId);

        if(deal == null) {
            throw new IllegalArgumentException("해당 거래가 존재하지 않습니다 dealId: "+ dealId);
        }

        String path;
        SaleType type = deal.getSaleType(); // 매매 전월세 타입 분류

        if(type == SaleType.TRADING){ // 매매
            path = "/contracts/sale_contract.pdf";
        } else if (type == SaleType.MONTHLY || type == SaleType.CHARTER) {
            // 전월세
            path = "/contracts/lease_contract.pdf";
        } else{
            throw new IllegalStateException("거래 타입 오류, saleType : " + type);
        }
        return path;
    }

    // 등기부등본 발급 api
    @Override
    // todo : 중복코드가 많아서 정리가 필요함
    public EstateRegistrationResponse getEstateRegistrationPdf(Long dealId)
            throws Exception {
        //DB에서 데이터 가져와서 request 생성
        EstateRegistrationRequest request = dealMapper.getEstateRegistrationRequest(dealId);
        // codef에서 응답 가져오기
        String rawResponse = codefService.realEstateRegistrationLeader(request);;
        // url 디코딩으로 최종 json 만듦 todo: 추가 자료 들고오기
//        String decodedJson = URLDecoder.decode(rawResponse, StandardCharsets.UTF_8);
        // pdf base64 파싱로 직
        EstateRegistrationResponse base64 = CodefConverter.parseDataToDto(
                rawResponse, EstateRegistrationResponse.class);
        // PDF 바이트 추출
        byte[] pdfBytes = PdfUtil.decodePdfBytes(base64.getResOriginalData());
        /* 6) S3 업로드 */
        String key  = "estate-Register/" + dealId + ".pdf";
        String url = binaryUploaderService.putPdfObject(BUCKET_NAME,key,pdfBytes);
        //추가 로직 ★ 분석 리포트 데이터 저장
//        EstateRegisterData data = CodefConverter.parseDataToDto(
//                rawResponse, EstateRegisterData.class);

        return new EstateRegistrationResponse(url, base64.getCommUniqueNo());
    }
    // 건축물대장 발급 api
    @Override
    public DealResponse.Download generateRegisterPdf(Long dealId) throws Exception {
        // 1) DB 조회
        DealDocumentInfo deal = dealMapper.getDocumentInfo(dealId);
        // request json 형식에 맞게 파싱
        BuildingRegisterRequest request = BuildingRegisterRequest.from(deal);
        // 1차·2차가 섞여 있을 수 있는 응답(rawResponse)
        String rawResponse = codefTwoFactorService.generalBuildingLeader(request);
        // url 디코딩으로 최종 json 만듦 -> codef에서 제공함 패스
//        String decodedJson = URLDecoder.decode(rawResponse, StandardCharsets.UTF_8);
        // pdf base64 데이터 저장
        BuildingRegisterResponse dto =
                CodefConverter.parseDataToDto(rawResponse, BuildingRegisterResponse.class);
        // json 파싱
        String base64Pdf = dto.getResOriGinalData();
        // PDF 바이트 추출
        byte[] pdfBytes = PdfUtil.decodePdfBytes(base64Pdf);
        /* ncp 업로드*/
        String key  = "building-register/" + dealId + ".pdf";
        String url = binaryUploaderService.putPdfObject(BUCKET_NAME,key,pdfBytes);

        return new DealResponse.Download(url);
    }
}
