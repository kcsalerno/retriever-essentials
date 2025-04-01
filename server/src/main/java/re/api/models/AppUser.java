package re.api.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class AppUser implements UserDetails {
    private int appUserId;
    private final String email;
    private String passwordHash;
    private final UserRole userRole;
    private boolean enabled;

    public AppUser(int appUserId,String email, String passwordHash, UserRole userRole, boolean enabled) {
        this.appUserId = appUserId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userRole = userRole;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(userRole.toGrantedAuthority());
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean hasRole(UserRole role) {
        return this.userRole == role;
    }

    public int getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(int appUserId) {
        this.appUserId = appUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;
        return Objects.equals(email, appUser.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "appUserId=" + appUserId +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", role=" + userRole +
                '}';
    }
}