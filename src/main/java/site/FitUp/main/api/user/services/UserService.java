package site.FitUp.main.api.user.services;

import site.FitUp.main.api.user.dtos.UserRequest;
import site.FitUp.main.api.user.dtos.UserResponse;

public interface UserService {
    UserResponse.CreateUserResponse CreateUserService(UserRequest.CreateUserRequest request);

}
