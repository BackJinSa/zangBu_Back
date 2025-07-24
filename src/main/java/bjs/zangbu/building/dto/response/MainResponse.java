package bjs.zangbu.building.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class MainResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainPageResponse {
        private String nickName;
        private List<BuildingInfo> topReviewed;
        private List<BuildingInfo> topLiked;
        private List<BuildingInfo> newRooms;

        public static MainPageResponse toDto(String nickName, List<BuildingInfo> topReviewed, List<BuildingInfo> topLiked, List<BuildingInfo> newRooms) {
            return new MainPageResponse(
                    nickName,
                    topReviewed,
                    topLiked,
                    newRooms);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuildingInfo {
        private Long buildingId;
        private Integer price;
        private String buildingName;
        private String imageUrl;
        private Boolean isBookmarked;
    }
}
