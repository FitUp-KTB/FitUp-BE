package site.FitUp.main.api.user.services;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import site.FitUp.main.api.user.dtos.UserRequest;
import site.FitUp.main.api.user.dtos.UserResponse;
import site.FitUp.main.common.enums.MessageCode;
import site.FitUp.main.exception.UserException.UserException.UserIsValidException;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse.CreateUserResponse CreateUserService(
            UserRequest.CreateUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new UserIsValidException();
        }
        String newUserId = generateUserId();
        User user = User.builder()
                .userId(newUserId)
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender()).build();
        userRepository.save(user);
        log.info(newUserId);
        return UserResponse.CreateUserResponse.builder()
                .userId(newUserId)
                .accessToken(JwtUtil.generateAccessToken(newUserId))
                .refreshToken(JwtUtil.generateRefreshToken(newUserId)).build();
    }

    @Override
    @Transactional
    public String createUserProfileService(UserRequest.CreateUserProfileRequest request,
            String userId) {
        User user = userRepository.findById(userId).orElse(null);

        user.setBirthDate(request.getBirthDate());
        user.setTargetWeight(request.getTargetWeight());
        user.setGoal(request.getGoal());
        user.setChronic(request.getChronic());

        return MessageCode.PROFILE_UPDATED.getMessage();

    }

    @Override
    public UserResponse.LoginUserResponse LoginUserService(UserRequest.LoginUserRequest request)
            throws IllegalAccessException {

        User user = userRepository.findByEmail(request.getEmail());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalAccessException();
        } else {
            return UserResponse.LoginUserResponse.builder()
                    .accessToken(JwtUtil.generateAccessToken(user.getUserId()))
                    .refreshToken(JwtUtil.generateRefreshToken(user.getUserId())).build();
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


}
