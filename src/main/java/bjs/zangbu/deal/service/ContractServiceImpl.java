package bjs.zangbu.deal.service;

import bjs.zangbu.codef.converter.CodefConverter;
import bjs.zangbu.codef.service.CodefService;
import bjs.zangbu.codef.service.CodefTwoFactorService;
import bjs.zangbu.deal.S3.S3Uploader;
import bjs.zangbu.deal.dto.join.DealDocumentInfo;
import bjs.zangbu.deal.dto.join.DealWithSaleType;
import bjs.zangbu.deal.dto.request.BuildingRegisterRequest;
import bjs.zangbu.deal.dto.response.BuildingRegisterResponse;
import bjs.zangbu.deal.mapper.DealMapper;
import bjs.zangbu.deal.util.PdfUtil;
import bjs.zangbu.notification.vo.SaleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Log4j2
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final DealMapper dealMapper;
    private final CodefTwoFactorService codefTwoFactorService;
    private final S3Uploader s3Uploader;

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

    @Override
    public BuildingRegisterResponse generateRegisterPdf(Long dealId) throws Exception {
        // 1) DB 조회
        DealDocumentInfo deal = dealMapper.selectDocumentInfo(dealId);
        // request json 형식에 맞게 파싱
        BuildingRegisterRequest request = BuildingRegisterRequest.from(deal);
        // 1차·2차가 섞여 있을 수 있는 응답(rawResponse)
        String rawResponse = codefTwoFactorService.generalBuildingLeader(request);
        // url 디코딩으로 최종 json 만듦
        String decodedJson = URLDecoder.decode(rawResponse, StandardCharsets.UTF_8);
        // 필요한 데이터만 파싱하여 저장
        BuildingRegisterResponse dto = CodefConverter.parseDataToDto(decodedJson, BuildingRegisterResponse.class);

        /* 5) PDF 바이트 추출 */
        byte[] pdfBytes = PdfUtil.decodePdfBytes(dto.getResOriginalData());

        /* 6) S3 업로드 */
        String key  = "building-register-" + dealId + ".pdf";
        String url  = s3Uploader.uploadPdf(pdfBytes, key);   // ← public URL or presigned URL

        return new BuildingRegisterResponse(url, dto.getResViolationStatus());
    }
}
