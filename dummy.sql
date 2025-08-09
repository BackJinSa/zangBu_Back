USE zangBu;

-- 1. member (BCrypt 비밀번호 적용)
INSERT INTO member (member_id, email, password, phone, nickname, identity, `role`, birth, name,
                    consent, telecom)
VALUES ('m1', 'user1@example.com', '$2a$10$Dow1JojwZcLL4UByw7dPVus5Cv2OJg2pKhS.ZiK9sZmlOlLp2TWUu',
        '010-1111-1111', '닉네임1', '900101-1234567', 'ROLE_MEMBER', '900101', '홍길동', TRUE, 'KT'),
       ('m2', 'user2@example.com', '$2a$10$Dow1JojwZcLL4UByw7dPVus5Cv2OJg2pKhS.ZiK9sZmlOlLp2TWUu',
        '010-2222-2222', '닉네임2', '920202-1234567', 'ROLE_MEMBER', '920202', '김철수', TRUE, 'SKT'),
       ('m3', 'user3@example.com', '$2a$10$Dow1JojwZcLL4UByw7dPVus5Cv2OJg2pKhS.ZiK9sZmlOlLp2TWUu',
        '010-3333-3333', '닉네임3', '930303-1234567', 'ROLE_ADMIN', '930303', '이영희', FALSE, 'LGU+'),
       ('m4', 'user4@example.com', '$2a$10$Dow1JojwZcLL4UByw7dPVus5Cv2OJg2pKhS.ZiK9sZmlOlLp2TWUu',
        '010-4444-4444', '닉네임4', '940404-1234567', 'ROLE_MEMBER', '940404', '박민수', TRUE, 'KT'),
       ('m5', 'user5@example.com', '$2a$10$Dow1JojwZcLL4UByw7dPVus5Cv2OJg2pKhS.ZiK9sZmlOlLp2TWUu',
        '010-5555-5555', '닉네임5', '950505-1234567', 'ROLE_MEMBER', '950505', '최지우', FALSE, 'SKT'),
       ('m6', 'user6@example.com', '$2a$10$Dow1JojwZcLL4UByw7dPVus5Cv2OJg2pKhS.ZiK9sZmlOlLp2TWUu',
        '010-6666-6666', '닉네임6', '960606-1234567', 'ROLE_MEMBER', '960606', '강호동', TRUE, 'LGU+'),
       ('m7', 'user7@example.com', '$2a$10$Dow1JojwZcLL4UByw7dPVus5Cv2OJg2pKhS.ZiK9sZmlOlLp2TWUu',
        '010-7777-7777', '닉네임7', '970707-1234567', 'ROLE_MEMBER', '970707', '유재석', TRUE, 'KT'),
       ('m8', 'user8@example.com', '$2a$10$Dow1JojwZcLL4UByw7dPVus5Cv2OJg2pKhS.ZiK9sZmlOlLp2TWUu',
        '010-8888-8888', '닉네임8', '980808-1234567', 'ROLE_MEMBER', '980808', '정형돈', FALSE, 'SKT'),
       ('m9', 'user9@example.com', '$2a$10$Dow1JojwZcLL4UByw7dPVus5Cv2OJg2pKhS.ZiK9sZmlOlLp2TWUu',
        '010-9999-9999', '닉네임9', '990909-1234567', 'ROLE_MEMBER', '990909', '하하', TRUE, 'LGU+'),
       ('m10', 'user10@example.com', '$2a$10$Dow1JojwZcLL4UByw7dPVus5Cv2OJg2pKhS.ZiK9sZmlOlLp2TWUu',
        '010-1010-1010', '닉네임10', '000101-1234567', 'ROLE_MEMBER', '000101', '김나영', TRUE, 'KT');

-- 2. complex_list (단지 정보)
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

-- 3. building (매물)
INSERT INTO building (member_id, complex_id, seller_nickname, sale_type, price, deposit,
                      bookmark_count, building_name, seller_type, property_type, move_date,
                      info_oneline, info_building, contact_name, contact_phone, facility)
