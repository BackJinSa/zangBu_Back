1. Controller (/api/map/list POST)
클라이언트가 보낸 [MapRequestDto] 리스트를 바디로 받아서
mapService.locate(...) 호출

2. Service (MapServiceImpl)
각 MapRequestDto 에서 주소·건물이름 꺼내
CodefClient.lookup() 으로 위도·경도 API 호출
결과 MapLocationVo → MapResponseDto 로 매핑

3. DTOs
요청: { address, buildingName }
응답: { address, lat, lng, buildingName }

4. Utility (CodefClient)
REST template 혹은 WebClient 로 외부 API에 POST
JSON 응답을 MapLocationVo 에 바인딩