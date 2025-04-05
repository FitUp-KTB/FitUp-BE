package site.FitUp.main.api.user.services;

import site.FitUp.main.api.user.dtos.UserRequest;
import site.FitUp.main.api.user.dtos.UserResponse;

public interface UserService {
    UserResponse.CreateUserResponse CreateUserService(UserRequest.CreateUserRequest request);

    String createUserProfileService(UserRequest.CreateUserProfileRequest request,String userId);

    UserResponse.LoginUserResponse LoginUserService(UserRequest.LoginUserRequest request) throws IllegalAccessException;

    UserResponse.EditTargetResponse EditTargetService(UserRequest.EditUserRequest request,String userId);

    UserResponse.GetUserResponse GetUserResponse(String userId);

}