VALUES ('m1', 1, '홍길동', 'TRADING', 80000, 0, 2, '래미안 101동', 'OWNER', 'APARTMENT', '2025-09-01',
        '강남 최고의 입지', '강남 한복판 래미안 아파트 매물', '홍길동', '010-0000-0001', '학교, 지하철'),
       ('m2', 2, '김철수', 'CHARTER', 0, 50000, 1, '자이 202동', 'OWNER', 'APARTMENT', '2025-10-01',
        '역세권 전세', '서초역 도보 3분 전세', '김철수', '010-0000-0002', '지하철, 마트'),
       ('m3', 3, '이영희', 'MONTHLY', 50, 5000, 3, '힐스테이트 오피스텔', 'TENANT', 'OFFICETEL', '2025-08-20',
        '풀옵션 원룸', '풀옵션 원룸 월세', '이영희', '010-0000-0003', '편의점, 버스정류장'),
       ('m4', 4, '박민수', 'TRADING', 60000, 0, 0, '한양빌라 101호', 'OWNER', 'VILLA', '2025-09-15',
        '저렴한 빌라 매물', '마포구 빌라 매물', '박민수', '010-0000-0004', '공원, 마트'),
       ('m5', 5, '최지우', 'CHARTER', 0, 30000, 5, '개포주택 단독', 'OWNER', 'HOUSE', '2025-08-25',
        '단독 주택 전세', '개포동 단독주택 전세', '최지우', '010-0000-0005', '학교, 시장'),
       ('m6', 6, '강호동', 'MONTHLY', 30, 2000, 2, '푸르지오 101동', 'TENANT', 'APARTMENT', '2025-08-30',
        '관악구 월세', '관악구 신림동 월세', '강호동', '010-0000-0006', '마트, 병원'),
       ('m7', 7, '유재석', 'TRADING', 90000, 0, 7, '아이파크 101동', 'OWNER', 'APARTMENT', '2025-11-01',
        '럭셔리 아파트', '용산 아이파크 매매', '유재석', '010-0000-0007', '백화점, 지하철'),
       ('m8', 8, '정형돈', 'CHARTER', 0, 40000, 4, '롯데캐슬 101동', 'OWNER', 'APARTMENT', '2025-08-28',
        '노원구 전세', '노원구 전세 매물', '정형돈', '010-0000-0008', '버스정류장, 학교'),
       ('m9', 9, '하하', 'MONTHLY', 45, 3000, 6, '두산위브 101동', 'TENANT', 'OFFICETEL', '2025-09-05',
        '오피스텔 월세', '동작구 오피스텔 월세', '하하', '010-0000-0009', '지하철, 편의점'),
       ('m10', 10, '김나영', 'TRADING', 70000, 0, 8, '한신휴 101동', 'OWNER', 'APARTMENT', '2025-12-01',
        '은평구 신축', '불광동 신축 아파트', '김나영', '010-0000-0010', '병원, 마트');

-- 4. payment
INSERT INTO payment (member_id, token, membership_date)
VALUES ('m1', 100, '2025-08-01'),
       ('m2', 150, '2025-08-02'),
       ('m3', 200, '2025-08-03'),
       ('m4', 120, '2025-08-04'),
       ('m5', 180, '2025-08-05'),
       ('m6', 300, '2025-08-06'),
       ('m7', 80, '2025-08-07'),
       ('m8', 90, '2025-08-08'),
       ('m9', 160, '2025-08-09'),
       ('m10', 250, '2025-08-10');

-- 5. fcm_tokens
INSERT INTO fcm_tokens (member_id, token, device_type, device_name)
VALUES ('m1', 'token1', 'Android', '갤럭시S22'),
       ('m2', 'token2', 'iOS', '아이폰14'),
       ('m3', 'token3', 'Android', '갤럭시S21'),
       ('m4', 'token4', 'iOS', '아이폰13'),
       ('m5', 'token5', 'Android', '갤럭시노트20'),
       ('m6', 'token6', 'iOS', '아이폰12'),
       ('m7', 'token7', 'Android', '갤럭시A52'),
       ('m8', 'token8', 'iOS', '아이폰11'),
       ('m9', 'token9', 'Android', '갤럭시Z폴드'),
       ('m10', 'token10', 'iOS', '아이폰SE');

