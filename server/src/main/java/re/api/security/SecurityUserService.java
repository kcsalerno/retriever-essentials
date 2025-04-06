package re.api.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import re.api.data.AppUserRepository;
import re.api.models.AppUser;

@Service
public class SecurityUserService implements UserDetailsService {

    private final AppUserRepository repository;

    public SecurityUserService(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = repository.findByEmail(email);
        if (user == null || !user.isEnabled()) {
            throw new UsernameNotFoundException("User not found or not enabled: " + email);
        }
        return user;
    }
}
