package bjs.zangbu.imageList.service;

import bjs.zangbu.imageList.vo.ImageList;

/**
 * 이미지 관련 서비스 인터페이스
 */
public interface ImageListService {

    /**
     * 이미지 정보를 등록 처리합니다.
     *
     * @param imageList 등록할 이미지 정보 객체
     */
    void createImageList(ImageList imageList);

    /**
     * 특정 건물의 대표 이미지 URL을 조회합니다.
     *
     * @param buildingId 조회할 건물 ID
     * @return 대표 이미지 URL 문자열
     */
    String representativeImage(Long buildingId);
}
