package bjs.zangbu.building.service;

import bjs.zangbu.building.dto.response.MainResponse.*;

/**
 * 메인 페이지와 관련된 서비스를 제공하는 인터페이스입니다.
 */
public interface MainService {

    /**
     * 주어진 멤버 ID에 해당하는 메인 페이지 정보를 조회합니다.
     *
     * @param memberId 조회할 회원의 ID
     * @return 메인 페이지 표시용 데이터 전송 객체
     */
    MainPageResponse mainPage(String memberId);
}
