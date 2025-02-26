package site.FitUp.main.api.user.dtos;

import lombok.Getter;
import lombok.Setter;

public class UserRequest {
    public static class CreateUserRequest{

    }
    @Getter
    @Setter
    public static class EditUserRequest{
        private String content;
    }

}
