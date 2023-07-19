package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.LineIntegrationSupporter.createLineByLineRequest;
import static subway.integration.LineIntegrationSupporter.deleteLineByLineId;
import static subway.integration.LineIntegrationSupporter.findAllLines;
import static subway.integration.LineIntegrationSupporter.getLineByLineId;
import static subway.integration.LineIntegrationSupporter.registerSectionToLine;
import static subway.integration.LineIntegrationSupporter.updateLineByLineId;
import static subway.integration.StationIntegrationSupporter.createStation;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import subway.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
class LineIntegrationTest extends IntegrationTest {

    private String stationRequest1;
    private String stationRequest2;
    private String stationRequest3;
    private String stationRequest4;
    private LineRequest lineRequest1;
    private LineRequest lineRequest2;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        stationRequest1 = createStation(new StationRequest("강남")).body().as(StationResponse.class).getId();
        stationRequest2 = createStation(new StationRequest("신도림")).body().as(StationResponse.class).getId();
        stationRequest3 = createStation(new StationRequest("부천")).body().as(StationResponse.class).getId();
        stationRequest4 = createStation(new StationRequest("잠실")).body().as(StationResponse.class).getId();

        lineRequest1 = new LineRequest("신분당선", "bg-red-600", String.valueOf(stationRequest1),
                String.valueOf(stationRequest2), 10);

        lineRequest2 = new LineRequest("2호선", "bg-green-600", String.valueOf(stationRequest3),
                String.valueOf(stationRequest4), 5);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = createLineByLineRequest(lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        createLineByLineRequest(lineRequest1);

        // when
        ExtractableResponse<Response> response = createLineByLineRequest(lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = createLineByLineRequest(lineRequest1);
        ExtractableResponse<Response> createResponse2 = createLineByLineRequest(lineRequest2);

        // when
        ExtractableResponse<Response> response = findAllLines();

        List<String> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> it.header("Location").split("/")[2])
                .collect(Collectors.toList());
        List<String> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineByLineRequest(lineRequest1);

        String lineId = createResponse.header("Location").split("/")[2];

        // when
        ExtractableResponse<Response> response = getLineByLineId(lineId);
        LineResponse resultResponse = response.as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultResponse.getId()).isEqualTo(lineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineByLineRequest(lineRequest1);
        String lineId = createResponse.header("Location").split("/")[2];

        // when
        ExtractableResponse<Response> response = updateLineByLineId(lineId, lineRequest2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineByLineRequest(lineRequest1);
        String lineId = createResponse.header("Location").split("/")[2];

        // when
        ExtractableResponse<Response> response = deleteLineByLineId(lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Line에 구간을 등록한다.")
    void createSection() {
        // given
        String lineId = createLineByLineRequest(lineRequest1).body().as(LineResponse.class).getId();

        SectionRequest sectionRequest = new SectionRequest(String.valueOf(stationRequest2),
                String.valueOf(stationRequest3), 5);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
