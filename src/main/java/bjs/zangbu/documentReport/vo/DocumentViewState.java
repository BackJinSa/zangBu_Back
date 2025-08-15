package bjs.zangbu.documentReport.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 회원별 열람 상태 (버튼 활성화 조건)
 * - 문서 키는 공용(빌딩 기준 1행) → 여기엔 '누가/언제 봤는지'만 기록
 * - 컨트롤러에서 보기 성공 시 UPSERT(ON DUPLICATE KEY UPDATE)
 */
@Data
public class DocumentViewState {
    private String memberId;
    private Long buildingId;
    private String docType;       // "ESTATE" or "BUILDING_REGISTER"
    private LocalDateTime viewedAt;
}