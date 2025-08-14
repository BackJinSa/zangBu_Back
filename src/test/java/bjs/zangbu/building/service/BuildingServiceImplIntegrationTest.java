package bjs.zangbu.building.service;

import bjs.zangbu.building.dto.request.BuildingRequest.*;
import bjs.zangbu.building.dto.response.BuildingResponse.ViewDetailResponse;
import bjs.zangbu.building.vo.PropertyType;
import bjs.zangbu.building.vo.SellerType;
import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.notification.vo.SaleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
public class BuildingServiceImplIntegrationTest {

    @Autowired
    private BuildingServiceImpl buildingService;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    public void setup() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            // 테스트 데이터 정리
            conn.createStatement().executeUpdate("DELETE FROM image_list");
            conn.createStatement().executeUpdate("DELETE FROM building");
            conn.createStatement().executeUpdate("DELETE FROM complex_list");
            conn.createStatement().executeUpdate("DELETE FROM member");

            // member 테이블에 테스트 데이터 삽입
            String memberSql = "INSERT IGNORE INTO member (member_id, email, password, nickname, phone, role, birth, name, consent, telecom) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(memberSql)) {
                ps.setString(1, "testMemberId");
                ps.setString(2, "test@example.com");
                ps.setString(3, "$2a$10$CLYRewHu.BlhxzL3DVOUkuCNJUItgHLqFRHFjBgVRbHFqEtvHRGZu");
                ps.setString(4, "백현빈");
                ps.setString(5, "010-1234-5678");
                ps.setString(6, "ROLE_MEMBER");
                ps.setString(7, "901010");
                ps.setString(8, "백현빈");
                ps.setInt(9, 1);
                ps.setString(10, "SKT");
                ps.executeUpdate();
            }
        }
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }


    @Test
    public void bookMarkService_찜_추가_통합테스트() throws Exception {
        Long buildingId = createTestBuilding();
        BookmarkRequest request = new BookmarkRequest();
        setField(request, "buildingId", buildingId);
        buildingService.bookMarkService(request, "testMemberId");
    }

    @Test
    public void saleRegistration_이미지업로드_통합테스트_익명클래스_MultipartFile_사용() throws Exception {
        SaleRegistrationRequest request = new SaleRegistrationRequest();

        ComplexDetails complexDetails = new ComplexDetails();
        setField(complexDetails, "resType", "아파트");
        setField(complexDetails, "complexName", "이수브라운스톤");
        setField(complexDetails, "complexNo", 11230103001448265L);
        setField(complexDetails, "sido", "서울");
        setField(complexDetails, "sigungu", "동대문구");
        setField(complexDetails, "siCode", "11230");
        setField(complexDetails, "eupmyeondong", "제기동");
        setField(complexDetails, "transactionId", "68993de7ec82188312aca2a3");
        setField(complexDetails, "address", "서울 동대문구 왕산로23길 89");
        setField(complexDetails, "zonecode", "02575");
        setField(complexDetails, "buildingName", "이수브라운스톤");
        setField(complexDetails, "bname", "제기동");
        setField(request, "complexList", complexDetails);

        BuildingDetails buildingDetails = new BuildingDetails();
        setField(buildingDetails, "sellerNickname", "백현빈");
        setField(buildingDetails, "saleType", SaleType.CHARTER);
        setField(buildingDetails, "price", 1000);
        setField(buildingDetails, "deposit", 0L);
        setField(buildingDetails, "bookmarkCount", 0);
        setField(buildingDetails, "buildingName", "이수브라운스톤");
        setField(buildingDetails, "sellerType", SellerType.OWNER);
        setField(buildingDetails, "propertyType", PropertyType.APARTMENT);
        setField(buildingDetails, "moveDate", java.time.LocalDateTime.parse("2023-09-01T00:00:00"));
        setField(buildingDetails, "infoOneline", "교통이 편리한 신축 아파트");
        setField(buildingDetails, "infoBuilding", "역세권에 위치한 깔끔하고 넓은 아파트입니다. 주변에 편의시설이 많아 생활하기 좋습니다.");
        setField(buildingDetails, "contactName", "홍길동");
        setField(buildingDetails, "contactPhone", "010-7511-7975");
        setField(buildingDetails, "facility", "엘리베이터, 주차장, 보안카메라");
        setField(request, "building", buildingDetails);

        ImageDetails imageDetails = new ImageDetails();
        byte[] fakeImageContent = "fake image content".getBytes();

        setField(imageDetails, "imageFile", new org.springframework.web.multipart.MultipartFile() {
            @Override
            public String getName() { return "imageFile"; }
            @Override
            public String getOriginalFilename() { return "file1.jpg"; }
            @Override
            public String getContentType() { return "image/jpeg"; }
            @Override
            public boolean isEmpty() { return fakeImageContent.length == 0; }
            @Override
            public long getSize() { return fakeImageContent.length; }
            @Override
            public byte[] getBytes() { return fakeImageContent; }
            @Override
            public java.io.InputStream getInputStream() { return new java.io.ByteArrayInputStream(fakeImageContent); }
            @Override
            public void transferTo(java.io.File dest) throws java.io.IOException, IllegalStateException {
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(dest)) {
                    fos.write(fakeImageContent);
                }
            }
        });

        setField(request, "image", imageDetails);
        buildingService.SaleRegistration(request, "testMemberId");
    }

    @Test
    public void removeBuilding_통합테스트() throws Exception {
        Long buildingId = createTestBuilding();
        buildingService.removeBuilding(buildingId);
    }

    // 테스트용 건물 데이터를 생성하고 ID를 반환하는 헬퍼 메서드
    private Long createTestBuilding() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            String complexSql = "INSERT INTO complex_list (res_type, complex_name, complex_no, sido, sigungu, si_code, eupmyeondong, transaction_id, address, zonecode, building_name, bname) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(complexSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "아파트");
                ps.setString(2, "테스트 아파트");
                ps.setLong(3, 1234567890L);
                ps.setString(4, "서울");
                ps.setString(5, "강남구");
                ps.setString(6, "11680");
                ps.setString(7, "개포동");
                ps.setString(8, "test_tx_id");
                ps.setString(9, "서울 강남구 개포동");
                ps.setString(10, "12345");
                ps.setString(11, "테스트 아파트");
                ps.setString(12, "개포동");
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                Long complexId = null;
                if (rs.next()) {
                    complexId = rs.getLong(1);
                }

                String buildingSql = "INSERT INTO building (seller_nickname, sale_type, price, deposit, bookmark_count, building_name, seller_type, property_type, move_date, info_oneline, info_building, contact_name, contact_phone, facility, member_id, complex_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(buildingSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    ps2.setString(1, "테스트 판매자");
                    ps2.setString(2, "CHARTER");
                    ps2.setLong(3, 1000);
                    ps2.setLong(4, 0);
                    ps2.setLong(5, 0);
                    ps2.setString(6, "테스트 아파트");
                    ps2.setString(7, "OWNER");
                    ps2.setString(8, "APARTMENT");
                    ps2.setObject(9, LocalDateTime.now());
                    ps2.setString(10, "테스트 정보");
                    ps2.setString(11, "자세한 테스트 정보");
                    ps2.setString(12, "테스트 연락처");
                    ps2.setString(13, "010-0000-0000");
                    ps2.setString(14, "주차장");
                    ps2.setString(15, "testMemberId");
                    ps2.setLong(16, complexId);
                    ps2.executeUpdate();
                    ResultSet rs2 = ps2.getGeneratedKeys();
                    if (rs2.next()) {
                        return rs2.getLong(1);
                    }
                }
            }
        }
        return -1L;
    }
}