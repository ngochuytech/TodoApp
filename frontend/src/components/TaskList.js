import React, { useState, useEffect } from 'react';
import api, { getIdUserFromToken } from '../api';
import Task from './Task';
import Header from './Header';
import { Collapse } from 'bootstrap';

const TaskList = () => {
  const [taskLists, setTaskLists] = useState([]);
  const [newListTitle, setNewListTitle] = useState('');
  const [editList, setEditList] = useState(null);
  const [userId, setUserId] = useState(sessionStorage.getItem('idUser') || '');
  const [error, setError] = useState('');
  const [showEditModal, setShowEditModal] = useState(false);
  const [completedTaskLists, setCompletedTaskLists] = useState({}); // Track completed task lists

  useEffect(() => {
    fetchTaskLists();
  }, []);

  useEffect(() => {
    const accordionItems = document.querySelectorAll('.accordion-collapse');
    accordionItems.forEach((item) => {
      new Collapse(item, {
        toggle: false,
        parent: '#taskListAccordion',
      });
    });
  }, [taskLists]);

  const fetchTaskLists = async () => {
    try {
      const response = await api.get(`/api/task-lists/${userId}`);
      console.log('TaskLists fetched:', response.data);
      const lists = response.data;
      setTaskLists(lists);
      // Check completion status for each task list
      const completionStatus = {};
      for (const list of lists) {
        const tasksResponse = await api.get(`/api/tasks/task-list/${list.id}`);
        const tasks = tasksResponse.data;
        completionStatus[list.id] = tasks.length > 0 && tasks.every(task => task.completed);
      }
      setCompletedTaskLists(completionStatus);
      setError('');
    } catch (err) {
      setError(err.response?.data || 'Failed to fetch task lists');
    }
  };

  const handleAddTaskList = async (e) => {
    e.preventDefault();
    if (!newListTitle.trim()) {
      setError('Task list title cannot be empty');
      return;
    }
    try {
      const response = await api.post('/api/task-lists', {
        title: newListTitle,
        user_id: userId,
      });
      const newList = response.data;
      setTaskLists([...taskLists, newList]);
      setCompletedTaskLists({ ...completedTaskLists, [newList.id]: true });
      setNewListTitle('');
      setError('');
    } catch (err) {
      setError(err.response?.data || 'Failed to create task list');
    }
  };

  const handleUpdateTaskList = async (e) => {
    e.preventDefault();
    if (!editList?.title?.trim()) {
      setError('Task list title cannot be empty');
      return;
    }
    try {
      const response = await api.put(`/api/task-lists/${editList.id}`, {
        title: editList.title,
        user_id: userId,
      });
      setTaskLists(taskLists.map((list) => (list.id === editList.id ? response.data : list)));
      setEditList(null);
      setShowEditModal(false);
      setError('');
    } catch (err) {
      setError(err.response?.data || 'Failed to update task list');
    }
  };

  const handleDeleteTaskList = async (id) => {
    try {
      await api.delete(`/api/task-lists/${id}`);
      setTaskLists(taskLists.filter((list) => list.id !== id));
      const updatedCompletedTaskLists = { ...completedTaskLists };
      delete updatedCompletedTaskLists[id];
      setCompletedTaskLists(updatedCompletedTaskLists);
      setError('');
    } catch (err) {
      setError(err.response?.data || 'Failed to delete task list');
    }
  };

  const openEditModal = (list) => {
    setEditList({ ...list });
    setShowEditModal(true);
  };

  const closeEditModal = () => {
    setEditList(null);
    setShowEditModal(false);
    setError('');
  };

  const handleTaskChange = async (taskListId) => {
    try {
      const tasksResponse = await api.get(`/api/tasks/task-list/${taskListId}`);
      const tasks = tasksResponse.data;
      setCompletedTaskLists({
        ...completedTaskLists,
        [taskListId]: tasks.length > 0 && tasks.every(task => task.completed),
      });
    } catch (err) {
      console.error('Failed to refresh task list status:', err);
    }
  };

  return (
    <div className="task-list-container">
      <Header />
      <h2 className="text-center mb-4">My Task Lists</h2>
      {error && (
        <div className="alert alert-danger alert-dismissible fade show" role="alert">
          {error}
          <button type="button" className="btn-close" onClick={() => setError('')}></button>
        </div>
      )}

      <form onSubmit={handleAddTaskList} className="mb-4">
        <div className="input-group">
          <input
            type="text"
            className="form-control"
            placeholder="Enter new task list title"
            value={newListTitle}
            onChange={(e) => setNewListTitle(e.target.value)}
          />
          <button type="submit" className="btn btn-primary">
            <i className="bi bi-plus-circle me-1"></i> Add Task List
          </button>
        </div>
      </form>

      <div className="accordion" id="taskListAccordion">
        {taskLists.length === 0 ? (
          <div className="text-center text-muted my-4">
            <i className="bi bi-list-task me-2"></i> You have no task lists yet. Create one!
          </div>
        ) : (
          taskLists.map((list, index) => (
            <div
              key={list.id}
              className={`accordion-item ${completedTaskLists[list.id] ? 'completed-task-list' : ''}`}
            >
              <h2 className="accordion-header" id={`heading${list.id}`}>
                <button
                  className="accordion-button"
                  type="button"
                  data-bs-toggle="collapse"
                  data-bs-target={`#collapse${list.id}`}
                  aria-expanded={index === 0 ? 'true' : 'false'}
                  aria-controls={`collapse${list.id}`}
                >
                  {list.title}
                </button>
              </h2>
              <div
                id={`collapse${list.id}`}
                className={`accordion-collapse collapse ${index === 0 ? 'show' : ''}`}
                aria-labelledby={`heading${list.id}`}
                data-bs-parent="#taskListAccordion"
              >
                <div className="accordion-body">
                  <div className="d-flex justify-content-end mb-3">
                    <button
                      className="btn btn-warning btn-sm me-2"
                      onClick={() => openEditModal(list)}
                    >
                      <i className="bi bi-pencil me-1"></i> Edit
                    </button>
                    <button
                      className="btn btn-danger btn-sm"
                      onClick={() => handleDeleteTaskList(list.id)}
                    >
                      <i className="bi bi-trash me-1"></i> Delete
                    </button>
                  </div>
                  {list.id && <Task taskListId={list.id} onTaskChange={() => handleTaskChange(list.id)} />}
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      <div className={`modal fade ${showEditModal ? 'show d-block' : ''}`} tabIndex="-1">
        <div className="modal-dialog modal-dialog-centered">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">Edit Task List</h5>
              <button type="button" className="btn-close" onClick={closeEditModal}></button>
            </div>
            <form onSubmit={handleUpdateTaskList}>
              <div className="modal-body">
                <div className="mb-3">
                  <label htmlFor="taskListTitle" className="form-label">
                    Title
                  </label>
                  <input
                    type="text"
                    className="form-control"
                    id="taskListTitle"
                    value={editList ? editList.title : ''}
                    onChange={(e) =>
                      setEditList({ ...editList, title: e.target.value })
                    }
                    required
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={closeEditModal}>
                  Close
                </button>
                <button type="submit" className="btn btn-primary">
                  Save Changes
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
      {showEditModal && <div className="modal-backdrop fade show"></div>}
    </div>
  );
};

export default TaskList;