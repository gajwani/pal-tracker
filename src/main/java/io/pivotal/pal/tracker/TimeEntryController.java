package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {
    private final TimeEntryRepository timeEntryRepository;
    private final DistributionSummary timeEntrySummary;
    private final Counter actionCounter;

    public TimeEntryController(
            TimeEntryRepository timeEntryRepository,
            MeterRegistry meterRegistry
    ) {
        this.timeEntryRepository = timeEntryRepository;
        timeEntrySummary = meterRegistry.summary("timeEntry.summary");
        actionCounter = meterRegistry.counter("timeEntry.actionCounter");
    }

    @PostMapping
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry toBeCreated) {
        TimeEntry timeEntry = timeEntryRepository.create(toBeCreated);

        actionCounter.increment();
        timeEntrySummary.record(timeEntryRepository.list().size());

        return ResponseEntity.created(null).body(timeEntry);
    }

    @GetMapping("{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable long id) {
        TimeEntry timeEntry = timeEntryRepository.find(id);
        if(timeEntry == null) {
            actionCounter.increment();
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(timeEntry);
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        actionCounter.increment();
        return ResponseEntity.ok(timeEntryRepository.list());
    }

    @PutMapping("{id}")
    public ResponseEntity<TimeEntry> update(@PathVariable long id, @RequestBody TimeEntry newValue) {
        TimeEntry updatedEntry = timeEntryRepository.update(id, newValue);
        if(updatedEntry == null) {
            actionCounter.increment();
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedEntry);
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable long id) {
        timeEntryRepository.delete(id);
        actionCounter.increment();
        timeEntrySummary.record(timeEntryRepository.list().size());
        return ResponseEntity.noContent().build();
    }
}
