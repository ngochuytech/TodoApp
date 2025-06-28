import axios from 'axios';
import {jwtDecode} from 'jwt-decode';

// Tạo instance Axios với base URL
const api = axios.create({
  baseURL: 'http://localhost:8080',
});

// Interceptor để thêm header Authorization vào mỗi request
api.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('token');
    console.log("Token in interceptor:", token);
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      sessionStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const getIdUserFromToken = () => {
  const token = sessionStorage.getItem('token');
  if (token) {
    try {
      const decoded = jwtDecode(token);
      return decoded.idUser;
    } catch (error) {
      console.error('Invalid token', error);
      return null;
    }
  }
  return null;
};

export const getUserNameFromToken = () => {
  const token = sessionStorage.getItem('token');
  if (token) {
    try {
      const decoded = jwtDecode(token);
      return decoded.fullName;
    } catch (error) {
      console.error('Invalid token', error);
      return null;
    }
  }
  return null;
};

export const getEmailFromToken = () => {
  const token = sessionStorage.getItem('token');
  if (token) {
    try {
      const decoded = jwtDecode(token);
      return decoded.email;
    } catch (error) {
      console.error('Invalid token', error);
      return null;
    }
  }
  return null;
}

export default api;