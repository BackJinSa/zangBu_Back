package bjs.zangbu.review.controller;

import bjs.zangbu.review.dto.request.ReviewCreateRequest;
import bjs.zangbu.review.dto.response.ReviewCreateResponse;
import bjs.zangbu.review.dto.response.ReviewDetailResponse;
import bjs.zangbu.review.dto.response.ReviewListResponse;
import bjs.zangbu.review.dto.response.ReviewListResult;
import bjs.zangbu.review.exception.ReviewNotFoundException;
import bjs.zangbu.review.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("GET /review/list/{buildingId} 200")
    void list_ok() throws Exception {
        Long buildingId = 100L;
        ReviewListResult result = new ReviewListResult(
                2L,
                List.of(
                        new ReviewListResponse(1L, "nick1", "내용1", 5, "중층", java.time.LocalDateTime.now()),
                        new ReviewListResponse(2L, "nick2", "내용2", 4, "고층", java.time.LocalDateTime.now())),
                false,
                5);

        given(reviewService.listReviews(buildingId, 0, 10)).willReturn(result);

        mockMvc.perform(get("/review/list/{buildingId}", buildingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.reviews[0].reviewId").value(1))
                .andExpect(jsonPath("$.latestReviewRank").value(5));
    }

    @Test
    @DisplayName("GET /review/{reviewId} 200")
    void detail_ok() throws Exception {
        Long reviewId = 10L;
        ReviewDetailResponse dto = new ReviewDetailResponse(
                reviewId, 100L, 200L, "nick", 5, "good", "2024-01-01T12:00:00");
        given(reviewService.getReviewDetail(reviewId)).willReturn(dto);

        mockMvc.perform(get("/review/{reviewId}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(10))
                .andExpect(jsonPath("$.rank").value(5));
    }

    @Test
    @DisplayName("GET /review/{reviewId} 404 when not found")
    void detail_notFound() throws Exception {
        Long reviewId = 99L;
        given(reviewService.getReviewDetail(reviewId)).willThrow(new ReviewNotFoundException(reviewId));

        mockMvc.perform(get("/review/{reviewId}", reviewId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /review 201")
    void create_ok() throws Exception {
        ReviewCreateRequest req = new ReviewCreateRequest();
        setField(req, "buildingId", 100L);
        setField(req, "complexId", 200L);
        setField(req, "floor", "중층");
        setField(req, "rank", 5);
        setField(req, "content", "좋아요");

        ReviewCreateResponse resp = new ReviewCreateResponse(1L, 100L, "닉", "중층", 5, "좋아요", "2024-01-01 12:00:00");
        given(reviewService.createReview(any(ReviewCreateRequest.class), eq("임시 아이디(memberId)"),
                eq("임시 닉네임(nickname)")))
                .willReturn(resp);

        mockMvc.perform(post("/review")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer dummy")
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reviewId").value(1))
                .andExpect(jsonPath("$.buildingId").value(100));
    }

    @Test
    @DisplayName("DELETE /review/{reviewId} 204")
    void delete_ok() throws Exception {
        Long reviewId = 1L;
        mockMvc.perform(delete("/review/{reviewId}", reviewId))
                .andExpect(status().isNoContent());
    }

    // --- test utilities ---
    private static void setField(Object target, String fieldName, Object value) {
        try {
            var f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}