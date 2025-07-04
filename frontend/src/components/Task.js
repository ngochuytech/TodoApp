import React, { useState, useEffect } from 'react';
import { format, parseISO, isPast } from 'date-fns';
import api from '../api';

const Task = ({ taskListId, onTaskChange }) => {
  const [userId, setUserId] = useState(sessionStorage.getItem('idUser') || '');
  const [tasks, setTasks] = useState([]);
  const [filteredTasks, setFilteredTasks] = useState([]);
  const [newTaskTitle, setNewTaskTitle] = useState('');
  const [newTaskDescription, setNewTaskDescription] = useState('');
  const [newTaskDueDate, setNewTaskDueDate] = useState('');
  const [editTask, setEditTask] = useState(null);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [filter, setFilter] = useState('all');

  useEffect(() => {
    if (taskListId) {
      fetchTasks();
    } else {
      setError('Invalid Task List ID');
    }
  }, [taskListId, filter]);

  const fetchTasks = async () => {
    try {
      const response = await api.get(`/api/tasks/task-list/${taskListId}`);
      const fetchedTasks = response.data;
      setTasks(fetchedTasks);
      if (filter === 'completed') {
        setFilteredTasks(fetchedTasks.filter(task => task.completed));
      } else if (filter === 'uncompleted') {
        setFilteredTasks(fetchedTasks.filter(task => !task.completed));
      } else if (filter === 'overdue') {
        setFilteredTasks(fetchedTasks.filter(task => isOverdue(task)));
      } else if (filter === 'not-overdue') {
        setFilteredTasks(fetchedTasks.filter(task => !isOverdue(task)));
      } else {
        setFilteredTasks(fetchedTasks);
      }
      setError('');
    } catch (err) {
      setError(err.response?.data || 'Failed to fetch tasks');
    }
  };

  const handleAddTask = async (e) => {
    e.preventDefault();
    if (!newTaskTitle.trim()) {
      setError('Task title cannot be empty');
      return;
    }
    try {
      const response = await api.post(`/api/tasks`, {
        title: newTaskTitle,
        description: newTaskDescription,
        due_date: newTaskDueDate || null,
        completed: false,
        task_list_id: taskListId,
        user_id: userId,
      });
      const newTask = response.data;
      const updatedTasks = [...tasks, newTask];
      setTasks(updatedTasks);
      if (
        filter === 'all' ||
        (filter === 'uncompleted' && !newTask.completed) ||
        (filter === 'overdue' && isOverdue(newTask)) ||
        (filter === 'not-overdue' && !isOverdue(newTask))
      ) {
        setFilteredTasks([...filteredTasks, newTask]);
      }
      setNewTaskTitle('');
      setNewTaskDescription('');
      setNewTaskDueDate('');
      setShowModal(false);
      setError('');
      onTaskChange();
    } catch (err) {
      setError(err.response?.data || 'Failed to create task');
    }
  };

  const handleUpdateTask = async (e) => {
    e.preventDefault();
    if (!editTask?.title?.trim()) {
      setError('Task title cannot be empty');
      return;
    }
    if (!editTask.id) {
      setError('Task ID is missing');
      return;
    }
    try {
      const response = await api.put(`/api/tasks/${editTask.id}`, {
        title: editTask.title,
        description: editTask.description || '',
        due_date: editTask.due_date || null,
        completed: editTask.completed,
        task_list_id: taskListId,
        user_id: userId,
      });
      const updatedTask = { ...editTask, ...response.data };
      const updatedTasks = tasks.map((task) => (task.id === editTask.id ? updatedTask : task));
      setTasks(updatedTasks);
      if (filter === 'all') {
        setFilteredTasks(updatedTasks);
      } else if (filter === 'completed' && updatedTask.completed) {
        setFilteredTasks(updatedTasks.filter(task => task.completed));
      } else if (filter === 'uncompleted' && !updatedTask.completed) {
        setFilteredTasks(updatedTasks.filter(task => !task.completed));
      } else if (filter === 'overdue' && isOverdue(updatedTask)) {
        setFilteredTasks(updatedTasks.filter(task => isOverdue(task)));
      } else if (filter === 'not-overdue' && !isOverdue(updatedTask)) {
        setFilteredTasks(updatedTasks.filter(task => !isOverdue(task)));
      } else {
        setFilteredTasks(updatedTasks.filter(task => task.id !== updatedTask.id));
      }
      setEditTask(null);
      setShowModal(false);
      setError('');
      onTaskChange();
    } catch (err) {
      setError(err.response?.data || 'Failed to update task');
    }
  };

  const handleDeleteTask = async (id) => {
    try {
      await api.delete(`/api/tasks/${id}`);
      const updatedTasks = tasks.filter((task) => task.id !== id);
      setTasks(updatedTasks);
      if (filter === 'all') {
        setFilteredTasks(updatedTasks);
      } else if (filter === 'completed') {
        setFilteredTasks(updatedTasks.filter(task => task.completed));
      } else if (filter === 'uncompleted') {
        setFilteredTasks(updatedTasks.filter(task => !task.completed));
      } else if (filter === 'overdue') {
        setFilteredTasks(updatedTasks.filter(task => isOverdue(task)));
      } else if (filter === 'not-overdue') {
        setFilteredTasks(updatedTasks.filter(task => !isOverdue(task)));
      }
      setError('');
      onTaskChange();
    } catch (err) {
      setError(err.response?.data || 'Failed to delete task');
    }
  };

  const openAddModal = () => {
    setNewTaskTitle('');
    setNewTaskDescription('');
    setNewTaskDueDate('');
    setEditTask(null);
    setShowModal(true);
  };

  const openEditModal = (task) => {
    if (!task.id) {
      setError('Invalid task ID');
      return;
    }
    setEditTask({
      ...task,
      due_date: task.due_date ? format(parseISO(task.due_date), "yyyy-MM-dd'T'HH:mm") : '',
    });
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setError('');
  };

  const formatDate = (date) => {
    if (!date) return 'N/A';
    try {
      return format(parseISO(date), 'MMM dd, yyyy HH:mm');
    } catch {
      return 'Invalid date';
    }
  };

  const isOverdue = (task) => {
    if (!task.due_date || task.completed) return false;
    try {
      const dueDate = parseISO(task.due_date);
      return isPast(dueDate);
    } catch {
      return false;
    }
  };

  return (
    <div className="task-container">
      {error && (
        <div className="alert alert-danger alert-dismissible fade show" role="alert">
          {error}
          <button type="button" className="btn-close" onClick={() => setError('')}></button>
        </div>
      )}

      <div className="mb-3">
        <label htmlFor="taskFilter" className="form-label me-2">Filter Tasks:</label>
        <select
          id="taskFilter"
          className="form-select w-auto d-inline-block"
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
        >
          <option value="all">All Tasks</option>
          <option value="completed">Completed Tasks</option>
          <option value="uncompleted">Uncompleted Tasks</option>
          <option value="overdue">Overdue Tasks</option>
          <option value="not-overdue">Not Overdue Tasks</option>
        </select>
        <button
          className="btn btn-success ms-2"
          onClick={openAddModal}
          disabled={!taskListId}
        >
          <i className="bi bi-plus-circle me-1"></i> Add New Task
        </button>
      </div>

      <ul className="list-group">
        {filteredTasks.length === 0 ? (
          <li
            key="empty-state"
            className="list-group-item text-center text-muted d-flex align-items-center justify-content-center"
            style={{ minHeight: '100px' }}
          >
            <i className="bi bi-list-task me-2"></i> No tasks in this list. Add a new task!
          </li>
        ) : (
          filteredTasks.map((task) => (
            <li
              key={task.id}
              className={`list-group-item d-flex justify-content-between align-items-start task-item ${isOverdue(task) ? 'overdue-task' : ''}`}
            >
              <div>
                <div className="d-flex align-items-center">
                  <input
                    type="checkbox"
                    checked={task.completed}
                    onChange={async () => {
                      try {
                        const response = await api.put(`/api/tasks/${task.id}`, {
                          title: task.title,
                          description: task.description || '',
                          due_date: task.due_date || null,
                          completed: !task.completed,
                          task_list_id: taskListId,
                          user_id: userId,
                        });
                        const updatedTask = response.data;
                        const updatedTasks = tasks.map((t) => (t.id === task.id ? updatedTask : t));
                        setTasks(updatedTasks);
                        if (filter === 'all') {
                          setFilteredTasks(updatedTasks);
                        } else if (filter === 'completed' && updatedTask.completed) {
                          setFilteredTasks(updatedTasks.filter(t => t.completed));
                        } else if (filter === 'uncompleted' && !updatedTask.completed) {
                          setFilteredTasks(updatedTasks.filter(t => !t.completed));
                        } else if (filter === 'overdue' && isOverdue(updatedTask)) {
                          setFilteredTasks(updatedTasks.filter(t => isOverdue(t)));
                        } else if (filter === 'not-overdue' && !isOverdue(updatedTask)) {
                          setFilteredTasks(updatedTasks.filter(t => !isOverdue(t)));
                        } else {
                          setFilteredTasks(updatedTasks.filter(t => t.id !== updatedTask.id));
                        }
                        onTaskChange();
                      } catch (err) {
                        setError(err.response?.data || 'Failed to update task');
                      }
                    }}
                    className="me-2"
                  />
                  <span
                    style={{
                      textDecoration: task.completed ? 'line-through' : 'none',
                      fontWeight: 'bold',
                    }}
                  >
                    {task.title}
                  </span>
                </div>
                {task.description && (
                  <p className="task-description mt-1 mb-0">
                    {task.description.length > 50
                      ? `${task.description.substring(0, 50)}...`
                      : task.description}
                    {task.description.length > 50 && (
                      <button
                        className="btn btn-link btn-sm p-0 ms-1"
                        onClick={() => openEditModal(task)}
                      >
                        View more
                      </button>
                    )}
                  </p>
                )}
                <p className="text-muted small mt-1 mb-0">
                  <span className="me-3">
                    <i className="bi bi-calendar me-1"></i>
                    Created: {formatDate(task.created_at)}
                  </span>
                  <span>
                    <i className="bi bi-calendar-check me-1"></i>
                    Due: {formatDate(task.due_date)}
                  </span>
                </p>
              </div>
              <div>
                <button
                  className="btn btn-warning btn-sm me-2"
                  onClick={() => openEditModal(task)}
                >
                  <i className="bi bi-pencil me-1"></i> Edit
                </button>
                <button
                  className="btn btn-danger btn-sm"
                  onClick={() => handleDeleteTask(task.id)}
                >
                  <i className="bi bi-trash me-1"></i> Delete
                </button>
              </div>
            </li>
          ))
        )}
      </ul>

      <div className={`modal fade ${showModal ? 'show d-block' : ''}`} tabIndex="-1">
        <div className="modal-dialog modal-dialog-centered">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">{editTask ? 'Edit Task' : 'Add Task'}</h5>
              <button type="button" className="btn-close" onClick={closeModal}></button>
            </div>
            <form onSubmit={editTask ? handleUpdateTask : handleAddTask}>
              <div className="modal-body">
                <div className="mb-3">
                  <label htmlFor="taskTitle" className="form-label">
                    Title
                  </label>
                  <input
                    type="text"
                    className="form-control"
                    id="taskTitle"
                    value={editTask ? editTask.title : newTaskTitle}
                    onChange={(e) =>
                      editTask
                        ? setEditTask({ ...editTask, title: e.target.value })
                        : setNewTaskTitle(e.target.value)
                    }
                    required
                  />
                </div>
                <div className="mb-3">
                  <label htmlFor="taskDescription" className="form-label">
                    Description
                  </label>
                  <textarea
                    className="form-control"
                    id="taskDescription"
                    rows="4"
                    value={editTask ? editTask.description || '' : newTaskDescription}
                    onChange={(e) =>
                      editTask
                        ? setEditTask({ ...editTask, description: e.target.value })
                        : setNewTaskDescription(e.target.value)
                    }
                  ></textarea>
                </div>
                <div className="mb-3">
                  <label htmlFor="taskDueDate" className="form-label">
                    Due Date
                  </label>
                  <input
                    type="datetime-local"
                    className="form-control"
                    id="taskDueDate"
                    value={editTask ? editTask.due_date : newTaskDueDate}
                    onChange={(e) =>
                      editTask
                        ? setEditTask({ ...editTask, due_date: e.target.value })
                        : setNewTaskDueDate(e.target.value)
                    }
                  />
                </div>
                {editTask && (
                  <div className="form-check mb-3">
                    <input
                      type="checkbox"
                      className="form-check-input"
                      id="taskCompleted"
                      checked={editTask.completed}
                      onChange={(e) =>
                        setEditTask({ ...editTask, completed: e.target.checked })
                      }
                    />
                    <label className="form-check-label" htmlFor="taskCompleted">
                      Completed
                    </label>
                  </div>
                )}
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={closeModal}>
                  Close
                </button>
                <button type="submit" className="btn btn-primary">
                  {editTask ? 'Save Changes' : 'Add Task'}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
      {showModal && <div className="modal-backdrop fade show"></div>}
    </div>
  );
};

export default Task;