package site.FitUp.main.api.stat.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import site.FitUp.main.common.enums.CharacterType;

import java.util.List;

public class StatResponse {
    @Builder
    @Getter
    public static class CreateStatResponse{
        private long userStatSeq;
        private int strength;
        private int endurance;
        private int speed;
        private int flexibility;
        private int stamina;
        private String characterType;
    }
    @Builder
    @Getter
    public static class GetStatsResponse{
        private List<GetStatResponse> stats;
    }
    @Builder
    @Getter
    public static class GetStatResponse{
        private long userStatSeq;
        private int strength;
        private int endurance;
        private int speed;
        private int flexibility;
        private int stamina;
        private String characterType;
        private String createdAt;
    }
}