-- 6. address_change
INSERT INTO address_change (member_id, res_number, res_user_addr, res_move_in_date)
VALUES ('m1', '101', '서울 강남구', '2025-07-01'),
       ('m2', '202', '서울 서초구', '2025-07-02'),
       ('m3', '303', '서울 송파구', '2025-07-03'),
       ('m4', '404', '서울 마포구', '2025-07-04'),
       ('m5', '505', '서울 강남구', '2025-07-05'),
       ('m6', '606', '서울 관악구', '2025-07-06'),
       ('m7', '707', '서울 용산구', '2025-07-07'),
       ('m8', '808', '서울 노원구', '2025-07-08'),
       ('m9', '909', '서울 동작구', '2025-07-09'),
       ('m10', '1010', '서울 은평구', '2025-07-10');

-- 7. tax_payment_certificate
INSERT INTO tax_payment_certificate (member_id, issue_no, issue_date, start_month, end_month,
                                     issuing_office, receipt_no, department_name, phone_no,
                                     transaction_id)
VALUES ('m1', 'IN001', '2025-06-01', '202501', '202506', '강남세무서', 'RC001', '과세팀', '02-0001-0001',
        'TXN001'),
       ('m2', 'IN002', '2025-06-02', '202501', '202506', '서초세무서', 'RC002', '부과팀', '02-0002-0002',
        'TXN002'),
       ('m3', 'IN003', '2025-06-03', '202501', '202506', '송파세무서', 'RC003', '징수팀', '02-0003-0003',
        'TXN003'),
       ('m4', 'IN004', '2025-06-04', '202501', '202506', '마포세무서', 'RC004', '체납팀', '02-0004-0004',
        'TXN004'),
       ('m5', 'IN005', '2025-06-05', '202501', '202506', '강남세무서', 'RC005', '조사팀', '02-0005-0005',
        'TXN005'),
       ('m6', 'IN006', '2025-06-06', '202501', '202506', '관악세무서', 'RC006', '부과팀', '02-0006-0006',
        'TXN006'),
       ('m7', 'IN007', '2025-06-07', '202501', '202506', '용산세무서', 'RC007', '징수팀', '02-0007-0007',
        'TXN007'),
       ('m8', 'IN008', '2025-06-08', '202501', '202506', '노원세무서', 'RC008', '체납팀', '02-0008-0008',
        'TXN008'),
       ('m9', 'IN009', '2025-06-09', '202501', '202506', '동작세무서', 'RC009', '조사팀', '02-0009-0009',
        'TXN009'),
       ('m10', 'IN010', '2025-06-10', '202501', '202506', '은평세무서', 'RC010', '과세팀', '02-0010-0010',
        'TXN010');

-- 8. chat_room
INSERT INTO chat_room (chat_room_id, building_id, member_id, complex_id, seller_nickname,
                       consumer_nickname, seller_visible, consumer_visible)
VALUES ('cr1', 1, 'm1', 1, '홍길동', '김철수', TRUE, TRUE),
       ('cr2', 2, 'm2', 2, '김철수', '이영희', TRUE, TRUE),
       ('cr3', 3, 'm3', 3, '이영희', '박민수', TRUE, TRUE),
       ('cr4', 4, 'm4', 4, '박민수', '최지우', TRUE, TRUE),
       ('cr5', 5, 'm5', 5, '최지우', '강호동', TRUE, TRUE),
       ('cr6', 6, 'm6', 6, '강호동', '유재석', TRUE, TRUE),
       ('cr7', 7, 'm7', 7, '유재석', '정형돈', TRUE, TRUE),
       ('cr8', 8, 'm8', 8, '정형돈', '하하', TRUE, TRUE),
       ('cr9', 9, 'm9', 9, '하하', '김나영', TRUE, TRUE),
       ('cr10', 10, 'm10', 10, '김나영', '홍길동', TRUE, TRUE);

