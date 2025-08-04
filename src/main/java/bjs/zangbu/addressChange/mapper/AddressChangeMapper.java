package bjs.zangbu.addressChange.mapper;

import bjs.zangbu.addressChange.dto.request.ResRegisterCertRequest;
import bjs.zangbu.addressChange.vo.AddressChange;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface AddressChangeMapper {
    ResRegisterCertRequest getRegisterCertRequest(String memberId);
    /** 단건 insert. 성공 시 row.addressChangeId 에 PK가 생성됨. */
    int insert(AddressChange row);
    /** 중복 방지 체크: 동일 사용자 + 동일 주소 + 동일 전입일 존재 여부 */
    int existsByMemberAddrDate(@Param("memberId") String memberId,
                               @Param("resUserAddr") String resUserAddr,
                               @Param("resMoveInDate") LocalDateTime resMoveInDate);
}
