package io.pivotal.pal.tracker;

import java.util.List;

public interface TimeEntryRepository {
    TimeEntry create(TimeEntry toBeCreated);

    TimeEntry find(long timeEntryId);

    TimeEntry update(long id, TimeEntry newValue);

    void delete(long id);

    List<TimeEntry> list();
}
