package bjs.zangbu.complexList.mapper;
import bjs.zangbu.complexList.vo.ComplexList;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ComplexListMapper {
    // 단지(ComplexList) 정보를 DB에 삽입하는 메서드
    Long createComplexList(ComplexList complexList);
}
