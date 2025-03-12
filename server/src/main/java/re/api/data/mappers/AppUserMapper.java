package re.api.data.mappers;

import org.springframework.jdbc.core.RowMapper;
import re.api.models.AppUser;
import re.api.models.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AppUserMapper implements RowMapper<AppUser> {
    @Override
    public AppUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new AppUser(
                rs.getInt("app_user_id"),
                rs.getString("email"),
                rs.getString("passwordHash"),
                rs.getString("user_role"),
                rs.getBoolean("enabled")
        );
    }
}
