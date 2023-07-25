package subway.domain;

import subway.exception.SectionException;
import subway.exception.StationException;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

public class Section {

    private static final int MIN_DISTANCE_SIZE = 1;

    private final Long id;

    private Integer distance;
    private Station upStation;
    private Station downStation;
    private Section upSection;
    private Section downSection;

    private Section(Builder builder) {
        validate(builder);

        this.id = builder.id;
        this.distance = builder.distance;
        this.upStation = builder.upStation;
        this.downStation = builder.downStation;
        this.upSection = builder.upSection;
        this.downSection = builder.downSection;
    }

    public static Builder builder() {
        return new Builder();
    }

    private void validate(Builder builder) {
        validateNullStations(builder);
        validateSameStations(builder);
        validateDistance(builder);
    }

    private void validateNullStations(Builder builder) {
        if (builder.upStation == null) {
            throw new StationException("station이 존재하지 않습니다.");
        }

        if (builder.downStation == null) {
            throw new StationException("downStation이 존재하지 않습니다.");
        }
    }

    private void validateSameStations(Builder builder) {
        if (builder.upStation.equals(builder.downStation)) {
            throw new StationException(
                    MessageFormat.format("upStation\"{0}\"과 downStation\"{1}\"은 같을 수 없습니다.", builder.upStation, builder.downStation)
            );
        }
    }

    private void validateDistance(Builder builder) {
        if (builder.distance < MIN_DISTANCE_SIZE) {
            throw new SectionException(
                    MessageFormat.format("distance \"{0}\"는 0 이하가 될 수 없습니다.", builder.distance)
            );
        }
    }

    Section connectSection(final Section requestSection) {
        if (requestSection == null) {
            throw new SectionException("requestSection이 존재하지 않습니다.");
        }

        Optional<SectionConnector> sectionConnectorOptional = SectionConnector
                .findSectionConnector(this, requestSection);
        if (sectionConnectorOptional.isEmpty()) {
            return connectSectionIfDownSectionPresent(requestSection);
        }

        return sectionConnectorOptional.get().connectSection(this, requestSection);
    }

    private Section connectSectionIfDownSectionPresent(final Section requestSection) {
        if (downSection == null) {
            throw new SectionException(
                    MessageFormat.format("line에 requestSection \"{0}\"을 연결할 수 없습니다.", requestSection)
            );
        }

        return downSection.connectSection(requestSection);
    }

    public Section connectDownSection(final Section requestSection) {
        this.downSection = requestSection;
        requestSection.upSection = this;
        return downSection;
    }

    Section connectUpSection(final Section requestSection) {
        this.upSection = requestSection;
        requestSection.downSection = this;
        return requestSection;
    }

    Section connectMiddleUpSection(final Section requestSection) {
        final Section newDownSection = Section.builder()
                .upSection(this)
                .downSection(this.downSection)
                .upStation(requestSection.downStation)
                .downStation(this.downStation)
                .distance(this.distance - requestSection.getDistance())
                .build();

        this.downStation = requestSection.downStation;
        if (this.downSection != null) {
            this.downSection.upSection = newDownSection;
        }
        this.downSection = newDownSection;
        this.distance = requestSection.getDistance();
        return newDownSection;
    }

    Section connectMiddleDownSection(final Section requestSection) {
        final Section newUpSection = Section.builder()
                .upSection(this.upSection)
                .downSection(this)
                .upStation(this.upStation)
                .downStation(requestSection.upStation)
                .distance(this.distance - requestSection.getDistance())
                .build();

        this.upStation = requestSection.upStation;
        if (this.upSection != null) {
            this.upSection.downSection = newUpSection;
        }
        this.upSection = newUpSection;
        this.distance = requestSection.getDistance();
        return newUpSection;
    }

    public Section findDownSection() {
        if (downSection == null) {
            return this;
        }
        return downSection.findDownSection();
    }

    public Section findUpSection() {
        if (upSection == null) {
            return this;
        }
        return upSection.findUpSection();
    }

    public void disconnectDownSection() {
        if (downSection == null) {
            throw new SectionException("downSection이 존재하지 않습니다.");
        }

        downSection.upSection = null;
        downSection = null;
    }

    public Long getId() {
        return id;
    }

    public Section getDownSection() {
        return downSection;
    }

    public Section getUpSection() {
        return upSection;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {

        protected Long id;
        protected Station upStation;
        protected Station downStation;
        protected Section upSection;
        protected Section downSection;
        protected Integer distance;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder upStation(Station upStation) {
            this.upStation = upStation;
            return this;
        }

        public Builder downStation(Station downStation) {
            this.downStation = downStation;
            return this;
        }

        public Builder upSection(Section upSection) {
            this.upSection = upSection;
            return this;
        }

        public Builder downSection(Section downSection) {
            this.downSection = downSection;
            return this;
        }

        public Builder distance(Integer distance) {
            this.distance = distance;
            return this;
        }

        public Section build() {
            return new Section(this);
        }
    }
}
