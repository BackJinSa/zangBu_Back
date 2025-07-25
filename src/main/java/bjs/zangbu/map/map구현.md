<H1> /map/list 기능 구현 </H1>

1. - [x] Controller (/map/list POST)
클라이언트가 보낸 [MapRequestDto] 리스트를 바디로 받아서
mapService.locate(...) 호출

2. - [x] Service (MapServiceImpl)
각 MapRequestDto 에서 주소·건물이름 꺼내
CodefClient.lookup() 으로 위도·경도 API 호출
결과 MapLocationVo → MapResponseDto 로 매핑

3. - [x] DTOs
요청: { address, buildingName }
응답: { address, latitude, longitude, buildingName }

4. - [x] Utility (CodefClient) 
REST template 혹은 WebClient 로 외부 API에 POST
JSON 응답을 MapLocationVo 에 바인딩


<H1> /map/search 기능 구현 </H1>

1. - [x] Controller (/api/map/search POST)
클라이언트가 보낸 MapSearchRequestDto 바디로 받아서
mapSearchService.search(body) 호출

2. - [x] Service (MapSearchServiceImpl)
request.getQuery() 유효성 검사 (빈 값 예외 처리)
kakaoMapClient.searchByKeyword(query) 호출
반환된 DTO 리스트를 그대로 반환

3. - [x] DTOs
요청: { query: String }
응답:
[
    {
        "placeName"       : "송파더센트레",
        "addressName"     : "서울 송파구 장지동 896",
        "roadAddressName" : "서울 송파구 위례광장로 136",
        "x"               : "127.124846",
        "y"               : "37.477157",
        "placeUrl"        : "http://place.map.kakao.com/11111111"
    }, …
]

4. - [x] Utility (KakaoMapClient)
RestTemplate 또는 WebClient 로
https://dapi.kakao.com/v2/local/search/keyword.json?query={query} 호출
Authorization: KakaoAK {REST_API_KEY} 헤더 세팅
JSON documents 배열을 MapSearchResponseDto 리스트로 매핑