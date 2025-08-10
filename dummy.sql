# USE zangBu;
DELETE
FROM chat_message;
DELETE
FROM deal;
DELETE
FROM document_report;
DELETE
FROM bookmark;
DELETE
FROM notification;
DELETE
FROM review;
DELETE
FROM image_list;
DELETE
FROM chat_room;
DELETE
FROM tax_payment_certificate;
DELETE
FROM address_change;
DELETE
FROM fcm_tokens;
DELETE
FROM payment;
DELETE
FROM building;
DELETE
FROM complex_list;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE chat_message;
TRUNCATE TABLE deal;
TRUNCATE TABLE document_report;
TRUNCATE TABLE bookmark;
TRUNCATE TABLE notification;
TRUNCATE TABLE review;
TRUNCATE TABLE image_list;
TRUNCATE TABLE chat_room;
TRUNCATE TABLE tax_payment_certificate;
TRUNCATE TABLE address_change;
TRUNCATE TABLE fcm_tokens;
TRUNCATE TABLE payment;
TRUNCATE TABLE building;
TRUNCATE TABLE complex_list;
TRUNCATE TABLE member;

-- =========================
-- 1) member (UUID + BCrypt + 암호화 identity)
--  비밀번호 입력: 모두 pass123 (BCrypt 해시 동일)
-- =========================
INSERT INTO member (member_id, email, password, phone, nickname, identity, `role`, birth, name,
                    consent, telecom)
VALUES ('b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 'saranghein@naver.com',
        '$2a$10$liILzm.p0VE/i6tXGJp46.DLuuq37NS4nm8YCUBJrOiS2qNu5C9vi', '010-1111-1111', 'ddd',
        'YjAq7lRsn+1aslwapBiewTpF0yJxQR5SuWtZVKQuaAxKkkjX6tFA8tOP33GOlqYUsR6pXnd2auhi7oWvypzbpbkJUaBj4CqukZb+7TB8gCMSyXdAv+cscczFuEJGQc1gN+s8AxtXgZ0zsBIa5RL/qzZhTzgkWfBob0sIq0CE3Wks/IIeuq7lw9PDGskrJ45aCfI5kXmW0sEbSirmtPlidXPv1mdbGxOGG7BLAkNcYaY15StGlyR3Nor1Qz541sChDExhxWXx3ESDAuFLJ4PnIzodeYpWAFdrEK1TXDpS+1lORH2Daw+k+RbA04DNHreMbLAinYdQq9xLEvxfUTKo7A==',
        'ROLE_MEMBER', '010615', '이해인', 0, 'LGU+'),
       ('a12f7d81-8e34-4d9a-b1e1-2f74e2148c00', 'user1@example.com',
        '$2a$10$liILzm.p0VE/i6tXGJp46.DLuuq37NS4nm8YCUBJrOiS2qNu5C9vi', '010-2222-3333', '닉네임1',
        'AbCdEfGhIjKlMnOpQrStUvWxYz1234567890+/abcdefg=', 'ROLE_MEMBER', '900101', '홍길동', 1, 'KT'),
       ('b21c8e72-5c12-42f9-9d77-4f83e3059d11', 'user2@example.com',
        '$2a$10$liILzm.p0VE/i6tXGJp46.DLuuq37NS4nm8YCUBJrOiS2qNu5C9vi', '010-3333-4444', '닉네임2',
        'XyZaBcDeFgHiJkLmNoPqRsTuVwYz0987654321+/hijklmn=', 'ROLE_MEMBER', '920202', '김철수', 1,
        'SKT'),
       ('c34d9f83-7d45-4e0a-a555-5f94f416ae22', 'user3@example.com',
        '$2a$10$liILzm.p0VE/i6tXGJp46.DLuuq37NS4nm8YCUBJrOiS2qNu5C9vi', '010-4444-5555', '닉네임3',
        'QrStUvWxYzAbCdEfGhIjKlMnOp0987654321+/abcdefg=', 'ROLE_ADMIN', '930303', '이영희', 0, 'LGU+'),
       ('d45eaf94-9e56-4f1b-b666-6fa5f527bf33', 'user4@example.com',
        '$2a$10$liILzm.p0VE/i6tXGJp46.DLuuq37NS4nm8YCUBJrOiS2qNu5C9vi', '010-5555-6666', '닉네임4',
        'MnOpQrStUvWxYzAbCdEfGhIjKl0987654321+/abcdefg=', 'ROLE_MEMBER', '940404', '박민수', 1, 'KT'),
       ('e56fb0a5-af67-402c-c777-7ab6b638ca44', 'user5@example.com',
        '$2a$10$liILzm.p0VE/i6tXGJp46.DLuuq37NS4nm8YCUBJrOiS2qNu5C9vi', '010-6666-7777', '닉네임5',
        'EfGhIjKlMnOpQrStUvWxYzAbCd0987654321+/abcdefg=', 'ROLE_MEMBER', '950505', '최지우', 0, 'SKT'),
       ('f67ac1b6-b078-4a3d-d888-8ac7b749da55', 'user6@example.com',
        '$2a$10$liILzm.p0VE/i6tXGJp46.DLuuq37NS4nm8YCUBJrOiS2qNu5C9vi', '010-7777-8888', '닉네임6',
        'KlMnOpQrStUvWxYzAbCdEfGhIj0987654321+/abcdefg=', 'ROLE_MEMBER', '960606', '강호동', 1,
        'LGU+'),
       ('078bd2c7-c189-4b4e-a999-9bd8c850ea66', 'user7@example.com',
        '$2a$10$liILzm.p0VE/i6tXGJp46.DLuuq37NS4nm8YCUBJrOiS2qNu5C9vi', '010-8888-9999', '닉네임7',
        'UvWxYzAbCdEfGhIjKlMnOpQrSt0987654321+/abcdefg=', 'ROLE_MEMBER', '970707', '유재석', 1, 'KT'),
       ('189ce3d8-d290-4c5f-b000-0ce9d961fb77', 'user8@example.com',
        '$2a$10$liILzm.p0VE/i6tXGJp46.DLuuq37NS4nm8YCUBJrOiS2qNu5C9vi', '010-9999-0000', '닉네임8',
        'AbCdEfGhIjKlMnOpQrStUvWxYz0987654321+/abcdefg=', 'ROLE_MEMBER', '980808', '정형돈', 0, 'SKT'),
       ('29adf4e9-e301-4d6a-a111-1df0a0720c88', 'user9@example.com',
        '$2a$10$liILzm.p0VE/i6tXGJp46.DLuuq37NS4nm8YCUBJrOiS2qNu5C9vi', '010-0000-1111', '닉네임9',
        'XyZaBcDeFgHiJkLmNoPqRsTuVwYz0987654321+/abcdefg=', 'ROLE_MEMBER', '990909', '하하', 1,
        'LGU+'),
       ('39bef5fa-f412-4e7b-b222-2ef1b1831d99', 'user10@example.com',
        '$2a$10$liILzm.p0VE/i6tXGJp46.DLuuq37NS4nm8YCUBJrOiS2qNu5C9vi', '010-1111-2222', '닉네임10',
        'QrStUvWxYzAbCdEfGhIjKlMnOp0987654321+/abcdefg=', 'ROLE_MEMBER', '000101', '김나영', 1, 'KT');

