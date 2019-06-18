package io.pivotal.pal.tracker;

import org.springframework.util.ObjectUtils;

import java.sql.Time;
import java.util.*;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    Map<Long, TimeEntry> entries = new HashMap<>();

    private Long lastId = 1L;

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(lastId);
        entries.put(timeEntry.getId(), timeEntry);
        lastId++;

        return timeEntry;
    }

    @Override
    public TimeEntry find(long id) {
        return entries.get(id);
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList<>(entries.values());
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        if(ObjectUtils.isEmpty(entries.get(id))) {
            return null;
        }
        timeEntry.setId(id);
        entries.put(id, timeEntry);
        return entries.get(id);
    }

    @Override
    public void delete(long id) {
        entries.remove(id);
    }
}
