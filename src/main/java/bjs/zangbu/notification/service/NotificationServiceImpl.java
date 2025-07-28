package bjs.zangbu.notification.service;

import bjs.zangbu.bookmark.mapper.BookMarkMapper;
import bjs.zangbu.bookmark.vo.Bookmark;
import bjs.zangbu.building.dto.response.BuildingResponse;
import bjs.zangbu.building.mapper.BuildingMapper;
import bjs.zangbu.building.vo.Building;
import bjs.zangbu.deal.mapper.DealMapper;
import bjs.zangbu.fcm.mapper.FcmMapper;
import bjs.zangbu.fcm.service.FcmSender;
import bjs.zangbu.member.mapper.MemberMapper;
import bjs.zangbu.notification.mapper.NotificationMapper;
import bjs.zangbu.notification.vo.Notification;
import bjs.zangbu.notification.vo.Type;
import bjs.zangbu.review.mapper.ReviewMapper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static bjs.zangbu.notification.dto.response.NotificationResponse.*;
import static bjs.zangbu.notification.dto.response.NotificationResponse.NotificationElement.formatMoney;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final BookMarkMapper bookmarkMapper;
    private final BuildingMapper buildingMapper;
    private final DealMapper dealMapper;
    private final ReviewMapper reviewMapper;
    private final FcmMapper fcmMapper;
    private final FcmSender fcmSender;
    private final MemberMapper memberMapper;

    // ====================== API 전용 ======================
    // [API] 전체 알림 조회(DB select)
    @Override
    public NotificationAll getAllNotifications(String memberId) {
        // 1. 알림 VO 리스트 조회
        List<Notification> notifications = notificationMapper.selectAllByMemberId(memberId);

        // 2. PageInfo로 감싸서 페이지 정보 획득
        PageInfo<Notification> pageInfo = new PageInfo<>(notifications);

        // 3. DTO로 변환
        return NotificationAll.toDto(pageInfo);
    }

    // [API] 하나의 알림 읽음 처리 (DB update)
    @Override
    public boolean markAsRead(String memberId, Long notificationId) {
        try {
            // 하나의 알림 수정 후 성공했다면 return 1 / 0 이면 읽음처리가 안된것
            int updated = notificationMapper.updateIsRead(memberId, notificationId);
            return updated == 1;
        } catch (Exception e) {
            log.error("알림 읽음 처리 실패: memberId={}, notificationId={}", memberId, notificationId, e);
            throw new RuntimeException("알림 읽음 처리 중 오류가 발생했습니다.");
        }
    }

    // [API] 전체 알림 읽음 처리 (DB update) return 값 : 읽음 처리된 알림 개수
    @Override
    public MarkAllReadResult markAllAsRead(String memberId) {
        try {
            // 전체 알림 수정 후 성공했다면 return 읽음처리된 알림 개수 / 0 이면 읽음처리된 알림이 없는것
            int updated = notificationMapper.updateAllIsRead(memberId);
            return new MarkAllReadResult(updated);
        } catch (Exception e) {
            log.error("알림 읽음 처리 실패: memberId={}", memberId, e);
            throw new RuntimeException("알림 읽음 처리 중 오류가 발생했습니다.");
        }
    }

    // [API] 알림 삭제 (DB delete)
    @Override
    public boolean removeNotification(String memberId, Long notificationId) {
        try {
            // 선택된 알림 삭제/ 성공했다면 return 1/ 0 이면 삭제가 안된것
            int deleted = notificationMapper.removeNotification(memberId, notificationId);

            // 삭제된 행이 없으면 → 삭제할 게 없는 것이므로 false 반환
            return deleted > 0;

        } catch (DataAccessException e) {
            log.error("알림 삭제 실패: memberId={}, notificationId={}", memberId, notificationId, e);
            throw new RuntimeException("알림 삭제 중 데이터베이스 오류가 발생했습니다.", e);
        } catch (Exception e) {
            log.error("알림 삭제 실패: memberId={}, notificationId={}", memberId, notificationId, e);
            throw new RuntimeException("알림 삭제 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }

    // ====================== 알림 저장/전송 ===========================

    /**
     * 중복 알림 여부를 확인한 뒤,
     * 오늘 동일한 알림이 없으면 notification 테이블에 저장하고
     * FCM 푸시 알림을 해당 유저의 모든 디바이스에 전송한다.
     *
     * @param memberId 알림 대상 사용자 ID
     * @param building 해당 알림이 관련된 건물
     * @param type 알림 유형 (예: BUILDING, DEAL, REVIEW)
     * @param title 알림 제목 (FCM용)
     * @param message 알림 본문 메시지 (DB/FCM용)
     * @param currentPrice 현재 매물 가격 (DB에 저장될 정보)
     */
    @Override
    public void sendNotificationIfNotExists(String memberId, Building building,
                                            Type type, String title, String message, int currentPrice) {

        // null 값이 존재하는지 확인 후 존재하면 종료
        if (memberId == null || building == null || type == null || title == null || message == null) {
            log.warn("알림 전송 실패: 필수 값이 null입니다. memberId={}, building={}, type={}, title={}, message={}",
                    memberId, building, type, title, message);
            return;
        }

        boolean exists = false;

        // 1. 중복 알림 체크 (BUILDING 타입일 경우만)
        if (type == Type.BUILDING) {
            try {
                exists = notificationMapper.existsSamePriceNotificationToday(
                        memberId,
                        building.getBuildingId(),
                        type.name(),
                        currentPrice
                );
            } catch (Exception e) {
                log.error("중복 알림 체크 실패: {}", e.getMessage());
                return;
            }
        }

        if (exists) return; // 중복 알림이 있으면 전송 생략

        // 2. 최신 리뷰 평점 조회 (REVIEW 타입일 때만)
        Integer rank = 0;
        if (type == Type.REVIEW) {
            try {
                Integer latestRank = reviewMapper.selectLatestReviewRank(building.getBuildingId());
                rank = (latestRank != null) ? latestRank : 0;
            } catch (Exception e) {
                log.warn("리뷰 평점 조회 실패: buildingId={}, {}", building.getBuildingId(), e.getMessage());
            }
        }

        // 3. 알림 객체 생성 (VO)
        Notification notification = new Notification(
                null,            // notification_id (Auto Increment)
                message,                    // 알림 메시지
                false,                      // 읽음 여부
                type,                       // 알림 타입 (BUILDING, REVIEW, TRADE 등)
                new Date(),                 // 생성 시각
                building.getSaleType(),     // 매물 종류 (APARTMENT, VILLA 등)
                currentPrice,               // 시세 또는 거래가
                building.getInfoBuilding(), // 주소 또는 위치 설명
                rank,                       // 리뷰 평점 (리뷰 알림이면 실제 점수, 아니면 0)
                memberId,                   // 알림 대상자
                building.getBuildingId()    // 빌딩 아이디
        );

        // 4. [DB] 알림 정보를 notification 테이블에 저장
        try {
            notificationMapper.insertNotification(notification);
        } catch (Exception e) {
            log.error("알림 저장 실패: {}", e.getMessage());
            return;
        }

        try {
            // 5. [DB] 사용자가 알림을 수신 동의 했는지 확인
            boolean consent = memberMapper.selectFcmConsentByMemberId(memberId);
            if (!consent) return; // 사용자 수신 동의 X → 전송 안 함

            // 5. [FCM] 알림 대상자의 디바이스 토큰 조회
            List<String> tokens = fcmMapper.selectTokensByMemberId(memberId);

            // 디바이스가 하나 이상 등록되어 있으면 푸시 알림 전송
            if (tokens != null && !tokens.isEmpty()) {
                fcmSender.sendToMany(tokens, title, message);
            }
        } catch (Exception e) {
            log.warn("FCM 전송 실패: {}", e.getMessage());
        }
    }

    // ====================== 트리거 전용 ===========================

    /**
     * [시세 변동 감지 메서드]
     * 사용자가 찜한 매물 중에서 시세가 변동된 경우,
     * 알림을 저장하고 FCM 푸시 메시지를 전송한 뒤,
     * bookmark 테이블의 가격을 최신 시세로 갱신한다.
     */
    @Override
    public void detectPriceChangeForAllBookmarks() {
        // 1. [DB] 모든 bookmark 데이터 조회 (찜한 매물 리스트)
        // select bookmark_id, member_id, building_id, price
        List<Bookmark> bookmarks = bookmarkMapper.selectAllBookmarks();

        // 2. 각 찜한 매물(bookmark)을 반복 처리
        for (Bookmark bookmark : bookmarks) {

            // (1) 찜 정보에서 필요한 값 꺼내기
            Long buildingId = bookmark.getBuildingId();   // 어떤 건물인지
            String memberId = bookmark.getUserId();       // 누가 찜했는지
            int oldPrice = bookmark.getPrice();           // 찜 당시 매물 가격

            // (2). [DB] building 테이블에서 해당 매물(buildingId)의 정보를 가져온다.
            // select *
            Building building =  buildingMapper.getBuildingById(buildingId);
            if (building == null) {
                // 예외 상황: 건물 정보가 사라졌을 수도 있음
                log.warn("[시세변동] 건물 정보 없음: buildingId = {}", buildingId);
                continue; // 다음 bookmark로 넘어감
            }

            // (3) 최신 가격 확인
            int currentPrice = building.getPrice();
            if (oldPrice == currentPrice) continue; // 가격이 그대로면 알림 필요 없음

            // (4) 알림 제목 및 메시지 생성
            String title = "[시세 변동 알림]";
            String message = building.getBuildingName() + " 시세가 "
                    + formatMoney(oldPrice) + " → " + formatMoney(currentPrice) + "으로 변동되었습니다.";

            // (5) 알림 저장 + 푸시 전송 (공통 메서드로 추출)
            sendNotificationIfNotExists(
                    memberId,         // 알림 대상자 (찜한 유저)
                    building,       // 어떤 건물인지
                    Type.BUILDING,    // 알림 타입: 시세변동
                    title,            // 알림 제목
                    message,          // 알림 메시지
                    currentPrice      // 현재 시세 (알림에 저장됨)
            );

            // (6). [DB] bookmark 테이블에 저장된 가격을 최신 가격으로 업데이트
            // ★ 중복 알림 방지를 위해 반드시 최신화 필요 ★
            bookmarkMapper.updateBookmarkPrice(bookmark.getBookMarkId(), currentPrice);
        }
    }

    /**
     * [실거래 발생 감지 트리거 메서드]
     * deal 테이블의 상태가 'CLOSE_DEAL'(거래 성사)로 변경될 때 호출됨.
     * 해당 매물을 찜한 모든 유저에게 실시간 알림을 저장하고 FCM 푸시 메시지를 전송한다.
     *
     * @param dealId 거래 완료된 deal의 ID
     */
    @Override
    public void detectTradeHappenedNow(Long dealId) {
        // 1. [DB] dealId를 통해 거래가 발생한 건물(building) ID를 조회
        // select building_id
        Long buildingId = dealMapper.getBuildingIdByDealId(dealId);
        if (buildingId == null) {
            log.warn("[실거래 알림] buildingId를 찾을 수 없음: dealId = {}", dealId);
            return;
        }

        // 2. [DB] bookmark 테이블에서 해당 건물을 찜한 사용자 ID 목록 조회
        // select member_id
        List<String> memberIds = bookmarkMapper.selectUserIdsByBuildingId(buildingId);
        if (memberIds == null || memberIds.isEmpty()) {
            log.warn("ℹ[실거래 알림] 찜한 유저 없음: buildingId = {}", buildingId);
            return; // 찜한 유저가 없다면 알림 필요 없음
        }

        // 3. building 테이블에서 해당 건물의 상세 정보 조회
        // select *
        Building building = buildingMapper.getBuildingById(buildingId);
        if (building == null) {
            log.warn("[실거래 알림] building 정보 조회 실패: buildingId = {}", buildingId);
            return;
        }

        Integer price = building.getPrice();
        if (price == null) {
            log.warn("[실거래 알림] building 가격 정보 없음: buildingId = {}", buildingId);
            return;
        }

        // 4. 알림 제목 및 메시지 생성
        String title = "[실거래 발생 알림]";
        // 알림 메시지 생성
        // 예: "OOOOO 메믈이 5억에 거래되었습니다."
        String message = building.getBuildingName() + " 매물이 "
                + formatMoney(price) + "에 거래되었습니다.";

        // 5. 찜한 모든 유저에게 알림 저장 및 전송
        for (String memberId : memberIds) {
            // 공통 메서드 호출(알림 저장/알림 실제 전송)
            sendNotificationIfNotExists(
                    memberId,       // 알림 대상자 (찜한 유저)
                    building,       // 어떤 건물인지
                    Type.TRADE,     // 알림 타입: 실거래 발생
                    title,          // 알림 제목
                    message,        // 알림 메시지
                    price           // 거래 가격
            );
        }
    }

    /**
     * [리뷰 등록 감지 트리거 메서드]
     * 사용자가 리뷰를 등록하는 순간 호출됨.
     * 해당 매물을 찜한 모든 유저에게 리뷰 등록 알림을 저장하고 FCM 푸시 메시지를 전송한다.
     *
     * @param buildingId 리뷰가 등록된 매물의 building_id
     */
    @Override
    public void notificationReviewRegisterd(Long buildingId) {

        // 이 매물을 찜한 모든 유저에게 알림을 발송해야됨
        // 1. [DB] bookmark 테이블에서 해당 건물을 찜한 사용자 ID 목록 조회
        // select member_id
        List<String> memberIds = bookmarkMapper.selectUserIdsByBuildingId(buildingId);
        if (memberIds == null || memberIds.isEmpty()) {
            log.info("[리뷰 알림] 찜한 유저 없음: buildingId = {}", buildingId);
            return;
        }

        // 2. [DB] Building_id를 통해 Review 테이블에서 가장 최신 리뷰의 평점 가져오기
        // select rank
        Integer rank = reviewMapper.selectLatestReviewRank(buildingId);
        if (rank == null) rank = 0; // 평점이 없거나 실패한 경우 0점 처리

        // 3. [DB] building 테이블에서 해당 매물(buildingId)의 정보를 가져온다.
        // select *
        Building building =  buildingMapper.getBuildingById(buildingId);
        if (building == null) {
            log.warn("[리뷰 알림] building 정보 조회 실패: buildingId = {}", buildingId);
            return;
        }

        // 4. 빌딩의 이름을 가져온다.
        String buildingName = building.getBuildingName();

        // 4. 알림 제목 및 메시지 생성
        String title = "[리뷰 등록 알림]";
        String message = "관심 매물 " + buildingName + "에 새로운 리뷰가 등록되었습니다. (평점 " + rank + "점)";

        // 5. 찜한 유저 모두에게 알림 저장 및 전송
        for(String memberId : memberIds) {
            sendNotificationIfNotExists(
                    memberId,           // 알림 대상자
                    building,       // 어떤 건물인지
                    Type.REVIEW,        // 알림 타입: 리뷰 등록
                    title,              // 알림 제목
                    message,            // 알림 메시지
                    building.getPrice() // 매물 가격 (현재 기준)
            );
        }
    }
}