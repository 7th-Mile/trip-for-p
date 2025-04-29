package team.seventhmile.tripforp.domain.course.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class RouteResult {
    private double minTotalDistance = Double.MAX_VALUE;
    private String minRoute = "";
}