-- =========================
-- 2) complex_list (1..10 자동 PK)
-- =========================
INSERT INTO complex_list (res_type, complex_name, complex_no, sido, sigungu, si_code, eupmyeondong,
                          transaction_id, address, zonecode, building_name, bname, dong, ho,
                          roadName)
VALUES ('아파트', '래미안1차', 101, '서울시', '강남구', '1100', '역삼동', 'tx001', '서울 강남구 역삼동 1-1', '06232',
        '래미안1차', '역삼동', '101', '101', '테헤란로'),
       ('아파트', '자이2차', 102, '서울시', '서초구', '1101', '서초동', 'tx002', '서울 서초구 서초동 2-2', '06512', '자이2차',
        '서초동', '102', '202', '강남대로'),
       ('오피스텔', '힐스테이트', 103, '서울시', '송파구', '1102', '잠실동', 'tx003', '서울 송파구 잠실동 3-3', '05555',
        '힐스테이트', '잠실동', '103', '303', '올림픽로'),
       ('빌라', '한양빌라', 104, '서울시', '마포구', '1103', '망원동', 'tx004', '서울 마포구 망원동 4-4', '04044', '한양빌라',
        '망원동', '104', '404', '망원로'),
       ('주택', '개포주택', 105, '서울시', '강남구', '1100', '개포동', 'tx005', '서울 강남구 개포동 5-5', '06363', '개포주택',
        '개포동', '105', '505', '개포로'),
       ('아파트', '푸르지오', 106, '서울시', '관악구', '1104', '신림동', 'tx006', '서울 관악구 신림동 6-6', '08888', '푸르지오',
        '신림동', '106', '606', '관악로'),
       ('아파트', '아이파크', 107, '서울시', '용산구', '1105', '한남동', 'tx007', '서울 용산구 한남동 7-7', '04444', '아이파크',
        '한남동', '107', '707', '이태원로'),
       ('아파트', '롯데캐슬', 108, '서울시', '노원구', '1106', '상계동', 'tx008', '서울 노원구 상계동 8-8', '01666', '롯데캐슬',
        '상계동', '108', '808', '노원로'),
       ('오피스텔', '두산위브', 109, '서울시', '동작구', '1107', '흑석동', 'tx009', '서울 동작구 흑석동 9-9', '06969',
        '두산위브', '흑석동', '109', '909', '동작대로'),
       ('아파트', '한신휴', 110, '서울시', '은평구', '1108', '불광동', 'tx010', '서울 은평구 불광동 10-10', '03333', '한신휴',
        '불광동', '110', '1010', '은평로');

