package com.nguyenhuy.todo_app.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task extends BaseEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "title", length = 200, nullable = false)
   private String title;

   @Column(name = "description", nullable = false)
   private String description;

   @Column(name = "due_date")
   private LocalDateTime dueDate;
   
   @Column(name = "completed", nullable = false)
   private boolean completed;

   @ManyToOne
   @JoinColumn(name = "task_list_id", nullable = false)
   private TaskList taskList;

   @ManyToOne
   @JoinColumn(name = "user_id", nullable = false)
   private User user;
}
