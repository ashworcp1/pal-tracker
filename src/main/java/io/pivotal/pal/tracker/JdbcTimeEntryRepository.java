package io.pivotal.pal.tracker;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", timeEntry.getId())
                .addValue("project_id", timeEntry.getProjectId())
                .addValue("user_id", timeEntry.getUserId())
                .addValue("date", timeEntry.getDate())
                .addValue("hours", timeEntry.getHours());
        final KeyHolder holder = new GeneratedKeyHolder();

        jdbcTemplate.update("INSERT INTO time_entries (id, project_id, user_id, date, hours) " +
                "VALUES (:id, :project_id, :user_id, :date, :hours)", parameters, holder);

        final long newNameId = holder.getKey().longValue();
        return this.find(newNameId);
    }

    @Override
    public TimeEntry find(long id) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        TimeEntry timeEntry;
        try {
            timeEntry = jdbcTemplate.queryForObject("select * from time_entries where id = :id", paramMap, (rs, rowNum) ->
                    new TimeEntry(rs.getLong("id"), rs.getLong("project_id"), rs.getLong("user_id"), rs.getDate("date").toLocalDate(),
                            rs.getInt("hours")));
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
        return timeEntry;
    }

    @Override
    public List<TimeEntry> list() {
        List<TimeEntry> timeEntries = new ArrayList<>();
        return jdbcTemplate.query("select * from time_entries order by id;", new HashMap<>(), (rs, rowNum) -> new TimeEntry(rs.getLong("id"), rs.getLong("project_id"), rs.getLong("user_id"), rs.getDate("date").toLocalDate(),
                rs.getInt("hours")));
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("project_id", timeEntry.getProjectId())
                .addValue("user_id", timeEntry.getUserId())
                .addValue("date", timeEntry.getDate())
                .addValue("hours", timeEntry.getHours())
                .addValue("id", id);

        int rowsUpdated = jdbcTemplate.update("update time_entries set project_id = :project_id, user_id = :user_id, date = :date, hours = :hours where id = :id", parameters);

        return find(id);
    }

    @Override
    public void delete(long id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id);
        jdbcTemplate.update("delete from time_entries where id = :id", parameters);

    }
}
