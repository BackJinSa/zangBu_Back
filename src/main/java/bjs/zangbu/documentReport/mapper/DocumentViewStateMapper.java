package bjs.zangbu.documentReport.mapper;

import bjs.zangbu.documentReport.vo.DocumentViewState;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DocumentViewStateMapper {

    /**
     * 성공적으로 보기 완료 시 UPSERT
     */
    int upsert(@Param("memberId") String memberId,
               @Param("buildingId") Long buildingId,
               @Param("docType") String docType);

    /**
     * 이 회원이 두 문서를 모두 봤는지(버튼 활성화 조건)
     */
    int countViewedBoth(@Param("memberId") String memberId,
                        @Param("buildingId") Long buildingId);

    /**
     * 단일 문서 조회 여부(옵션)
     */
    DocumentViewState findOne(@Param("memberId") String memberId,
                              @Param("buildingId") Long buildingId,
                              @Param("docType") String docType);
}