-- =========================
-- 3) building (1..10 자동 PK)  member_id는 위 UUID 매핑
-- =========================
INSERT INTO building (member_id, complex_id, seller_nickname, sale_type, price, deposit,
                      bookmark_count, created_at, building_name, seller_type, property_type,
                      move_date, info_oneline, info_building, contact_name, contact_phone, facility)
VALUES ('b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 1, '홍길동', 'TRADING', 80000, 0, 2, NOW(), '래미안 101동',
        'OWNER', 'APARTMENT', '2025-09-01', '강남 최고의 입지', '강남 한복판 래미안 아파트 매물', '홍길동',
        '010-0000-0001', '학교, 지하철'),
       ('a12f7d81-8e34-4d9a-b1e1-2f74e2148c00', 2, '김철수', 'CHARTER', 0, 50000, 1, NOW(), '자이 202동',
        'OWNER', 'APARTMENT', '2025-10-01', '역세권 전세', '서초역 도보 3분 전세', '김철수', '010-0000-0002',
        '지하철, 마트'),
       ('b21c8e72-5c12-42f9-9d77-4f83e3059d11', 3, '이영희', 'MONTHLY', 50, 5000, 3, NOW(),
        '힐스테이트 오피스텔', 'TENANT', 'OFFICETEL', '2025-08-20', '풀옵션 원룸', '풀옵션 원룸 월세', '이영희',
        '010-0000-0003', '편의점, 버스정류장'),
       ('c34d9f83-7d45-4e0a-a555-5f94f416ae22', 4, '박민수', 'TRADING', 60000, 0, 0, NOW(),
        '한양빌라 101호', 'OWNER', 'VILLA', '2025-09-15', '저렴한 빌라 매물', '마포구 빌라 매물', '박민수',
        '010-0000-0004', '공원, 마트'),
       ('d45eaf94-9e56-4f1b-b666-6fa5f527bf33', 5, '최지우', 'CHARTER', 0, 30000, 5, NOW(), '개포주택 단독',
        'OWNER', 'HOUSE', '2025-08-25', '단독 주택 전세', '개포동 단독주택 전세', '최지우', '010-0000-0005',
        '학교, 시장'),
       ('e56fb0a5-af67-402c-c777-7ab6b638ca44', 6, '강호동', 'MONTHLY', 30, 2000, 2, NOW(),
        '푸르지오 101동', 'TENANT', 'APARTMENT', '2025-08-30', '관악구 월세', '관악구 신림동 월세', '강호동',
        '010-0000-0006', '마트, 병원'),
       ('f67ac1b6-b078-4a3d-d888-8ac7b749da55', 7, '유재석', 'TRADING', 90000, 0, 7, NOW(),
        '아이파크 101동', 'OWNER', 'APARTMENT', '2025-11-01', '럭셔리 아파트', '용산 아이파크 매매', '유재석',
        '010-0000-0007', '백화점, 지하철'),
       ('078bd2c7-c189-4b4e-a999-9bd8c850ea66', 8, '정형돈', 'CHARTER', 0, 40000, 4, NOW(),
        '롯데캐슬 101동', 'OWNER', 'APARTMENT', '2025-08-28', '노원구 전세', '노원구 전세 매물', '정형돈',
        '010-0000-0008', '버스정류장, 학교'),
       ('189ce3d8-d290-4c5f-b000-0ce9d961fb77', 9, '하하', 'MONTHLY', 45, 3000, 6, NOW(), '두산위브 101동',
        'TENANT', 'OFFICETEL', '2025-09-05', '오피스텔 월세', '동작구 오피스텔 월세', '하하', '010-0000-0009',
        '지하철, 편의점'),
       ('29adf4e9-e301-4d6a-a111-1df0a0720c88', 10, '김나영', 'TRADING', 70000, 0, 8, NOW(),
        '한신휴 101동', 'OWNER', 'APARTMENT', '2025-12-01', '은평구 신축', '불광동 신축 아파트', '김나영',
        '010-0000-0010', '병원, 마트');

