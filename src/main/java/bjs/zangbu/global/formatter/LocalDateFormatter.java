package bjs.zangbu.global.formatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateFormatter {

  public static class CreatedAt {

    public static String formattingCreatedAt(LocalDateTime createdAt) {
      if (createdAt == null) {
        return "";
      }
      // 게시글 생성 일자가 오늘인 경우 작성 시간만 반환
      if (createdAt.toLocalDate().isEqual(LocalDate.now())) {
        return createdAt.format(DateTimeFormatter.ofPattern("HH:mm"));
      }

      // 게시글 생성 일자가 올해인 경우 월/일 행태로 반환
      if (createdAt.getYear() == LocalDate.now().getYear()) {
        return createdAt.format(DateTimeFormatter.ofPattern("MM/dd"));
      }

      // 위의 두 조건 모두 해당되지 않으면 년/월/일 형태로 반환
      return createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
  }

}

