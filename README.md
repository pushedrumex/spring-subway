# jwp-subway-path

--- 

## 도메인 후보

지하철 Station
지하철 Station 속성:
이름(name)
지하철 Section
지하철 (상행 방향)Station과 (하행 방향)Station 사이의 연결 정보
지하철 Section 속성:
길이(distance)
지하철 Line
지하철 Section의 모음으로 Section에 포함된 지하철 Station의 연결 정보
지하철 Line 속성:
Line 이름(name)
Line 색(color)

## 요구사항

### 도메인

- [ ] Line
  - [ ] Line에 포함된 Section들을 알고있다.
  - [ ] Line 이름이 중복되면 예외를 던진다.
  - [ ] Line의 색깔이 중복되면 안된다.
  - [ ] 상행 마지막 Section과, 하행 마지막 Section을 알고있다.
  - [ ] Line에 Section을 추가할 수 있다.
    - [ ] Section이 추가될 때, 추가되는 Section의 상행Station이 자신의 하행Station과 동일한지 확인한다.
    - [ ] 새로운 Section의 하행Station은 해당 Line에 등록되어있는 Station일 수 없다. (상행은 됨)
  - [ ] Section을 삭제할 수 있다.
    - [ ] Line에 하나의 Section만 있을때, 삭제할 수 없다.
    - [ ] Line에서 Station을 삭제할때, Station이 Line에 존재하지 않는다면, 예외를 던진다.
- [x] Section
  - [x] Station과 Station을 연결할 수 있다.
  - [x] 연결된 Section을 알고있다.
- [x] Station
  - [x] 이름을 표현한다.
  - [x] 이름이 중복되면 예외를 던진다.

### 애플리케이션 서비스

- [x] SectionService
  - [x] UpStationId, DownStationId를 통해 Section을 생성한다.

### Dao

- [ ] SectionDao
  - [ ] Section을 받아 저장한다.

``` mermaid

flowchart LR

SectionController[SectionController] --> SectionService[SectionService]

```

### Section 등록 기능

- [ ] 새로운 Section의 시작Station은 기존 Section의 끝 Station과 같으면 등록 가능 하다.
- [ ] 새로운 Section의 시작Station은 기존 Section의 끝 Station과 다르면 예외를 던진다.
- [ ] 새로운 Section의 끝Station은 기존 Section에 등록되어 있으면 예외를 던진다.

### Section 제거 기능

- [ ] Line에 하나의 Section만 있을때, 삭제할 수 없다.
- [ ] Line에서 Station을 삭제할때, Station이 Line에 존재하지 않는다면, 예외를 던진다.
