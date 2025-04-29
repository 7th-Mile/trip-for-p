package team.seventhmile.tripforp.domain.region.entity;

import java.util.Optional;
import lombok.Getter;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;

@Getter
public enum Province {
    SEOUL("서울"),
    BUSAN("부산"),
    DAEGU("대구"),
    INCHEON("인천"),
    GWANGJU("광주"),
    DAEJEON("대전"),
    ULSAN("울산"),
    SEJONG("세종"),
    GYEONGGI("경기"),
    GANGWON("강원"),
    CHUNGBUK("충북"),
    CHUNGNAM("충남"),
    JEONBUK("전북"),
    JEONNAM("전남"),
    GYEONGBUK("경북"),
    GYEONGNAM("경남"),
    JEJU("제주");

    private final String name;

    Province(String name) {
        this.name = name;
    }

    public static Province findByName(String name) {
        return Province.findByNameOptional(name)
            .orElseThrow(() -> new ResourceNotFoundException(Province.class));
    }

    public static Optional<Province> findByNameOptional(String name) {
        for (Province province : Province.values()) {
            if (province.getName().equals(name)) {
                return Optional.of(province);
            }
        }
        return Optional.empty();
    }
}
