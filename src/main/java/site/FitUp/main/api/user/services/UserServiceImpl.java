package site.FitUp.main.api.user.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.stereotype.Service;
import site.FitUp.main.api.user.dtos.UserRequest;
import site.FitUp.main.api.user.dtos.UserResponse;
import site.FitUp.main.model.User;
import site.FitUp.main.repository.UserRepository;
import site.FitUp.main.util.JwtUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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
                .password(hashPassword(request.getPassword()))
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
        if(!user.getPassword().equals(hashPassword(request.getPassword()))){
            throw new IllegalAccessException();
        }else{
            return UserResponse.LoginUserResponse.builder()
                    .accessToken(JwtUtil.generateToken(user.getUserId()))
                    .refreshToken(JwtUtil.generateToken(user.getUserId())).build();
        }
    }
    @Transactional
    public UserResponse.EditTargetResponse EditTargetService(UserRequest.EditUserRequest request,String userId){
        User user= userRepository.findById(userId).orElse(null);
        user.setGoal(request.getContent());
        return UserResponse.EditTargetResponse.builder().content(request.getContent()).build();
    }

    public UserResponse.GetUserResponse GetUserResponse(String userId){
        User user= userRepository.findById(userId).orElse(null);
        return UserResponse.GetUserResponse
                .builder()
                .email(user.getEmail())
                .nickName(user.getNickname())
                .name(user.getName())
                .build();
    }
    ///
    private String generateUserId() {
        return "USER-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8); // 8자리 랜덤 ID
    }

    private static String hashPassword(String password){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

}
