package bjs.zangbu.scheduler.service;

import bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder;
import bjs.zangbu.ncp.service.MultipartUploaderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UploadCleanupScheduler {

  private final MultipartUploaderService uploader;


  /**
   * 매일 새벽 3시에 미완료된 멀티파트 업로드 정리
   */
  @Scheduled(cron = "0 0 3 * * *")
  public void cleanIncompleteUploads() {
    try {
      String bucket = HeaderCreationHolder.BUCKET_NAME;
      List<Pair<String, String>> list = uploader.listIncompleteUploads(bucket);

      for (Pair<String, String> upload : list) {
        String objectKey = upload.getLeft();
        String uploadId = upload.getRight();
        uploader.abortMultipartUpload(bucket, objectKey, uploadId);
      }
    } catch (Exception e) {
      log.error("자동 정리 실패: {}", e.getMessage());
    }
  }
}
