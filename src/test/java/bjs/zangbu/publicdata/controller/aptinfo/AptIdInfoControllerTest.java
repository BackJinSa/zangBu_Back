package bjs.zangbu.publicdata.controller.aptinfo;

import bjs.zangbu.publicdata.dto.aptinfo.AptInfo;
import bjs.zangbu.publicdata.dto.aptinfo.DongInfo;
import bjs.zangbu.publicdata.service.aptinfo.AptIdInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AptIdInfoControllerTest {

    @Mock
    private AptIdInfoService aptIdInfoService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AptIdInfoController(aptIdInfoService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAptInfo_Success() throws Exception {
        // Given
        String address = "서울특별시 용산구 이태원동";
        int page = 1;
        int perPage = 5;

        List<AptInfo> mockAptInfoList = new ArrayList<>();
        AptInfo aptInfo = new AptInfo();
        aptInfo.setAdres("서울특별시 용산구 이태원동 123-45");
        aptInfo.setComplexGbCd("01");
        aptInfo.setComplexNm1("이태원아파트");
        aptInfo.setComplexNm2("101동");
        aptInfo.setComplexNm3("");
        aptInfo.setComplexPk("11350120401804");
        aptInfo.setDongCnt(3);
        aptInfo.setPnu("1111010100");
        aptInfo.setUnitCnt(1200);
        aptInfo.setUseaprDt("2019-12-01");

        mockAptInfoList.add(aptInfo);

        when(aptIdInfoService.fetchAptInfo(anyString(), anyInt(), anyInt()))
                .thenReturn(mockAptInfoList);

        // When & Then
        mockMvc.perform(get("/publicdata/aptidinfo/info")
                .param("adres", address)
                .param("page", String.valueOf(page))
                .param("perPage", String.valueOf(perPage))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].adres").value("서울특별시 용산구 이태원동 123-45"))
                .andExpect(jsonPath("$[0].complexGbCd").value("01"))
                .andExpect(jsonPath("$[0].complexNm1").value("이태원아파트"))
                .andExpect(jsonPath("$[0].complexNm2").value("101동"))
                .andExpect(jsonPath("$[0].complexPk").value("11350120401804"))
                .andExpect(jsonPath("$[0].dongCnt").value(3))
                .andExpect(jsonPath("$[0].unitCnt").value(1200))
                .andExpect(jsonPath("$[0].useaprDt").value("2019-12-01"));
    }

    @Test
    void testGetAptInfo_WithDefaultValues() throws Exception {
        // Given
        String address = "서울특별시 용산구 이태원동";
        List<AptInfo> mockAptInfoList = new ArrayList<>();

        when(aptIdInfoService.fetchAptInfo(anyString(), anyInt(), anyInt()))
                .thenReturn(mockAptInfoList);

        // When & Then
        mockMvc.perform(get("/publicdata/aptidinfo/info")
                .param("adres", address)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetAptInfo_EmptyAddress() throws Exception {
        // Given
        String address = "";
        List<AptInfo> mockAptInfoList = new ArrayList<>();

        when(aptIdInfoService.fetchAptInfo(anyString(), anyInt(), anyInt()))
                .thenReturn(mockAptInfoList);

        // When & Then
        mockMvc.perform(get("/publicdata/aptidinfo/info")
                .param("adres", address)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetDongInfo_Success() throws Exception {
        // Given
        String complexPk = "11350120401804";
        int page = 1;
        int perPage = 5;

        List<DongInfo> mockDongInfoList = new ArrayList<>();
        DongInfo dongInfo = new DongInfo();
        dongInfo.setComplexPk("11350120401804");
        dongInfo.setDongNm1("101동");
        dongInfo.setDongNm2("");
        dongInfo.setDongNm3("");
        dongInfo.setGrndFlrCnt(25);

        mockDongInfoList.add(dongInfo);

        when(aptIdInfoService.fetchDongInfo(anyString(), anyInt(), anyInt()))
                .thenReturn(mockDongInfoList);

        // When & Then
        mockMvc.perform(get("/publicdata/aptidinfo/dong")
                .param("complexPk", complexPk)
                .param("page", String.valueOf(page))
                .param("perPage", String.valueOf(perPage))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].complexPk").value("11350120401804"))
                .andExpect(jsonPath("$[0].dongNm1").value("101동"))
                .andExpect(jsonPath("$[0].dongNm2").value(""))
                .andExpect(jsonPath("$[0].dongNm3").value(""))
                .andExpect(jsonPath("$[0].grndFlrCnt").value(25));
    }

    @Test
    void testGetDongInfo_WithDefaultValues() throws Exception {
        // Given
        String complexPk = "11350120401804";
        List<DongInfo> mockDongInfoList = new ArrayList<>();

        when(aptIdInfoService.fetchDongInfo(anyString(), anyInt(), anyInt()))
                .thenReturn(mockDongInfoList);

        // When & Then
        mockMvc.perform(get("/publicdata/aptidinfo/dong")
                .param("complexPk", complexPk)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetDongInfo_EmptyComplexPk() throws Exception {
        // Given
        String complexPk = "";
        List<DongInfo> mockDongInfoList = new ArrayList<>();

        when(aptIdInfoService.fetchDongInfo(anyString(), anyInt(), anyInt()))
                .thenReturn(mockDongInfoList);

        // When & Then
        mockMvc.perform(get("/publicdata/aptidinfo/dong")
                .param("complexPk", complexPk)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