-- =========================
-- 4) payment
-- =========================
INSERT INTO payment (member_id, token, membership_date)
VALUES ('b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 100, '2025-08-01'),
       ('a12f7d81-8e34-4d9a-b1e1-2f74e2148c00', 150, '2025-08-02'),
       ('b21c8e72-5c12-42f9-9d77-4f83e3059d11', 200, '2025-08-03'),
       ('c34d9f83-7d45-4e0a-a555-5f94f416ae22', 120, '2025-08-04'),
       ('d45eaf94-9e56-4f1b-b666-6fa5f527bf33', 180, '2025-08-05'),
       ('e56fb0a5-af67-402c-c777-7ab6b638ca44', 300, '2025-08-06'),
       ('f67ac1b6-b078-4a3d-d888-8ac7b749da55', 80, '2025-08-07'),
       ('078bd2c7-c189-4b4e-a999-9bd8c850ea66', 90, '2025-08-08'),
       ('189ce3d8-d290-4c5f-b000-0ce9d961fb77', 160, '2025-08-09'),
       ('29adf4e9-e301-4d6a-a111-1df0a0720c88', 250, '2025-08-10');

-- =========================
-- 5) fcm_tokens
-- =========================
INSERT INTO fcm_tokens (member_id, token, device_type, device_name)
VALUES ('b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 'token1', 'Android', '갤럭시S22'),
       ('a12f7d81-8e34-4d9a-b1e1-2f74e2148c00', 'token2', 'iOS', '아이폰14'),
       ('b21c8e72-5c12-42f9-9d77-4f83e3059d11', 'token3', 'Android', '갤럭시S21'),
       ('c34d9f83-7d45-4e0a-a555-5f94f416ae22', 'token4', 'iOS', '아이폰13'),
       ('d45eaf94-9e56-4f1b-b666-6fa5f527bf33', 'token5', 'Android', '갤럭시노트20'),
       ('e56fb0a5-af67-402c-c777-7ab6b638ca44', 'token6', 'iOS', '아이폰12'),
       ('f67ac1b6-b078-4a3d-d888-8ac7b749da55', 'token7', 'Android', '갤럭시A52'),
       ('078bd2c7-c189-4b4e-a999-9bd8c850ea66', 'token8', 'iOS', '아이폰11'),
       ('189ce3d8-d290-4c5f-b000-0ce9d961fb77', 'token9', 'Android', '갤럭시Z폴드'),
       ('29adf4e9-e301-4d6a-a111-1df0a0720c88', 'token10', 'iOS', '아이폰SE');

-- =========================
-- 6) address_change
-- =========================
INSERT INTO address_change (member_id, res_number, res_user_addr, res_move_in_date)
VALUES ('b93b7c61-fb18-4eab-abe0-13a4aa4722bd', '101', '서울 강남구', '2025-07-01'),
       ('a12f7d81-8e34-4d9a-b1e1-2f74e2148c00', '202', '서울 서초구', '2025-07-02'),
       ('b21c8e72-5c12-42f9-9d77-4f83e3059d11', '303', '서울 송파구', '2025-07-03'),
       ('c34d9f83-7d45-4e0a-a555-5f94f416ae22', '404', '서울 마포구', '2025-07-04'),
       ('d45eaf94-9e56-4f1b-b666-6fa5f527bf33', '505', '서울 강남구', '2025-07-05'),
       ('e56fb0a5-af67-402c-c777-7ab6b638ca44', '606', '서울 관악구', '2025-07-06'),
       ('f67ac1b6-b078-4a3d-d888-8ac7b749da55', '707', '서울 용산구', '2025-07-07'),
       ('078bd2c7-c189-4b4e-a999-9bd8c850ea66', '808', '서울 노원구', '2025-07-08'),
       ('189ce3d8-d290-4c5f-b000-0ce9d961fb77', '909', '서울 동작구', '2025-07-09'),
       ('29adf4e9-e301-4d6a-a111-1df0a0720c88', '1010', '서울 은평구', '2025-07-10');

-- =========================
-- 7) tax_payment_certificate
-- =========================
INSERT INTO tax_payment_certificate (member_id, issue_no, issue_date, start_month, end_month,
                                     issuing_office, receipt_no, department_name, phone_no,
                                     transaction_id)
VALUES ('b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 'IN001', '2025-06-01', '202501', '202506', '강남세무서',
        'RC001', '과세팀', '02-0001-0001', 'TXN001'),
       ('a12f7d81-8e34-4d9a-b1e1-2f74e2148c00', 'IN002', '2025-06-02', '202501', '202506', '서초세무서',
        'RC002', '부과팀', '02-0002-0002', 'TXN002'),
       ('b21c8e72-5c12-42f9-9d77-4f83e3059d11', 'IN003', '2025-06-03', '202501', '202506', '송파세무서',
        'RC003', '징수팀', '02-0003-0003', 'TXN003'),
       ('c34d9f83-7d45-4e0a-a555-5f94f416ae22', 'IN004', '2025-06-04', '202501', '202506', '마포세무서',
        'RC004', '체납팀', '02-0004-0004', 'TXN004'),
       ('d45eaf94-9e56-4f1b-b666-6fa5f527bf33', 'IN005', '2025-06-05', '202501', '202506', '강남세무서',
        'RC005', '조사팀', '02-0005-0005', 'TXN005'),
       ('e56fb0a5-af67-402c-c777-7ab6b638ca44', 'IN006', '2025-06-06', '202501', '202506', '관악세무서',
        'RC006', '부과팀', '02-0006-0006', 'TXN006'),
       ('f67ac1b6-b078-4a3d-d888-8ac7b749da55', 'IN007', '2025-06-07', '202501', '202506', '용산세무서',
        'RC007', '징수팀', '02-0007-0007', 'TXN007'),
       ('078bd2c7-c189-4b4e-a999-9bd8c850ea66', 'IN008', '2025-06-08', '202501', '202506', '노원세무서',
        'RC008', '체납팀', '02-0008-0008', 'TXN008'),
       ('189ce3d8-d290-4c5f-b000-0ce9d961fb77', 'IN009', '2025-06-09', '202501', '202506', '동작세무서',
        'RC009', '조사팀', '02-0009-0009', 'TXN009'),
       ('29adf4e9-e301-4d6a-a111-1df0a0720c88', 'IN010', '2025-06-10', '202501', '202506', '은평세무서',
        'RC010', '과세팀', '02-0010-0010', 'TXN010');

