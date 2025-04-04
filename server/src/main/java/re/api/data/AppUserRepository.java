package re.api.data;

import re.api.models.AppUser;

import java.util.List;

public interface AppUserRepository {
    List<AppUser> findAll();

    AppUser findById(int appUserId);

    AppUser findByEmail(String email);

    AppUser add(AppUser appUser);

    boolean updatePassword(int appUserId, String passwordHash);

    boolean disableById(int appUserId);

    boolean enableById(int appUserId);
}
