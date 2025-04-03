package site.FitUp.main.api.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @GetMapping("")
    public ApiResponse<UserResponse.GetUserResponse> GetUserController(
            @RequestHeader("Authorization") String token) {

        String userId = JwtUtil.extractUserId(token);
        return new ApiResponse<>(userService.GetUserResponse(userId));
    }

    @PostMapping("/token")
    public ApiResponse<UserResponse.LoginUserResponse> LoginUserController(
            @RequestBody UserRequest.LoginUserRequest request) throws IllegalAccessException {

        return new ApiResponse<>(userService.LoginUserService(request));
    }

    @PatchMapping("/target")
    public ApiResponse<UserResponse.EditTargetResponse> EditTargetController(
            @RequestHeader("Authorization") String token,
            @RequestBody UserRequest.EditUserRequest request) {
        String userId = JwtUtil.extractUserId(token);
        return new ApiResponse<>(userService.EditTargetService(request, userId));
    }

}
