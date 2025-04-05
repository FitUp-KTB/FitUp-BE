package site.FitUp.main.Security;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import site.FitUp.main.model.User;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {
    private final User userJpa;

    public UserDetailsImpl(User userJpa) {
        this.userJpa = userJpa;
    }

    public String getUserId() {
        return userJpa.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 현재 권한이 없지만, 필요하면 추가 가능
    }

    @Override
    public String getPassword() {
        return userJpa.getPassword();
    }

    @Override
    public String getUsername() {
        return userJpa.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부, 필요에 따라 변경 가능
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부, 필요에 따라 변경 가능
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부, 필요에 따라 변경 가능
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부, 필요에 따라 변경 가능
    }
}
