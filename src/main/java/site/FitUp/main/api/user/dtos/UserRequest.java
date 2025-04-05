package site.FitUp.main.api.user.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import site.FitUp.main.common.enums.Gender;

import java.time.LocalDate;

public class UserRequest {
    @Getter
    @Setter
    public static class CreateUserRequest{
        private String email;
        private String password;
        private String name;
        private Gender gender;

    }

    @Getter
    @Builder
    public static class CreateUserProfileRequest{
        private LocalDate birthDate;
        private int targetWeight;
        private String goal;
        private String chronic;
    }
    @Getter
    @Setter
    public static class EditUserRequest{
        private String content;
    }
    @Getter
    @Setter
    public static class LoginUserRequest{
        private String email;
        private String password;
    }
}