-- =========================
-- 8) chat_room (PK: VARCHAR(36)) 간단 id 사용
-- =========================
INSERT INTO chat_room (chat_room_id, building_id, member_id, complex_id, seller_nickname,
                       consumer_nickname, seller_visible, consumer_visible)
VALUES ('cr1', 1, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 1, '홍길동', '김철수', TRUE, TRUE),
       ('cr2', 2, 'a12f7d81-8e34-4d9a-b1e1-2f74e2148c00', 2, '김철수', '이영희', TRUE, TRUE),
       ('cr3', 3, 'b21c8e72-5c12-42f9-9d77-4f83e3059d11', 3, '이영희', '박민수', TRUE, TRUE),
       ('cr4', 4, 'c34d9f83-7d45-4e0a-a555-5f94f416ae22', 4, '박민수', '최지우', TRUE, TRUE),
       ('cr5', 5, 'd45eaf94-9e56-4f1b-b666-6fa5f527bf33', 5, '최지우', '강호동', TRUE, TRUE),
       ('cr6', 6, 'e56fb0a5-af67-402c-c777-7ab6b638ca44', 6, '강호동', '유재석', TRUE, TRUE),
       ('cr7', 7, 'f67ac1b6-b078-4a3d-d888-8ac7b749da55', 7, '유재석', '정형돈', TRUE, TRUE),
       ('cr8', 8, '078bd2c7-c189-4b4e-a999-9bd8c850ea66', 8, '정형돈', '하하', TRUE, TRUE),
       ('cr9', 9, '189ce3d8-d290-4c5f-b000-0ce9d961fb77', 9, '하하', '김나영', TRUE, TRUE),
       ('cr10', 10, '29adf4e9-e301-4d6a-a111-1df0a0720c88', 10, '김나영', '홍길동', TRUE, TRUE);

-- =========================
-- 9) image_list
-- =========================
INSERT INTO image_list (member_id, complex_id, building_id, image_url)
VALUES ('b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 1, 1, 'https://example.com/img1.jpg'),
       ('a12f7d81-8e34-4d9a-b1e1-2f74e2148c00', 2, 2, 'https://example.com/img2.jpg'),
       ('b21c8e72-5c12-42f9-9d77-4f83e3059d11', 3, 3, 'https://example.com/img3.jpg'),
       ('c34d9f83-7d45-4e0a-a555-5f94f416ae22', 4, 4, 'https://example.com/img4.jpg'),
       ('d45eaf94-9e56-4f1b-b666-6fa5f527bf33', 5, 5, 'https://example.com/img5.jpg'),
       ('e56fb0a5-af67-402c-c777-7ab6b638ca44', 6, 6, 'https://example.com/img6.jpg'),
       ('f67ac1b6-b078-4a3d-d888-8ac7b749da55', 7, 7, 'https://example.com/img7.jpg'),
       ('078bd2c7-c189-4b4e-a999-9bd8c850ea66', 8, 8, 'https://example.com/img8.jpg'),
       ('189ce3d8-d290-4c5f-b000-0ce9d961fb77', 9, 9, 'https://example.com/img9.jpg'),
       ('29adf4e9-e301-4d6a-a111-1df0a0720c88', 10, 10, 'https://example.com/img10.jpg');

-- =========================
-- 10) review
-- =========================
INSERT INTO review (building_id, member_id, complex_id, reviewer_nickname, `rank`, content)
VALUES (1, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 1, '홍길동', 5, '정말 좋은 집이에요.'),
       (2, 'a12f7d81-8e34-4d9a-b1e1-2f74e2148c00', 2, '김철수', 4, '위치가 좋아요.'),
       (3, 'b21c8e72-5c12-42f9-9d77-4f83e3059d11', 3, '이영희', 3, '가격이 조금 비싸요.'),
       (4, 'c34d9f83-7d45-4e0a-a555-5f94f416ae22', 4, '박민수', 5, '조용하고 좋아요.'),
       (5, 'd45eaf94-9e56-4f1b-b666-6fa5f527bf33', 5, '최지우', 4, '주변이 깨끗해요.'),
       (6, 'e56fb0a5-af67-402c-c777-7ab6b638ca44', 6, '강호동', 2, '주차가 불편해요.'),
       (7, 'f67ac1b6-b078-4a3d-d888-8ac7b749da55', 7, '유재석', 5, '뷰가 정말 멋져요.'),
       (8, '078bd2c7-c189-4b4e-a999-9bd8c850ea66', 8, '정형돈', 4, '넓고 좋아요.'),
       (9, '189ce3d8-d290-4c5f-b000-0ce9d961fb77', 9, '하하', 3, '교통이 조금 불편해요.'),
       (10, '29adf4e9-e301-4d6a-a111-1df0a0720c88', 10, '김나영', 5, '신축이라 깨끗합니다.');

