package site.FitUp.main.api.user.services;

import jakarta.transaction.Transactional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.FitUp.main.api.user.dtos.UserRequest;
import site.FitUp.main.api.user.dtos.UserResponse;
import site.FitUp.main.model.User;
import site.FitUp.main.model.UserStat;
import site.FitUp.main.repository.UserRepository;
import site.FitUp.main.repository.UserStatRepository;
import site.FitUp.main.util.JwtUtil;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserStatRepository userStatRepository;

    @Override
    public UserResponse.CreateUserResponse CreateUserService(
            UserRequest.CreateUserRequest request) {

        String newUserId = generateUserId();
        User user = User.builder()
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
    public UserResponse.LoginUserResponse LoginUserService(UserRequest.LoginUserRequest request)
            throws IllegalAccessException {

        User user = userRepository.findByEmail(request.getEmail());
        if (!user.getPassword().equals(hashPassword(request.getPassword()))) {
            throw new IllegalAccessException();
        } else {
            return UserResponse.LoginUserResponse.builder()
                    .accessToken(JwtUtil.generateToken(user.getUserId()))
                    .refreshToken(JwtUtil.generateToken(user.getUserId())).build();
        }
    }

    @Transactional
    public UserResponse.EditTargetResponse EditTargetService(UserRequest.EditUserRequest request,
            String userId) {
        User user = userRepository.findById(userId).orElse(null);
        user.setGoal(request.getContent());
        return UserResponse.EditTargetResponse.builder().content(request.getContent()).build();
    }

    public UserResponse.GetUserResponse GetUserResponse(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        UserStat userStat = userStatRepository.findTopByUserOrderByCreatedAtDesc(user);
        if (userStat == null) {
            userStat = UserStat.builder()
                    .height(0)
                    .weight(0)
                    .fat(0)
                    .muscleMass(0)
                    .pushUps(0)
                    .sitUp(0)
                    .runningPace(0.0)
                    .runningTime(0)
                    .squat(0)
                    .benchPress(0)
                    .deadLift(0).build();
        }

        return UserResponse.GetUserResponse
                .builder()
                .email(user.getEmail())
                .nickName(user.getNickname())
                .name(user.getName())
                .height(userStat.getHeight())
                .weight(userStat.getWeight())
                .bodyFat(userStat.getFat())
                .muscleMass(userStat.getMuscleMass())
                .pushUps(userStat.getPushUps())
                .sitUps(userStat.getSitUp())
                .runningPace(userStat.getRunningPace())
                .runningTime(userStat.getRunningTime())
                .squat(userStat.getSquat())
                .benchPress(userStat.getBenchPress())
                .build();
    }

    ///
    private String generateUserId() {
        return "USER-" + UUID.randomUUID().toString().replaceAll("-", "")
                .substring(0, 8); // 8자리 랜덤 ID
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

}
