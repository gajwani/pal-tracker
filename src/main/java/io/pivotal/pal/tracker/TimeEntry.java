package io.pivotal.pal.tracker;

import java.time.LocalDate;

public class TimeEntry {
    private long id;
    private long projectId;
    private long userId;
    private LocalDate date;
    private int hours;

    public TimeEntry() {
        this.id = 0;
        this.projectId = 0;
        this.userId = 0;
        this.date = null;
        this.hours = 0;
    }

    public TimeEntry(long id, long projectId, long userId, LocalDate date, int hours) {
        this.id = id;
        this.projectId = projectId;
        this.userId = userId;
        this.date = date;
        this.hours = hours;
    }

    public TimeEntry(long projectId, long userId, LocalDate date, int hours) {
        this.projectId = projectId;
        this.userId = userId;
        this.date = date;
        this.hours = hours;
    }

    @Override
    public boolean equals(Object other) {
        if(other == this) {
            return true;
        }

        if(other instanceof TimeEntry) {
            TimeEntry that = (TimeEntry) other;
            return this.id == that.id &&
                    this.projectId == that.projectId &&
                    this.userId == that.userId &&
                    this.date.equals(that.date) &&
                    this.hours == that.hours;
        }

        return false;
    }

    public long getId() {
        return id;
    }

    public long getProjectId() {
        return projectId;
    }

    public long getUserId() {
        return userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getHours() {
        return hours;
    }
}
