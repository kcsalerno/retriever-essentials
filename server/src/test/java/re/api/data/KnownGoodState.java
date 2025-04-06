package re.api.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class KnownGoodState {

    static boolean hasRun = false;
    @Autowired
    JdbcTemplate jdbcTemplate;

    void set() {
        if (!hasRun) {
            hasRun = true;
            jdbcTemplate.update("call set_known_good_state()");
        }
    }
}
