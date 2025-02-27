package site.FitUp.main.api.user.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.stereotype.Service;
import site.FitUp.main.api.user.dtos.UserRequest;
import site.FitUp.main.api.user.dtos.UserResponse;
import site.FitUp.main.model.User;
import site.FitUp.main.repository.UserRepository;
import site.FitUp.main.util.JwtUtil;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponse.CreateUserResponse CreateUserService(UserRequest.CreateUserRequest request) {

        String newUserId=generateUserId();
        User user=User.builder()
                .userId(newUserId)
                .email(request.getEmail())
                .name(request.getName())
                .nickname(request.getNickname())
                .password(request.getPassword())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .chronic(request.getChronic())
                .targetWeight(request.getTargetWeight())
                .goal(request.getGoal()).build();
        userRepository.save(user);
        log.info(newUserId);
        return UserResponse.CreateUserResponse.builder()
                .userId(newUserId)
                .accessToken(JwtUtil.generateToken(newUserId))
                .refreshToken(JwtUtil.generateToken(newUserId)).build();
    }

    @Override
    public UserResponse.LoginUserResponse LoginUserService(UserRequest.LoginUserRequest request) throws IllegalAccessException {
        User user=userRepository.findByEmail(request.getEmail());
        if(!user.getPassword().equals(request.getPassword())){
            throw new IllegalAccessException();
        }else{
            return UserResponse.LoginUserResponse.builder()
                    .accessToken(JwtUtil.generateToken(user.getUserId()))
                    .refreshToken(JwtUtil.generateToken(user.getUserId())).build();
        }
    }
    private String generateUserId() {
        return "USER-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8); // 8자리 랜덤 ID
    }

}
