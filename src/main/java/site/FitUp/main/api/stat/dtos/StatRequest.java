package site.FitUp.main.api.stat.dtos;

import lombok.Getter;
import lombok.Setter;

public class StatRequest {
    @Getter
    @Setter
    public static class CreateStatRequest{
        private int height;
        private int weight;
        private int bodyFat;
        private int pushUps;
        private int sitUps;
        private Double runningPace;
        private int runningTime;
        private int squat;
        private int benchPress;
        private int deadLift;
        private int muscleMass;
    }
}
