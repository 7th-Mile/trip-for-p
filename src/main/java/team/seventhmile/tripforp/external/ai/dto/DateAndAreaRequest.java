package team.seventhmile.tripforp.external.ai.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DateAndAreaRequest {

    @NotNull(message = "출발 날짜는 필수입니다.")
    @FutureOrPresent(message = "출발 날짜는 현재 또는 미래의 날짜여야 합니다.")
    private LocalDate startDate;

    @NotNull(message = "도착 날짜는 필수입니다.")
    @Future(message = "도착 날짜는 미래의 날짜여야 합니다.")
    private LocalDate endDate;

    @NotBlank(message = "지역은 필수입니다.")
    @Pattern(regexp = "^(서울|경기|인천|강원|충북|충남|대전|경북|경남|대구|울산|부산|전북|전남|광주|제주|세종)$",
        message = "유효하지 않은 지역입니다.")
    private String area;

    public static String convertToEnglish(String area) {
        switch (area) {
            case "서울":
                return "Seoul";
            case "경기":
                return "Gyeonggi-do";
            case "인천":
                return "Incheon";
            case "강원":
                return "Gangwon-do";
            case "충북":
                return "Chungcheongbuk-do";
            case "충남":
                return "Chungcheongnam-do";
            case "대전":
                return "Daejeon";
            case "경북":
                return "Gyeongsangbuk-do";
            case "경남":
                return "Gyeongsangnam-do";
            case "대구":
                return "Daegu";
            case "울산":
                return "Ulsan";
            case "부산":
                return "Busan";
            case "전북":
                return "Jeollabuk-do";
            case "전남":
                return "Jeollanam-do";
            case "광주":
                return "Gwangju";
            case "제주":
                return "Jeju";
            case "세종":
                return "Sejong";
            default:
                return "Korea";  // 또는 예외 처리
        }
    }
}
