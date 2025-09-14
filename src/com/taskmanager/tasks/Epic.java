package com.taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Represents an Epic task, which can contain multiple subtasks.
 * The Epic's duration, start time, and end time are calculated based on its subtasks.
 */
public class Epic extends Task {

    private static final Logger logger = Logger.getLogger(Epic.class.getName());

    public ArrayList<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        type = String.valueOf(TaskType.EPIC);
        checkDataTimeDurationEpic();
    }

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        type = String.valueOf(TaskType.EPIC);
        checkDataTimeDurationEpic();
    }

    /**
     * Returns the end time of the epic (based on the latest subtask).
     */
    public Optional<LocalDateTime> getEndTime() {
        return Optional.ofNullable(endTime);
    }

    /**
     * Disables manual start time assignment for epics.
     */
    @Override
    public void setStartTime(String newStartTime) {
        logger.warning("Manual start time assignment is not allowed for Epic.");
    }

    /**
     * Disables manual duration assignment for epics.
     */
    @Override
    public void setDuration(long newDuration) {
        logger.warning("Manual duration assignment is not allowed for Epic.");
    }

    /**
     * Recalculates start time, duration and end time for the epic.
     * Should be called whenever subtasks are modified.
     */
    public void checkDataTimeDurationEpic() {
        checkStartTimeEpic();
        checkDurationEpic();
        checkEndTimeEpic();
    }

    /**
     * Calculates the total duration of the epic as the sum of all subtask durations.
     */
    public void checkDurationEpic() {

        long sumDuration = 0;
        if (!subtasks.isEmpty()) {
            for (Subtask subtask : subtasks) {

                if (subtask.getDuration().isPresent()) {
                    if (subtask.getDuration().get().toMinutes() >= 0) {
                        sumDuration += subtask.getDuration().get().toMinutes();
                    }
                }
            }
            this.duration = Duration.ofMinutes(sumDuration);
            return;
        }
        this.duration = null;
    }

    /**
     * Calculates the earliest start time among all subtasks.
     */
    public void checkStartTimeEpic() {

        if (!subtasks.isEmpty()) {
            List<Subtask> sortedSubtasks = subtasks.stream().filter(subtask -> subtask.getStartTime().isPresent())
                    .sorted((st1, st2) -> {

                        if (st1.getStartTime().isPresent() && st2.getStartTime().isPresent()) {

                            if (st1.getStartTime().get().getYear() == st2.getStartTime().get().getYear()) {

                                if (st1.getStartTime().get().getMonthValue() == st2.getStartTime().get()
                                        .getMonthValue()) {

                                    if (st1.getStartTime().get().getDayOfMonth() == st2.getStartTime().get()
                                            .getDayOfMonth()) {

                                        if (st1.getStartTime().get().getHour() == st2.getStartTime().get()
                                                .getHour()) {

                                            if (st1.getStartTime().get().getMinute() == st2.getStartTime().get()
                                                    .getMinute()) {
                                                return 0;

                                            }
                                            return st1.getStartTime().get().getMinute() - st2.getStartTime().get()
                                                    .getMinute();
                                        }
                                        return st1.getStartTime().get().getHour() - st2.getStartTime().get()
                                                .getHour();
                                    }
                                    return st1.getStartTime().get().getDayOfMonth() - st2.getStartTime().get()
                                            .getDayOfMonth();
                                }
                                return st1.getStartTime().get().getMonthValue() - st2.getStartTime().get()
                                        .getMonthValue();
                            }
                            return st1.getStartTime().get().getYear() - st2.getStartTime().get().getYear();

                        } else {
                            logger.warning("Sorting fails.");
                            return 0;
                        }
                    })
                    .collect(Collectors.toList());

            if (!sortedSubtasks.isEmpty()) {
                if (sortedSubtasks.get(0).getStartTime().isPresent()) {
                    this.startTime = sortedSubtasks.get(0).getStartTime().get();
                    return;
                }
            }
        }
        this.startTime = null;
    }

    /**
     * Calculates the latest end time among all subtasks.
     */
    public void checkEndTimeEpic() {

        if (!subtasks.isEmpty()) {
            List<Subtask> sortedSubtasks = subtasks.stream().filter(subtask -> subtask.getEndTime().isPresent())
                    .sorted((st1, st2) -> {

                        if (st2.getEndTime().isPresent() && st1.getEndTime().isPresent()) {

                            if (st2.getEndTime().get().getYear() == st1.getEndTime().get().getYear()) {

                                if (st2.getEndTime().get().getMonthValue() == st1.getEndTime().get()
                                        .getMonthValue()) {

                                    if (st2.getEndTime().get().getDayOfMonth() == st1.getEndTime().get()
                                            .getDayOfMonth()) {

                                        if (st2.getEndTime().get().getHour() == st1.getEndTime().get()
                                                .getHour()) {

                                            if (st2.getEndTime().get().getMinute() == st1.getEndTime().get()
                                                    .getMinute()) {

                                                return 0;

                                            }
                                            return st2.getEndTime().get().getMinute() - st1.getEndTime().get()
                                                    .getMinute();
                                        }
                                        return st2.getEndTime().get().getHour() - st1.getEndTime().get()
                                                .getHour();
                                    }
                                    return st2.getEndTime().get().getDayOfMonth() - st1.getEndTime().get()
                                            .getDayOfMonth();
                                }
                                return st2.getEndTime().get().getMonthValue() - st1.getEndTime().get()
                                        .getMonthValue();
                            }
                            return st2.getEndTime().get().getYear() - st1.getEndTime().get().getYear();

                        } else {
                            logger.warning("Sorting fails.");
                            return 0;
                        }
                    })
                    .collect(Collectors.toList());

            if (!sortedSubtasks.isEmpty()) {
                if (sortedSubtasks.get(0).getEndTime().isPresent()) {

                    this.endTime = sortedSubtasks.get(0).getEndTime().get();
                    return;
                }
            }
        }
        this.endTime = null;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", id=" + this.getId() +
                ", status='" + this.getStatus() + '\'' +
                ", type='" + this.getType() + '\'' +
                ", startTime=" + this.getStartTime() + '\'' +
                ", duration=" + this.getDuration() + '\'' +
                ", endTime=" + this.getEndTime() + '\'' +
                '}';
    }
}
