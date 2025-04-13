package site.FitUp.main.api.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.FitUp.main.api.user.dtos.UserRequest;
import site.FitUp.main.api.user.dtos.UserResponse;
import site.FitUp.main.api.user.services.UserService;
import site.FitUp.main.common.ApiResponse;
import site.FitUp.main.util.JwtUtil;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ApiResponse<UserResponse.CreateUserResponse> CreateUserController(
            @RequestBody UserRequest.CreateUserRequest request) {

        return new ApiResponse<>(userService.CreateUserService(request));
    }

    @PostMapping("/profile")
    public ApiResponse<String> createUserProfileController(
            @RequestBody UserRequest.CreateUserProfileRequest request) {

        return new ApiResponse<>(userService.createUserProfileService(request, JwtUtil.getAuthenticatedUserId()));

    }


    @GetMapping("")
    public ApiResponse<UserResponse.GetUserResponse> GetUserController() {


        return new ApiResponse<>(userService.GetUserResponse(JwtUtil.getAuthenticatedUserId()));
    }

    @PostMapping("/token")
    public ApiResponse<UserResponse.LoginUserResponse> LoginUserController(
            @RequestBody UserRequest.LoginUserRequest request) throws IllegalAccessException {

        return new ApiResponse<>(userService.LoginUserService(request));
    }

    @PatchMapping("/target")
    public ApiResponse<UserResponse.EditTargetResponse> EditTargetController(
            @RequestBody UserRequest.EditUserRequest request) {

        return new ApiResponse<>(userService.EditTargetService(request, JwtUtil.getAuthenticatedUserId()));
    }

}
