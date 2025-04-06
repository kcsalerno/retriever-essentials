package re.api.data.mappers;

import org.springframework.jdbc.core.RowMapper;
import re.api.models.AppUser;
import re.api.models.UserRole;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AppUserMapper implements RowMapper<AppUser> {
    @Override
    public AppUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        // No empty constructor in AppUser, so we need to use the constructor with parameters
        return new AppUser(
                rs.getInt("app_user_id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                UserRole.valueOf(rs.getString("user_role").toUpperCase()),
                rs.getBoolean("enabled")
        );
    }
}