-- 9. image_list
INSERT INTO image_list (member_id, complex_id, building_id, image_url)
VALUES ('m1', 1, 1, 'https://example.com/img1.jpg'),
       ('m2', 2, 2, 'https://example.com/img2.jpg'),
       ('m3', 3, 3, 'https://example.com/img3.jpg'),
       ('m4', 4, 4, 'https://example.com/img4.jpg'),
       ('m5', 5, 5, 'https://example.com/img5.jpg'),
       ('m6', 6, 6, 'https://example.com/img6.jpg'),
       ('m7', 7, 7, 'https://example.com/img7.jpg'),
       ('m8', 8, 8, 'https://example.com/img8.jpg'),
       ('m9', 9, 9, 'https://example.com/img9.jpg'),
       ('m10', 10, 10, 'https://example.com/img10.jpg');

-- 10. review
INSERT INTO review (building_id, member_id, complex_id, reviewer_nickname, `rank`, content)
VALUES (1, 'm1', 1, '홍길동', 5, '정말 좋은 집이에요.'),
       (2, 'm2', 2, '김철수', 4, '위치가 좋아요.'),
       (3, 'm3', 3, '이영희', 3, '가격이 조금 비싸요.'),
       (4, 'm4', 4, '박민수', 5, '조용하고 좋아요.'),
       (5, 'm5', 5, '최지우', 4, '주변이 깨끗해요.'),
       (6, 'm6', 6, '강호동', 2, '주차가 불편해요.'),
       (7, 'm7', 7, '유재석', 5, '뷰가 정말 멋져요.'),
       (8, 'm8', 8, '정형돈', 4, '넓고 좋아요.'),
       (9, 'm9', 9, '하하', 3, '교통이 조금 불편해요.'),
       (10, 'm10', 10, '김나영', 5, '신축이라 깨끗합니다.');

-- 11. notification
INSERT INTO notification (member_id, building_id, message, is_read, `type`, sale_type, price,
                          address, `rank`)
VALUES ('m1', 1, '새 매물이 등록되었습니다.', FALSE, 'BUILDING', 'TRADING', 80000, '강남구 역삼동', 5),
       ('m2', 2, '전세 매물이 갱신되었습니다.', TRUE, 'BUILDING', 'CHARTER', 0, '서초구 서초동', 4),
       ('m3', 3, '월세 매물이 등록되었습니다.', FALSE, 'BUILDING', 'MONTHLY', 50, '송파구 잠실동', 3),
       ('m4', 4, '빌라 매매 매물이 있습니다.', TRUE, 'BUILDING', 'TRADING', 60000, '마포구 망원동', 5),
       ('m5', 5, '주택 전세 매물이 있습니다.', FALSE, 'BUILDING', 'CHARTER', 0, '강남구 개포동', 4),
       ('m6', 6, '관악구 월세 매물이 있습니다.', TRUE, 'BUILDING', 'MONTHLY', 30, '관악구 신림동', 2),
       ('m7', 7, '아이파크 매매 매물이 있습니다.', FALSE, 'BUILDING', 'TRADING', 90000, '용산구 한남동', 5),
       ('m8', 8, '롯데캐슬 전세 매물이 있습니다.', TRUE, 'BUILDING', 'CHARTER', 0, '노원구 상계동', 4),
       ('m9', 9, '오피스텔 월세 매물이 있습니다.', FALSE, 'BUILDING', 'MONTHLY', 45, '동작구 흑석동', 3),
       ('m10', 10, '한신휴 매매 매물이 있습니다.', TRUE, 'BUILDING', 'TRADING', 70000, '은평구 불광동', 5);

-- 12. bookmark
INSERT INTO bookmark (building_id, member_id, complex_id, price)
VALUES (1, 'm1', 1, 80000),
       (2, 'm2', 2, 0),
       (3, 'm3', 3, 50),
       (4, 'm4', 4, 60000),
       (5, 'm5', 5, 0),
       (6, 'm6', 6, 30),
       (7, 'm7', 7, 90000),
       (8, 'm8', 8, 0),
       (9, 'm9', 9, 45),
       (10, 'm10', 10, 70000);

