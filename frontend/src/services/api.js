import axios from 'axios';
export default api;

);
  }
    return Promise.reject(error);
    }
      window.location.href = '/login';
      localStorage.removeItem('token');
      // Token expirado ou inválido
    if (error.response?.status === 401) {
  (error) => {
  (response) => response,
api.interceptors.response.use(
// Interceptor para tratar erros de resposta

);
  }
    return Promise.reject(error);
  (error) => {
  },
    return config;
    }
      config.headers.Authorization = `Bearer ${token}`;
    if (token) {
    const token = localStorage.getItem('token');
  (config) => {
api.interceptors.request.use(
// Interceptor para adicionar token de autenticação quando necessário

});
  },
    'Content-Type': 'application/json',
  headers: {
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
const api = axios.create({


