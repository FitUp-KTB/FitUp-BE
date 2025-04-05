package site.FitUp.main.Security;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.FitUp.main.exception.UserException.UserException;
import site.FitUp.main.model.User;
import site.FitUp.main.repository.UserRepository;

@Service

public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User userJpa = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException());

        return new UserDetailsImpl(userJpa);

    }
}

