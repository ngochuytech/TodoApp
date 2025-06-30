package com.nguyenhuy.todo_app.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.nguyenhuy.todo_app.models.Task;
import com.nguyenhuy.todo_app.repositories.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskReminderService {
    private final TaskRepository taskRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 * * * * *")
    public void remindOverdueTasks() {
            List<Task> overdueTasks = taskRepository.findAll().stream()
                .filter(task -> !task.isCompleted()
                    && task.getDueDate() != null
                    && task.getDueDate().isBefore(LocalDateTime.now())
                    && !task.isReminded())
                .toList();

            for (Task task : overdueTasks) {
                String to = task.getUser().getEmail();
                String subject = "Task Overdue Reminder";
                String body = "Hello " + task.getUser().getFullName() + ",\n\n"
                            + "Your task \"" + task.getTitle() + "\" is overdue. Please check your ToDo App.";
                emailService.sendEmail(to, subject, body);

                task.setReminded(true);
                taskRepository.save(task);
            }
    }
}