-- =========================
-- 11) notification
-- =========================
INSERT INTO notification (member_id, building_id, message, is_read, `type`, created_at, sale_type,
                          price, address, `rank`)
VALUES ('b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 1, '새 매물이 등록되었습니다.', FALSE, 'BUILDING', NOW(),
        'TRADING', 80000, '강남구 역삼동', 5),
       ('a12f7d81-8e34-4d9a-b1e1-2f74e2148c00', 2, '전세 매물이 갱신되었습니다.', TRUE, 'BUILDING', NOW(),
        'CHARTER', 0, '서초구 서초동', 4),
       ('b21c8e72-5c12-42f9-9d77-4f83e3059d11', 3, '월세 매물이 등록되었습니다.', FALSE, 'BUILDING', NOW(),
        'MONTHLY', 50, '송파구 잠실동', 3),
       ('c34d9f83-7d45-4e0a-a555-5f94f416ae22', 4, '빌라 매매 매물이 있습니다.', TRUE, 'BUILDING', NOW(),
        'TRADING', 60000, '마포구 망원동', 5),
       ('d45eaf94-9e56-4f1b-b666-6fa5f527bf33', 5, '주택 전세 매물이 있습니다.', FALSE, 'BUILDING', NOW(),
        'CHARTER', 0, '강남구 개포동', 4),
       ('e56fb0a5-af67-402c-c777-7ab6b638ca44', 6, '관악구 월세 매물이 있습니다.', TRUE, 'BUILDING', NOW(),
        'MONTHLY', 30, '관악구 신림동', 2),
       ('f67ac1b6-b078-4a3d-d888-8ac7b749da55', 7, '아이파크 매매 매물이 있습니다.', FALSE, 'BUILDING', NOW(),
        'TRADING', 90000, '용산구 한남동', 5),
       ('078bd2c7-c189-4b4e-a999-9bd8c850ea66', 8, '롯데캐슬 전세 매물이 있습니다.', TRUE, 'BUILDING', NOW(),
        'CHARTER', 0, '노원구 상계동', 4),
       ('189ce3d8-d290-4c5f-b000-0ce9d961fb77', 9, '오피스텔 월세 매물이 있습니다.', FALSE, 'BUILDING', NOW(),
        'MONTHLY', 45, '동작구 흑석동', 3),
       ('29adf4e9-e301-4d6a-a111-1df0a0720c88', 10, '한신휴 매매 매물이 있습니다.', TRUE, 'BUILDING', NOW(),
        'TRADING', 70000, '은평구 불광동', 5);

-- =========================
-- 12) bookmark
-- =========================
INSERT INTO bookmark (building_id, member_id, complex_id, price)
VALUES (1, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 1, 80000),
       (2, 'a12f7d81-8e34-4d9a-b1e1-2f74e2148c00', 2, 0),
       (3, 'b21c8e72-5c12-42f9-9d77-4f83e3059d11', 3, 50),
       (4, 'c34d9f83-7d45-4e0a-a555-5f94f416ae22', 4, 60000),
       (5, 'd45eaf94-9e56-4f1b-b666-6fa5f527bf33', 5, 0),
       (6, 'e56fb0a5-af67-402c-c777-7ab6b638ca44', 6, 30),
       (7, 'f67ac1b6-b078-4a3d-d888-8ac7b749da55', 7, 90000),
       (8, '078bd2c7-c189-4b4e-a999-9bd8c850ea66', 8, 0),
       (9, '189ce3d8-d290-4c5f-b000-0ce9d961fb77', 9, 45),
       (10, '29adf4e9-e301-4d6a-a111-1df0a0720c88', 10, 70000);

-- =========================
-- 13) document_report
-- =========================
INSERT INTO document_report (building_id, member_id, complex_id, comm_unique_no, deal_amount,
                             deposit, monthly_rent, priority_debt, deposit_price,
                             final_auction_price, remaining_deposit, res_user_nm, is_trustee,
                             trust_type, created_at)
