package site.FitUp.main.api.user.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

}
