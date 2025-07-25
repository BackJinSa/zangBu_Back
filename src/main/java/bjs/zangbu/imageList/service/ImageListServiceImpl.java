package bjs.zangbu.imageList.service;
import bjs.zangbu.imageList.mapper.ImageListMapper;
import bjs.zangbu.imageList.vo.ImageList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageListServiceImpl implements ImageListService {

    private final ImageListMapper imageListMapper;

    // 이미지 정보 등록 처리
    @Override
    public void createImageList(ImageList imageList) {
        imageListMapper.createImageList(imageList);
    }

    // 대표 이미지 가져오기
    @Override
    public String representativeImage(Long buildingId) {
        return imageListMapper.representativeImage(buildingId);
    }
}
