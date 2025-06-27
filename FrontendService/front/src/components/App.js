import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '..//styles/App.css';

const App = () => {
  const [lightText, setLightText] = useState('');
  const [cashText, setCashText] = useState('');
  const [showCursor, setShowCursor] = useState(false);
  const [dollarIndex, setDollarIndex] = useState(-1); // Позиция $ в тексте
  const [buttonsVisible, setButtonsVisible] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    setShowCursor(true);
    
    // Анимация печати LIGHT
    const lightWord = 'LIGHT';
    let lightIndex = 0;
    
    const lightInterval = setInterval(() => {
      if (lightIndex < lightWord.length) {
        setLightText(lightWord.substring(0, lightIndex + 1));
        lightIndex++;
      } else {
        clearInterval(lightInterval);
        
        setTimeout(() => {
          // Анимация печати CA$H
          const cashWord = 'CA$H';
          let cashIndex = 0;
          
          const cashInterval = setInterval(() => {
            if (cashIndex < cashWord.length) {
              const newText = cashWord.substring(0, cashIndex + 1);
              setCashText(newText);
              
              // Если дошли до $, запоминаем его позицию
              if (cashWord[cashIndex] === '$') {
                setDollarIndex(cashIndex);
              }
              
              cashIndex++;
            } else {
              clearInterval(cashInterval);
              setShowCursor(false);
              
              // Запускаем анимацию денежного дождя и кнопок
              setTimeout(() => {
                setButtonsVisible(true);
              }, 300);
            }
          }, 150);
        }, 500);
      }
    }, 150);

    return () => {
      clearInterval(lightInterval);
    };
  }, []);

  const handleLogin = () => navigate('/login');
  const handleRegister = () => navigate('/register');

  // Функция для рендеринга символов с обработкой $
  const renderCashText = () => {
    return cashText.split('').map((char, index) => {
      if (char === '$' && index === dollarIndex) {
        return (
          <span key={index} className="dollar-char animate">
            {char}
          </span>
        );
      }
      return char;
    });
  };

  return (
    <div className="app">
      <div className="dollar-bills">
        {[...Array(25)].map((_, i) => (
          <div key={i} className="dollar-bill" style={{ 
            left: `${Math.random() * 100}%`,
            animationDelay: `${buttonsVisible ? 0 : 5 + Math.random() * 2}s`,
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
      
      <div className="content">
        <div className="text-container">
          <div className="text light">
            {lightText}
            {showCursor && lightText.length < 5 && <span className="cursor">|</span>}
          </div>
          
          <div className="text cash">
            {renderCashText()}
            {showCursor && cashText.length > 0 && cashText.length < 4 && <span className="cursor">|</span>}
          </div>
        </div>
        
        <div className={`buttons ${buttonsVisible ? 'visible' : ''}`}>
          <button onClick={handleLogin} className="login-btn">Войти</button>
          <button onClick={handleRegister} className="register-btn">Регистрация</button>
        </div>
      </div>
    </div>
  );
};

export default App;