VALUES (1, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 1, 'CU001', 80000, 0, 0, 0, 0, 0, 0, '홍길동',
        FALSE, '', NOW()),
       (2, 'a12f7d81-8e34-4d9a-b1e1-2f74e2148c00', 2, 'CU002', 0, 50000, 0, 0, 0, 0, 0, '김철수',
        FALSE, '', NOW()),
       (3, 'b21c8e72-5c12-42f9-9d77-4f83e3059d11', 3, 'CU003', 0, 5000, 50, 0, 0, 0, 0, '이영희',
        FALSE, '', NOW()),
       (4, 'c34d9f83-7d45-4e0a-a555-5f94f416ae22', 4, 'CU004', 60000, 0, 0, 0, 0, 0, 0, '박민수',
        FALSE, '', NOW()),
       (5, 'd45eaf94-9e56-4f1b-b666-6fa5f527bf33', 5, 'CU005', 0, 30000, 0, 0, 0, 0, 0, '최지우',
        FALSE, '', NOW()),
       (6, 'e56fb0a5-af67-402c-c777-7ab6b638ca44', 6, 'CU006', 0, 2000, 30, 0, 0, 0, 0, '강호동',
        FALSE, '', NOW()),
       (7, 'f67ac1b6-b078-4a3d-d888-8ac7b749da55', 7, 'CU007', 90000, 0, 0, 0, 0, 0, 0, '유재석',
        FALSE, '', NOW()),
       (8, '078bd2c7-c189-4b4e-a999-9bd8c850ea66', 8, 'CU008', 0, 40000, 0, 0, 0, 0, 0, '정형돈',
        FALSE, '', NOW()),
       (9, '189ce3d8-d290-4c5f-b000-0ce9d961fb77', 9, 'CU009', 0, 3000, 45, 0, 0, 0, 0, '하하', FALSE,
        '', NOW()),
       (10, '29adf4e9-e301-4d6a-a111-1df0a0720c88', 10, 'CU010', 70000, 0, 0, 0, 0, 0, 0, '김나영',
        FALSE, '', NOW());

-- =========================
-- 14) deal
-- =========================
INSERT INTO deal (chat_room_id, building_id, member_id, complex_id, status, created_at)
VALUES ('cr1', 1, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 1, 'BEFORE_TRANSACTION', NOW()),
       ('cr2', 2, 'a12f7d81-8e34-4d9a-b1e1-2f74e2148c00', 2, 'BEFORE_OWNER', NOW()),
       ('cr3', 3, 'b21c8e72-5c12-42f9-9d77-4f83e3059d11', 3, 'BEFORE_CONSUMER', NOW()),
       ('cr4', 4, 'c34d9f83-7d45-4e0a-a555-5f94f416ae22', 4, 'MIDDLE_DEAL', NOW()),
       ('cr5', 5, 'd45eaf94-9e56-4f1b-b666-6fa5f527bf33', 5, 'CLOSE_DEAL', NOW()),
       ('cr6', 6, 'e56fb0a5-af67-402c-c777-7ab6b638ca44', 6, 'BEFORE_TRANSACTION', NOW()),
       ('cr7', 7, 'f67ac1b6-b078-4a3d-d888-8ac7b749da55', 7, 'BEFORE_OWNER', NOW()),
       ('cr8', 8, '078bd2c7-c189-4b4e-a999-9bd8c850ea66', 8, 'BEFORE_CONSUMER', NOW()),
       ('cr9', 9, '189ce3d8-d290-4c5f-b000-0ce9d961fb77', 9, 'MIDDLE_DEAL', NOW()),
       ('cr10', 10, '29adf4e9-e301-4d6a-a111-1df0a0720c88', 10, 'CLOSE_DEAL', NOW());

-- =========================
-- 15) chat_message
-- =========================
INSERT INTO chat_message (chat_room_id, building_id, member_id, complex_id, created_at, message,
                          is_read)
VALUES ('cr1', 1, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 1, NOW(), '안녕하세요, 매물 문의드립니다.', FALSE),
       ('cr2', 2, 'a12f7d81-8e34-4d9a-b1e1-2f74e2148c00', 2, NOW(), '전세 가격이 어떻게 되나요?', TRUE),
       ('cr3', 3, 'b21c8e72-5c12-42f9-9d77-4f83e3059d11', 3, NOW(), '월세 조건이 궁금합니다.', FALSE),
       ('cr4', 4, 'c34d9f83-7d45-4e0a-a555-5f94f416ae22', 4, NOW(), '빌라 내부 사진이 있나요?', TRUE),
       ('cr5', 5, 'd45eaf94-9e56-4f1b-b666-6fa5f527bf33', 5, NOW(), '입주일이 언제인가요?', FALSE),
       ('cr6', 6, 'e56fb0a5-af67-402c-c777-7ab6b638ca44', 6, NOW(), '월세 보증금 조정 가능할까요?', TRUE),
       ('cr7', 7, 'f67ac1b6-b078-4a3d-d888-8ac7b749da55', 7, NOW(), '매매 대출이 가능한가요?', FALSE),
       ('cr8', 8, '078bd2c7-c189-4b4e-a999-9bd8c850ea66', 8, NOW(), '전세 계약 조건이 궁금합니다.', TRUE),
       ('cr9', 9, '189ce3d8-d290-4c5f-b000-0ce9d961fb77', 9, NOW(), '오피스텔 주차 가능하나요?', FALSE),
       ('cr10', 10, '29adf4e9-e301-4d6a-a111-1df0a0720c88', 10, NOW(), '신축 아파트인가요?', TRUE);


