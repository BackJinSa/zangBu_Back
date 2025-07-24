package bjs.zangbu.imageList.mapper;

import bjs.zangbu.imageList.vo.ImageList;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageListMapper {
    // 이미지 정보를 DB에 삽입하는 메서드
    void createImageList(ImageList imageList);
}
