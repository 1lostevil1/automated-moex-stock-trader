import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '..//styles/Register.css';

const Register = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [telegramUsername, setTelegramUsername] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const response = await axios.post('/api/signup', {
        username,
        password,
        telegramUsername
      });

      if (response.status === 200) {
        navigate('/');
      }
    } catch (error) {
      if (error.response) {
        if (error.response.status === 400) {
          setError('Пользователь с указанным именем уже существует');
        } else {
          setError('Ошибка регистрации, повторите позже');
        }
      } else if (error.request) {
        setError('Ошибка соединения с сервером');
      } else {
        setError('Неизвестная ошибка');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleLoginRedirect = () => {
    navigate('/');
  };

  return (
    <div className="register-app">
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

      <div className="register-content">
        <div className="register-header">
          <div className="register-title-light">LIGHT</div>
          <div className="register-title-cash">CA<span className="dollar-sign">$</span>H</div>
        </div>
        
        <form onSubmit={handleSubmit} className="register-form">
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

          <div className="input-container">
            <input
              type="text"
              id="telegramUsername"
              value={telegramUsername}
              onChange={(e) => setTelegramUsername(e.target.value)}
              placeholder="Telegram username"
              required
              disabled={isLoading}
            />
          </div>
          
          <button type="submit" className="register-submit-btn" disabled={isLoading}>
            {isLoading ? 'ЗАГРУЗКА...' : 'ЗАРЕГИСТРИРОВАТЬСЯ'}
          </button>
          
          {error && <div className="register-error">{error}</div>}
        </form>
        
        <button 
          onClick={handleLoginRedirect} 
          className="register-login-btn"
          disabled={isLoading}
        >
          УЖЕ ЕСТЬ АККАУНТ? ВОЙТИ
        </button>
      </div>
    </div>
  );
};

export default Register;