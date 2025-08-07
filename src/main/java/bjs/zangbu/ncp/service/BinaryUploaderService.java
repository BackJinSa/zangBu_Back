package bjs.zangbu.ncp.service;

public interface BinaryUploaderService {

  /**
   * PDF 바이너리를 Object Storage 에 업로드함.
   *
   * @param bucketName 버킷 이름
   * @param objectName 저장될 객체 이름 (예: "example.pdf")
   * @param pdfBytes   PDF 바이너리 데이터
   */
  void putPdfObject(String bucketName, String objectName, byte[] pdfBytes) throws Exception;
}
