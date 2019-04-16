package io.pivotal.pal.tracker;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    private Map<Long, TimeEntry> entries;
    private long nextId;

    public InMemoryTimeEntryRepository() {
        nextId = 1L;
        this.entries = new HashMap<>();
    }

    @Override
    public TimeEntry create(TimeEntry toBeCreated) {
        long toBeCreatedId = nextId;

        TimeEntry created = new TimeEntry(
                toBeCreatedId,
                toBeCreated.getProjectId(),
                toBeCreated.getUserId(),
                toBeCreated.getDate(),
                toBeCreated.getHours()
        );

        this.entries.put(toBeCreatedId, created);
        nextId += 1;
        return created;
    }

    @Override
    public TimeEntry find(long id) {
        return entries.get(id);
    }

    @Override
    public TimeEntry update(long id, TimeEntry newValue) {
        if(entries.get(id) == null) {
            return null;
        }

        TimeEntry updated = new TimeEntry(
                id,
                newValue.getProjectId(),
                newValue.getUserId(),
                newValue.getDate(),
                newValue.getHours()
        );
        entries.replace(id, updated);
        return updated;
    }

    @Override
    public void delete(long id) {
        entries.remove(id);
    }

    @Override
    public List<TimeEntry> list() {

        return entries.values().stream().sorted(
                Comparator.comparingLong(TimeEntry::getId)
        ).collect(Collectors.toList());
    }
}
