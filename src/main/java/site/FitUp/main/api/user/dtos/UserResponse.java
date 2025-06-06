package site.FitUp.main.api.user.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.FitUp.main.api.stat.dtos.StatRequest;
import site.FitUp.main.model.UserStat;

public class UserResponse {
    @Builder
    @Getter
    public static class CreateUserResponse{
        private String userId;
        private String accessToken;
        private String refreshToken;
    }
    @Builder
    @Getter
    public static class LoginUserResponse{
        private String accessToken;
        private String refreshToken;
    }
    @Builder
    @Getter
    public static class EditTargetResponse{
        private String content;
    }
    @Builder
    @Getter
    public static class GetUserResponse{
        private String email;
        private String name;
        private String nickName;
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
