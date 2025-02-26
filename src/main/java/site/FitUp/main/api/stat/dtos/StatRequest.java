package site.FitUp.main.api.stat.dtos;

public class StatRequest {
    public static class CreateStatRequest{
        private int height;
        private int weight;
        private int bodyFat;
        private int pushUps;
        private int sitUps;
        private float runningPace;
        private int squat;
        private int benchPress;
        private int deadLift;
    }
}
