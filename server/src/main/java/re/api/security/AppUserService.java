package re.api.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import re.api.data.AppUserRepository;
import re.api.domain.Result;
import re.api.domain.ResultType;
import re.api.domain.Validations;
import re.api.models.AppUser;

import java.util.List;

@Service
public class AppUserService {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AppUser> findAll() {
        return repository.findAll();
    }

    public AppUser findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public AppUser findById(int appUserId) {
        return repository.findById(appUserId);
    }

    public Result<AppUser> add(AppUser appUser) {
        Result<AppUser> result = validate(appUser);

        if (!result.isSuccess()) {
            return result;
        }

        AppUser existing = repository.findByEmail(appUser.getUsername());
        if (existing != null) {
            result.addMessage(ResultType.INVALID, "Email already in use.");
            return result;
        }

        String hashed = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(hashed);

        AppUser added = repository.add(appUser);
        result.setPayload(added);
        return result;
    }

    public Result<AppUser> changePassword(AppUser appUser) {
        Result<AppUser> result = new Result<>();

        if (appUser == null) {
            result.addMessage(ResultType.INVALID, "User cannot be null.");
            return result;
        }

        int userId = appUser.getAppUserId();
        String newPassword = appUser.getPassword();

        if (newPassword == null || newPassword.length() < 6) {
            result.addMessage(ResultType.INVALID, "Password must be at least 6 characters.");
            return result;
        }

        String hashed = passwordEncoder.encode(newPassword);
        boolean success = repository.updatePassword(userId, hashed);

        if (!success) {
            result.addMessage(ResultType.NOT_FOUND, "User not found.");
        }

        return result;
    }

    public Result<AppUser> enableById(int userId) {
        Result<AppUser> result = new Result<>();

        boolean success = repository.enableById(userId);
        if (!success) {
            result.addMessage(ResultType.NOT_FOUND, "User not found.");
        }

        return result;
    }

    public Result<AppUser> disableById(int userId) {
        Result<AppUser> result = new Result<>();

        boolean success = repository.disableById(userId);
        if (!success) {
            result.addMessage(ResultType.NOT_FOUND, "User not found.");
        }

        return result;
    }

    private Result<AppUser> validate(AppUser user) {
        Result<AppUser> result = new Result<>();

        if (user == null) {
            result.addMessage(ResultType.INVALID, "User cannot be null.");
            return result;
        }

        if (Validations.isNullOrBlank(user.getUsername())) {
            result.addMessage(ResultType.INVALID, "Email is required.");
        }

        if (Validations.isNullOrBlank(user.getPassword())) {
            result.addMessage(ResultType.INVALID, "Password is required.");
        }

        return result;
    }
}
