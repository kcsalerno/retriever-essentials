package re.api.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class AppUser implements UserDetails {
    private int appUserId;
    private final String email;
    private final String passwordHash;
    private final GrantedAuthority authority;
    private boolean enabled;

    public AppUser(int appUserId,String email, String passwordHash, String user_role, boolean enabled) {
        this.appUserId = appUserId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.authority = new SimpleGrantedAuthority(user_role);     // Use ENUM value directly
        this.enabled = enabled;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return List.of(authority); // Return a single-element list
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    // Could add methods to set password, username, or roles if needed later.

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

    public int getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(int appUserId) {
        this.appUserId = appUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;     // Self-check
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
                ", role=" + authority.getAuthority() +
                '}';
    }
}