package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Date;
import java.util.List;

@Repository
public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry toBeCreated) {
        Number insertedTimeEntryId = insertTimeEntry(toBeCreated);
        List<TimeEntry> timeEntries = getTimeEntriesWithId(insertedTimeEntryId);

        return timeEntries.get(0);
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        List<TimeEntry> timeEntryWithId = getTimeEntriesWithId(timeEntryId);
        if(!timeEntryWithId.isEmpty()) {
            return timeEntryWithId.get(0);
        }

        return null;
    }

    @Override
    public TimeEntry update(long id, TimeEntry newValue) {
        PreparedStatementCreator psc = con -> {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE time_entries " +
                            "SET project_id = ?, user_id = ?, date = ?, hours = ? " +
                            "WHERE id = ?;");
            ps.setLong(1, newValue.getProjectId());
            ps.setLong(2, newValue.getUserId());
            ps.setObject(3, newValue.getDate());
            ps.setInt(4, newValue.getHours());
            ps.setLong(5, id);

            System.out.println(ps);

            return ps;
        };

        jdbcTemplate.update(psc);

        return getTimeEntriesWithId(id).get(0);
    }

    @Override
    public void delete(long id) {
        PreparedStatementCreator psc = con -> {
            PreparedStatement ps = con.prepareStatement("DELETE FROM time_entries WHERE id = ?;");
            ps.setLong(1, id);

            return ps;
        };

        jdbcTemplate.update(psc);
    }

    @Override
    public List<TimeEntry> list() {
        return getTimeEntries();
    }

    private Number insertTimeEntry(TimeEntry toBeCreated) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator psc = con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO time_entries ( project_id, user_id, date, hours )\n" +
                            "VALUES ( ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, toBeCreated.getProjectId());
            ps.setLong(2, toBeCreated.getUserId());
            ps.setObject(3, toBeCreated.getDate());
            ps.setInt(4, toBeCreated.getHours());

            return ps;
        };

        jdbcTemplate.update(psc, generatedKeyHolder);

        return generatedKeyHolder.getKey();
    }

    private RowMapper<TimeEntry> timeEntryRowMapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            ((Date) rs.getObject("date")).toLocalDate(),
            rs.getInt("hours")
    );

    private List<TimeEntry> getTimeEntriesWithId(Number insertedTimeEntryId) {
        return jdbcTemplate.query(
                "select * from time_entries where id = ?",
                new Object[] {insertedTimeEntryId},
                timeEntryRowMapper
        );
    }

    private List<TimeEntry> getTimeEntries() {
        return jdbcTemplate.query(
                "select * from time_entries",
                new Object[] {},
                (rs, rowNum) -> new TimeEntry(
                        rs.getLong("id"),
                        rs.getLong("project_id"),
                        rs.getLong("user_id"),
                        ((Date) rs.getObject("date")).toLocalDate(),
                        rs.getInt("hours")
                )
        );
    }
}
