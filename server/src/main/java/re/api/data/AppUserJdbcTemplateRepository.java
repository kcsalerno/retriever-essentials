package re.api.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import re.api.data.mappers.AppUserMapper;
import re.api.models.AppUser;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class AppUserJdbcTemplateRepository implements AppUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public AppUserJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<AppUser> findAll() {
        final String sql = """
                SELECT app_user_id, username, password_hash, user_role, enabled
                FROM app_user
                """;

        return jdbcTemplate.query(sql, new AppUserMapper());
    }

    @Override
    public AppUser findById(int appUserId) {
        final String sql = """
                SELECT app_user_id, username, password_hash, user_role, enabled
                FROM app_user
                WHERE app_user_id = ?
                """;

        return jdbcTemplate.query(sql, new AppUserMapper(), appUserId).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public AppUser findByEmail(String email) {
        final String sql = """
                SELECT app_user_id, username, password_hash, user_role, enabled
                FROM app_user
                WHERE username = ?
                """;

        return jdbcTemplate.query(sql, new AppUserMapper(), email).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public AppUser add(AppUser user) {
        final String sql = """
                INSERT INTO app_user (username, user_role)
                VALUES (?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
            return ps;
        }, keyHolder);

        if (rows <= 0) {
            return null;
        }

        user.setAppUserId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public boolean updatePassword(int appUserId, String passwordHash) {
        final String sql = """
                UPDATE app_user
                SET password_hash = ?
                WHERE app_user_id = ?
                """;

        return jdbcTemplate.update(sql, passwordHash, appUserId) > 0;
    }

    @Override
    public boolean disableById(int appUserId) {
        final String sql = """
                UPDATE app_user
                SET enabled = false
                WHERE app_user_id = ?
                """;

        return jdbcTemplate.update(sql, appUserId) > 0;
    }

    @Override
    public boolean enableById(int appUserId) {
        final String sql = """
                UPDATE app_user
                SET enabled = true
                WHERE app_user_id = ?
                """;

        return jdbcTemplate.update(sql, appUserId) > 0;
    }
}
