package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.exception.StationException;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        validateDuplicateName(stationRequest.getName());

        Station station = stationDao.insert(new Station(stationRequest.getName()));
        return StationResponse.of(station);
    }

    private void validateDuplicateName(String name) {
        stationDao.findByName(name)
                .ifPresent(station -> {
                    throw new StationException(
                            MessageFormat.format("station name \"{0}\"에 해당하는 station이 이미 존재합니다.", station.getName())
                    );
                });
    }

    public StationResponse findStationById(Long id) {
        Station station = stationDao.findById(id).orElseThrow(() -> new StationException(
                MessageFormat.format("station id \"{0}\"에 해당하는 station이 없습니다.", id)));
        
        return StationResponse.of(station);
    }

    public List<StationResponse> findAllStations() {
        List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void updateStation(Long id, StationRequest stationRequest) {
        stationDao.update(new Station(id, stationRequest.getName()));
    }

    public void deleteStationById(Long id) {
        stationDao.deleteById(id);
    }
}