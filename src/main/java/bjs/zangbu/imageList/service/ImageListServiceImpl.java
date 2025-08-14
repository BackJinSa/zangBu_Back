package bjs.zangbu.imageList.service;

import bjs.zangbu.imageList.mapper.ImageListMapper;
import bjs.zangbu.imageList.vo.ImageList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 이미지 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class ImageListServiceImpl implements ImageListService {

    /** 이미지 매퍼 객체 주입 */
    private final ImageListMapper imageListMapper;

    /**
     * 이미지 정보를 등록 처리합니다.
     *
     * @param imageList 등록할 이미지 정보 객체
     */
    @Override
    public void createImageList(ImageList imageList) {
        imageListMapper.createImageList(imageList);
    }

    /**
     * 특정 건물의 대표 이미지 URL을 조회합니다.
     *
     * @param buildingId 조회할 건물 ID
     * @return 대표 이미지 URL 문자열
     */
    @Override
    public String representativeImage(Long buildingId) {
        return imageListMapper.representativeImage(buildingId);
    }

    @Override
    public List<String> getBuildingImageUrll(Long buildingId) {
        return  imageListMapper.getBuildingImageUrl(buildingId);
    }
}
