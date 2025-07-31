package bjs.zangbu.ncp.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 멀티파트 업로드 시 사용되는 ETag 정보 담는 클래스 <br> - 각 파트 번호와 해당 파트의 ETag 값을 가짐 <br> - complete 시 XML 생성에 사용됨
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PartETag {

  /**
   * 업로드된 파트 번호 (1부터 시작함)
   */
  private int partNumber;

  /**
   * 업로드된 파트의 ETag 값 (서버 응답에서 추출됨)
   */
  private String eTag;


}
