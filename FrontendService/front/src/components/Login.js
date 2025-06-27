import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/Login.css';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const response = await axios.post('http://localhost:9888/api/authenticate', {
        username: username,
        password: password,
      });

      if (response.status === 200) {
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('username', username);
        navigate('/subscribe');
      }
    } catch (error) {
      if (error.response) {
        setError(error.response.status === 401 
          ? 'Неверный логин или пароль' 
          : 'Ошибка аутентификации');
      } else {
        setError('Ошибка соединения');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleRegisterRedirect = () => {
    navigate('/register');
  };

  return (
    <div className="login-app">
      <div className="dollar-bills">
        {[...Array(25)].map((_, i) => (
          <div key={i} className="dollar-bill" style={{ 
            left: `${Math.random() * 100}%`,
            animationDelay: `${Math.random() * 2}s`,
            animationDuration: `${5 + Math.random() * 10}s`,
          }}>
            <div className="bill-rectangle">
              <div className="bill-circle">
                <span className="bill-dollar">$</span>
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="login-content">
        <div className="login-header">
          <div className="login-title-light">LIGHT</div>
          <div className="login-title-cash">CA<span className="dollar-sign">$</span>H</div>
        </div>
        
        <form onSubmit={handleSubmit} className="login-form">
          <div className="input-container">
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Логин"
              required
              disabled={isLoading}
            />
          </div>
          
          <div className="input-container">
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Пароль"
              required
              disabled={isLoading}
            />
          </div>
          
          <button type="submit" className="login-submit-btn" disabled={isLoading}>
            {isLoading ? 'ЗАГРУЗКА...' : 'ВОЙТИ'}
          </button>
          
          {error && <div className="login-error">{error}</div>}
        </form>
        
        <button 
          onClick={handleRegisterRedirect} 
          className="login-register-btn"
          disabled={isLoading}
        >
          СОЗДАТЬ АККАУНТ
        </button>
      </div>
    </div>
  );
};

export default Login;