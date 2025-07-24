package bjs.zangbu.notification.service;

import bjs.zangbu.bookmark.mapper.BookMarkMapper;
import bjs.zangbu.bookmark.vo.Bookmark;
import bjs.zangbu.building.mapper.BuildingMapper;
import bjs.zangbu.building.vo.Building;
import bjs.zangbu.deal.mapper.DealMapper;
import bjs.zangbu.notification.dto.request.NotificationRequest;
import bjs.zangbu.notification.dto.response.NotificationResponse;
import bjs.zangbu.notification.mapper.NotificationMapper;
import bjs.zangbu.notification.vo.Notification;
import bjs.zangbu.notification.vo.SaleType;
import bjs.zangbu.notification.vo.Type;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static bjs.zangbu.notification.dto.response.NotificationResponse.NotificationElement.formatMoney;
import static java.util.stream.Collectors.toList;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final BookMarkMapper bookmarkMapper;
    private final BuildingMapper buildingMapper;
    private final DealMapper dealMapper;

    // ====================== API 전용 ======================
    // [API] 전체 알림 조회(DB select)
    // 페이지네이션 구현 예정..
    @Override
    public NotificationResponse.All getAllNotifications(String memberId) {
        try {
            // 1. 알림 vo 리스트 조회 (Notification 객체들)
            List<Notification> notifications = notificationMapper.selectAllByMemberId(memberId);

            // 2. Notification -> NotificationElement로 가공 -> 리스트로 다시 변환
            List<NotificationResponse.NotificationElement> result = notifications.stream()
                    // Notification -> from(Notification) -> NotificationElement
                    .map(NotificationResponse.NotificationElement::from)
                    .collect(toList());

            // 3. 응답 객체 생성
            NotificationResponse.All response = new NotificationResponse.All(
                    result.size(),
                    result,
                    false
            );
            return response;
        } catch (Exception e) {
            log.error("알림 전체 조회 실패, e");
            throw new RuntimeException("알림 조회 중 알 수 없는 오류 발생.");
        }
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
    // 페이지네이션 구현 예정..
    @Override
    public NotificationResponse.MarkAllReadResult markAllAsRead(String memberId) {
        try {
            // 전체 알림 수정 후 성공했다면 return 읽음처리된 알림 개수 / 0 이면 읽음처리된 알림이 없는것
            int updated = notificationMapper.updateAllIsRead(memberId);
            return new NotificationResponse.MarkAllReadResult(updated);
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

    // ====================== 트리거 전용 ===========================

    // 1. 시세 변동 감지 알림
    // 2. 스케줄러 + FCM 메시지 발송(예정)
    @Override
    public void detectPriceChangeForAllBookmarks() {
        // 1. [DB] bookmark 테이블에서 모든 찜한 매물 정보를 가져온다.
        // select bookmark_id, member_id, building_id, price
        List<Bookmark> bookmarks = bookmarkMapper.selectAllBookmarks();

        // 가져온 찜 목록을 하나씩 반복 처리
        for (Bookmark bookmark : bookmarks) {

            // 2. 찜했을 당시 매물의 가격
            int oldPrice = bookmark.getPrice();

            // 3. [DB] building 테이블에서 해당 매물(buildingId)의 정보를 가져온다.
            // select *
            Building building =  buildingMapper.getBuildingById(bookmark.getBuildingId());
            int currentPrice = building.getPrice();

            // 4. 기존에 bookmark 테이블에 저장되어 있는 가격과 최신 가격이 다르면 (= 가격이 변했다면)
            if (oldPrice != currentPrice) {

                // 사용자에게 보낼 알림 메시지를 만들어준다
                // 예: "OOOOO 시세가 5억 → 5.3억으로 변동되었습니다."
                String message = building.getBuildingName() + " 시세가 "
                        + formatMoney(bookmark.getPrice()) + " → "
                        + formatMoney(currentPrice) + "으로 변동되었습니다.";

                // 4.  알림 저장을 위한 요청 객체 생성 (Request DTO)
                Notification notification = new Notification(
                        null,            // notification_id
                        message,                    // 알림 메시지
                        false,                      // 알림 읽음 여부
                        Type.BUILDING,              // 알림 타입은 BUILDING (시세 변경)
                        new Date(),                 // 알림 생성 날짜
                        null,                       // 매물 종류
                        currentPrice,               // 알림 등록 당시 매물 시세
                        null,                       // 주소
                        0,                          // 평점
                        building.getMemberId()      // 유저id
                );

                // 5. [DB] 알림 정보를 DB(notification 테이블)에 저장
                notificationMapper.insertNotification(notification);

                // 6. [DB] bookmark 테이블에 저장된 이전 시세를 최신 시세로 업데이트
                // ★ 중복 알림 방지를 위해 반드시 최신화 필요 ★
                bookmarkMapper.updateBookmarkPrice(bookmark.getBookMarkId(), currentPrice);
            }
        }
        // =========================================================
        // 알림은 생성했고 생성된 알림을 실시간으로 보내줘야됨
        // =========================================================
    }

    // 1. 실거래 발생 감지 알림
    // 2. 스케줄러 + FCM 메시지 발송(예정)
    @Override
    public void detecTradeHappenedTody() {
        // 1. [DB] deal 테이블에서 오늘 거래된 building_id 목록을 가져온다.
        // select building_id
        List<Long> buildingIds = dealMapper.selectTodayTrade();

        // 가져온 building_id를 하나씩 반복 처리
        for (Long buildingId : buildingIds) {
            // 2. [DB] building_id 를 통해 bookmark 테이블에서 이 매물을 찜한 유저 ID를 모두 가져온다.
            // select member_id
            List<String> membersIds = bookmarkMapper.selectUserIdsByBuildingId(buildingId);

            // 3. [DB] building 테이블에서 해당 매물(buildingId)의 정보를 가져온다. (알림 메시지에 표시하기 위해)
            // select *
            Building building =  buildingMapper.getBuildingById(buildingId);

            // 4. 빌딩 가격 저장 (이 가격에 실거래 당시 가격임)
            Integer price = building.getPrice();
            if (price == null) continue;

            // 5. 알림 메시지 생성
            String message = building.getBuildingName() + " 매물이 " + formatMoney(price) + "에 거래되었습니다.";

            // 6. 알림 생성 → 유저 수만큼 반복 저장
            for(int i=0; i<membersIds.size(); i++) {
                Notification notification = new Notification(
                        null,            // notification_id
                        message,                    // 알림 메시지
                        false,                      // 알림 읽음 여부
                        Type.BUILDING,              // 알림 타입은 BUILDING (시세 변경)
                        new Date(),                 // 알림 생성 날짜
                        null,                       // 매물 종류
                        price,                      // 알림 등록 당시 매물 시세
                        null,                       // 주소
                        0,                          // 평점
                        building.getMemberId()      // 유저id
                );

                // 7. [DB] 알림 정보를 DB(notification 테이블)에 저장
                notificationMapper.insertNotification(notification);
            }
        }

        // =========================================================
        // 알림은 생성했고 생성된 알림을 실시간으로 보내줘야됨
        // =========================================================
    }

    // 1. 리뷰 등록 감지 (리뷰 등록 서비스에서 리뷰가 등록되는 순간 실행되어야함)
    // 2. 실시간 트리거 (Review 등록 서비스 내부에서 실행) + FCM 메시지 발송(예정)
    @Override
    public void notificationReviewRegisterd(Long buildingId) {
        // 이 매물을 찜한 모든 유저에게 알림을 발송해야됨
        // 1. [DB] building_id 를 통해 bookmark 테이블에서 이 매물을 찜한 유저를 모두 가져온다.
        List<String> membersIds = bookmarkMapper.selectUserIdsByBuildingId(buildingId);
        if(membersIds == null || membersIds.isEmpty()) return;

        // 2. [DB] Building_id를 통해 Review 테이블에서 리뷰의 평점 가져오기
        // select rank
        Integer rank = reviewMapper.selectRankByBuildingId(buildingId);

        // 3. [DB] building 테이블에서 해당 매물(buildingId)의 정보를 가져온다.
        // select *
        Building building =  buildingMapper.getBuildingById(buildingId);

        // 4. 빌딩의 이름을 가져온다.
        String buildingName = building.getBuildingName();

        // 5. 알림 메시지 생성
        String message = "관심 매물 " + buildingName + "에 새로운 리뷰가 등록되었습니다. (평점 " + rank + "점)";

        // 6. 알림 생성 및 저장
        for(String memberId : membersIds) {
            Notification notification = new Notification(
                    null,            // notification_id
                    message,                    // 알림 메시지
                    false,                      // 알림 읽음 여부
                    Type.BUILDING,              // 알림 타입은 BUILDING (시세 변경)
                    new Date(),                 // 알림 생성 날짜
                    null,                       // 매물 종류
                    0,                          // 알림 등록 당시 매물 시세
                    null,                       // 주소
                    0,                          // 평점
                    memberId                    // 유저id
            );

            // 7. [DB] 알림 정보를 DB(notification 테이블)에 저장
            notificationMapper.insertNotification(notification);
        }

        // =========================================================
        // 알림은 생성했고 생성된 알림을 실시간으로 보내줘야됨
        // =========================================================
    }
}
