import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

const Header = () => {
  const [username, setUsername] = useState(sessionStorage.getItem('username') || '');
  const userId = sessionStorage.getItem('idUser') || '';
  const navigate = useNavigate();

  // Fetch username from API if not in sessionStorage
  useEffect(() => {
    
    if (!username && userId) {
      const fetchUsername = async () => {
        try {
          const response = await api.get(`/api/users/${userId}`);
          setUsername(response.data.username || 'User');
          sessionStorage.setItem('username', response.data.username || 'User');
        } catch (err) {
          console.error('Failed to fetch username:', err);
          setUsername('User');
        }
      };
      fetchUsername();
    }
  }, [userId]);

  const handleLogout = () => {
    sessionStorage.removeItem('idUser');
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('username');
    navigate('/login');
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light mb-4">
      <div className="container-fluid">
        <span className="navbar-brand">Welcome, {username || 'User'}</span>
        <div className="d-flex">
          <button className="btn btn-danger" onClick={handleLogout}>
            <i className="bi bi-box-arrow-right me-1"></i> Logout
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Header;