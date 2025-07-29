package bjs.zangbu.imageList.mapper;

import bjs.zangbu.imageList.vo.ImageList;
import org.apache.ibatis.annotations.Mapper;

/**
 * 이미지 관련 DB 매핑 인터페이스 (MyBatis Mapper)
 * 이미지 정보 등록 및 대표 이미지 조회 기능을 담당합니다.
 */
@Mapper
public interface ImageListMapper {

    /**
     * 이미지 정보를 데이터베이스에 삽입합니다.
     *
     * @param imageList 삽입할 이미지 정보 객체
     */
    void createImageList(ImageList imageList);

    /**
     * 특정 건물에 대한 대표 이미지 URL을 조회합니다.
     *
     * @param buildingId 대표 이미지를 조회할 건물 ID
     * @return 대표 이미지 URL 문자열
     */
    String representativeImage(Long buildingId);
}