-- 2) complex_list
INSERT INTO complex_list (complex_id, res_type, complex_name, complex_no, sido, sigungu, si_code,
                          eupmyeondong, transaction_id, address, zonecode, building_name, bname,
                          dong, ho, roadName)
VALUES (101, '아파트', '래미안1차', 101, '서울시', '강남구', '1100', '역삼동', 'tx001', '서울 강남구 역삼동 1-1', '06232',
        '래미안1차', '역삼동', '101', '101', '테헤란로');

-- 3) building
INSERT INTO building (building_id, member_id, complex_id, seller_nickname, sale_type, price,
                      deposit, bookmark_count, created_at, building_name, seller_type,
                      property_type, move_date, info_oneline, info_building, contact_name,
                      contact_phone, facility)
VALUES (201, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 101, '홍길동', 'TRADING', 80000, 0, 2, NOW(),
        '래미안 101동', 'OWNER', 'APARTMENT', '2025-09-01', '강남 최고의 입지', '강남 한복판 래미안 아파트 매물', '홍길동',
        '010-0000-0001', '학교, 지하철');

-- 4) payment
INSERT INTO payment (payment_id, member_id, token, membership_date)
VALUES (301, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 100, '2025-08-01');

-- 5) fcm_tokens
INSERT INTO fcm_tokens (fcm_token_id, member_id, token, device_type, device_name)
VALUES (401, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 'token1', 'Android', '갤럭시S22');

-- 6) address_change
INSERT INTO address_change (address_change_id, member_id, res_number, res_user_addr,
                            res_move_in_date)
VALUES (501, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', '101', '서울 강남구', '2025-07-01');

-- 7) tax_payment_certificate
INSERT INTO tax_payment_certificate (tax_payment_certificate_id, member_id, issue_no, issue_date,
                                     start_month, end_month, issuing_office, receipt_no,
                                     department_name, phone_no, transaction_id)
VALUES (601, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 'IN001', '2025-06-01', '202501', '202506',
        '강남세무서', 'RC001', '과세팀', '02-0001-0001', 'TXN001');

-- 8) chat_room
INSERT INTO chat_room (chat_room_id, building_id, member_id, complex_id, seller_nickname,
                       consumer_nickname, seller_visible, consumer_visible)
VALUES ('cr_sarang1', 201, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 101, '홍길동', '김철수', TRUE, TRUE);

-- 9) image_list
INSERT INTO image_list (image_id, member_id, complex_id, building_id, image_url)
VALUES (701, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 101, 201,
        'https://example.com/img_sarang.jpg');

-- 10) review
INSERT INTO review (review_id, building_id, member_id, complex_id, reviewer_nickname, `rank`,
                    content, created_at)
VALUES (801, 201, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 101, '홍길동', 5, '정말 좋은 집이에요.', NOW());

-- 11) notification
INSERT INTO notification (notification_id, member_id, building_id, message, is_read, `type`,
                          created_at, sale_type, price, address, `rank`)
VALUES (901, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 201, '새 매물이 등록되었습니다.', FALSE, 'BUILDING',
        NOW(), 'TRADING', 80000, '강남구 역삼동', 5);

-- 12) bookmark
INSERT INTO bookmark (bookmark_id, building_id, member_id, complex_id, price)
VALUES (1001, 201, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 101, 80000);

-- 13) document_report
INSERT INTO document_report (report_id, building_id, member_id, complex_id, comm_unique_no,
                             deal_amount, deposit, monthly_rent, priority_debt, deposit_price,
                             final_auction_price, remaining_deposit, res_user_nm, is_trustee,
                             trust_type, created_at)
VALUES (1101, 201, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 101, 'CU001', 80000, 0, 0, 0, 0, 0, 0,
        '홍길동', FALSE, '', NOW());

-- 14) deal
INSERT INTO deal (deal_id, chat_room_id, building_id, member_id, complex_id, status, created_at)
VALUES (1201, 'cr_sarang1', 201, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 101, 'BEFORE_TRANSACTION',
        NOW());

-- 15) chat_message
INSERT INTO chat_message (chat_message_id, chat_room_id, building_id, member_id, complex_id,
                          created_at, message, is_read)
VALUES (1301, 'cr_sarang1', 201, 'b93b7c61-fb18-4eab-abe0-13a4aa4722bd', 101, NOW(),
        '안녕하세요, 매물 문의드립니다.', FALSE);