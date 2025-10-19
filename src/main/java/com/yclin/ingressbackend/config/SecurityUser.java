package com.yclin.ingressbackend.config;

import com.yclin.ingressbackend.entity.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 一个 UserDetails 的实现，它包装了我们的 User 实体。
 * 这将我们的领域模型 (User) 与安全模型 (UserDetails) 完全解耦，
 * 解决了因直接在实体上实现 UserDetails 而引发的各种棘手问题。
 */
public class SecurityUser implements UserDetails {

    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }

    /**
     * 将所有 UserDetails 接口方法的调用，委托给内部包装的 user 对象。
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 在我们的业务中，账户永不过期
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 在我们的业务中，账户永不锁定
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 在我们的业务中，凭证永不过期
    }

    @Override
    public boolean isEnabled() {
        // 用户的启用状态取决于其是否未被封禁
        return !user.getIsBanned();
    }
    
    /**
     * 一个辅助方法，方便我们从 Spring Security 的上下文中
     * 取回我们原始的、完整的 User 实体对象。
     * @return The original User entity.
     */
    public User getUser() {
        return user;
    }
}