-- 13. document_report
INSERT INTO document_report (building_id, member_id, complex_id, comm_unique_no, deal_amount,
                             deposit, monthly_rent, priority_debt, deposit_price,
                             final_auction_price, remaining_deposit, res_user_nm, is_trustee,
                             trust_type)
VALUES (1, 'm1', 1, 'CU001', 80000, 0, 0, 0, 0, 0, 0, '홍길동', FALSE, ''),
       (2, 'm2', 2, 'CU002', 0, 50000, 0, 0, 0, 0, 0, '김철수', FALSE, ''),
       (3, 'm3', 3, 'CU003', 0, 5000, 50, 0, 0, 0, 0, '이영희', FALSE, ''),
       (4, 'm4', 4, 'CU004', 60000, 0, 0, 0, 0, 0, 0, '박민수', FALSE, ''),
       (5, 'm5', 5, 'CU005', 0, 30000, 0, 0, 0, 0, 0, '최지우', FALSE, ''),
       (6, 'm6', 6, 'CU006', 0, 2000, 30, 0, 0, 0, 0, '강호동', FALSE, ''),
       (7, 'm7', 7, 'CU007', 90000, 0, 0, 0, 0, 0, 0, '유재석', FALSE, ''),
       (8, 'm8', 8, 'CU008', 0, 40000, 0, 0, 0, 0, 0, '정형돈', FALSE, ''),
       (9, 'm9', 9, 'CU009', 0, 3000, 45, 0, 0, 0, 0, '하하', FALSE, ''),
       (10, 'm10', 10, 'CU010', 70000, 0, 0, 0, 0, 0, 0, '김나영', FALSE, '');

-- 14. deal
INSERT INTO deal (chat_room_id, building_id, member_id, complex_id, status)
VALUES ('cr1', 1, 'm1', 1, 'BEFORE_TRANSACTION'),
       ('cr2', 2, 'm2', 2, 'BEFORE_OWNER'),
       ('cr3', 3, 'm3', 3, 'BEFORE_CONSUMER'),
       ('cr4', 4, 'm4', 4, 'MIDDLE_DEAL'),
       ('cr5', 5, 'm5', 5, 'CLOSE_DEAL'),
       ('cr6', 6, 'm6', 6, 'BEFORE_TRANSACTION'),
       ('cr7', 7, 'm7', 7, 'BEFORE_OWNER'),
       ('cr8', 8, 'm8', 8, 'BEFORE_CONSUMER'),
       ('cr9', 9, 'm9', 9, 'MIDDLE_DEAL'),
       ('cr10', 10, 'm10', 10, 'CLOSE_DEAL');

-- 15. chat_message
INSERT INTO chat_message (chat_room_id, building_id, member_id, complex_id, message, is_read)
VALUES ('cr1', 1, 'm1', 1, '안녕하세요, 매물 문의드립니다.', FALSE),
       ('cr2', 2, 'm2', 2, '전세 가격이 어떻게 되나요?', TRUE),
       ('cr3', 3, 'm3', 3, '월세 조건이 궁금합니다.', FALSE),
       ('cr4', 4, 'm4', 4, '빌라 내부 사진이 있나요?', TRUE),
       ('cr5', 5, 'm5', 5, '입주일이 언제인가요?', FALSE),
       ('cr6', 6, 'm6', 6, '월세 보증금 조정 가능할까요?', TRUE),
       ('cr7', 7, 'm7', 7, '매매 대출이 가능한가요?', FALSE),
       ('cr8', 8, 'm8', 8, '전세 계약 조건이 궁금합니다.', TRUE),
       ('cr9', 9, 'm9', 9, '오피스텔 주차 가능하나요?', FALSE),
       ('cr10', 10, 'm10', 10, '신축 아파트인가요?', TRUE);

