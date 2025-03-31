package re.api.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRole {
    ADMIN,
    AUTHORITY;

    public GrantedAuthority toGrantedAuthority() {
        // "ROLE_" prefix is required by Spring Security
        return new SimpleGrantedAuthority("ROLE_" + this.name());
    }

    @Override
    public String toString() {
        return name();
    }
}
