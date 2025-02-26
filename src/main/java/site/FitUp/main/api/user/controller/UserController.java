package site.FitUp.main.api.user.controller;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;
import site.FitUp.main.api.user.dtos.UserRequest;
import site.FitUp.main.api.user.dtos.UserResponse;
import site.FitUp.main.common.ApiResponse;
import site.FitUp.main.util.JwtUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {


    @PostMapping("")
    public ApiResponse<UserResponse.CreateUserResponse> CreateUserController(){
        String userId="USERf029w9";
        String accessToken= JwtUtil.generateToken(userId);
        String refreshToken=JwtUtil.generateToken(userId);
        return new ApiResponse<>(UserResponse.CreateUserResponse.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken).build());
    }
    @GetMapping("")
    public ApiResponse GetUserController(@RequestHeader("Authorization") String token) {

        String userId = JwtUtil.extractUserId(token);
        return new ApiResponse(userId);
    }
    @PostMapping("/token")
    public ApiResponse<UserResponse.LoginUserResponse> LoginUserController(@RequestHeader("Authorization") String token){
        String userId="USERf029w9";
        String accessToken= JwtUtil.generateToken(userId);
        String refreshToken=JwtUtil.generateToken(userId);

        return new ApiResponse<>(UserResponse.LoginUserResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
    }
    @PostMapping("/target")
    public ApiResponse<UserResponse.EditTargetResponse> EditTargetController(@RequestBody UserRequest.EditUserRequest request){
        return new ApiResponse<>(UserResponse.EditTargetResponse.builder()
                .content(request.getContent()).build());
    }
}
