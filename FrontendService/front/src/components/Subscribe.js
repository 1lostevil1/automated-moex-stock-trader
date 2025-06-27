import React, { useState, useEffect, useRef } from 'react';
import { Line } from 'react-chartjs-2';
import { useNavigate } from 'react-router-dom';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  TimeScale,
} from 'chart.js';
import 'chartjs-adapter-date-fns';
import axios from 'axios';
import '..//styles/Subscribe.css';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  TimeScale
);

const Subscribe = () => {
  const navigate = useNavigate();
  const [stocks, setStocks] = useState([]);
  const [subscribedStocks, setSubscribedStocks] = useState([]);
  const [selectedStock, setSelectedStock] = useState(null);
  const [chartData, setChartData] = useState({});
  const [notification, setNotification] = useState(null);
  const stompClient = useRef(null);
  const jwtToken = localStorage.getItem('token');

  useEffect(() => {
    fetchStocks();
    if (jwtToken) fetchSubscribedStocks();
    setupWebSocket();

    return () => {
      if (stompClient.current) {
        stompClient.current.disconnect();
      }
    };
  }, [jwtToken]);

  const fetchStocks = async () => {
    try {
      const response = await axios.get('/api/stocks');
      setStocks(response.data);
      if (response.data.length > 0) {
        setSelectedStock(response.data[0].ticker);
      }
    } catch (error) {
      console.error('Ошибка загрузки акций:', error);
    }
  };

  const fetchSubscribedStocks = async () => {
    try {
      const response = await axios.get('/api/secured/stocksByJwt', {
        headers: { Authorization: jwtToken }
      });
      setSubscribedStocks(response.data.map(stock => stock.ticker));
    } catch (error) {
      console.error('Ошибка загрузки подписок:', error);
    }
  };

  const setupWebSocket = () => {
    const socket = new SockJS('/ws');
    stompClient.current = Stomp.over(socket);
    stompClient.current.connect({}, () => {
      stompClient.current.subscribe('/topic/forecastResponse', (message) => {
        const newData = JSON.parse(message.body);
        updateChartData(newData);
      });
    });
  };

  const updateChartData = (forecastData) => {
    const stockData = forecastData.filter(item => item.ticker === selectedStock);
    if (stockData.length === 0) return;

    const labels = stockData.map(item => new Date(item.timing));
    const actualPrices = stockData.map(item => item.lastPrice);
    const predictedPrices = stockData.map(item => item.closePrice);

    const shiftedPredictedPrices = predictedPrices.map((_, i, arr) => 
      i > 0 ? arr[i-1] : null
    );

    shiftedPredictedPrices.push(predictedPrices[predictedPrices.length-1]);
    labels.push(new Date(labels[labels.length-1].getTime() + 60000));

    setChartData({
      labels,
      datasets: [
        {
          label: 'Фактическая цена',
          data: actualPrices,
          borderColor: '#00ff00',
          backgroundColor: 'rgba(0, 255, 0, 0.1)',
          tension: 0.1,
          pointRadius: 3,
        },
        {
          label: 'Прогнозируемая цена',
          data: shiftedPredictedPrices,
          borderColor: '#ff00ff',
          backgroundColor: 'rgba(255, 0, 255, 0.1)',
          borderDash: [5, 5],
          tension: 0.1,
          pointRadius: 3,
        },
      ],
    });
  };

  const handleStockSelect = (ticker) => {
    setSelectedStock(ticker);
  };

  const handleSubscribe = async (ticker, isSubscribed) => {
    try {
      const endpoint = isSubscribed ? '/api/secured/unsubscribe' : '/api/secured/subscribe';
      const response = await axios.post(
        endpoint,
        { ticker },
        { headers: { Authorization: jwtToken } }
      );

      if (response.status === 200) {
        showNotification(response.data, false);
        updateSubscriptionStatus(ticker, isSubscribed);
      }
    } catch (error) {
      showNotification(error.response?.data || 'Ошибка подписки', true);
    }
  };

  const showNotification = (message, isError) => {
    setNotification({ message, isError });
    setTimeout(() => setNotification(null), 3000);
  };

  const updateSubscriptionStatus = (ticker, isSubscribed) => {
    if (isSubscribed) {
      setSubscribedStocks(subscribedStocks.filter(t => t !== ticker));
    } else {
      setSubscribedStocks([...subscribedStocks, ticker]);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/');
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      x: {
        type: 'time',
        time: {
          unit: 'minute',
          displayFormats: {
            minute: 'HH:mm',
          },
        },
        title: {
          display: true,
          text: 'Время',
          color: '#fff',
        },
        ticks: {
          color: '#fff',
        },
        grid: {
          color: 'rgba(255, 255, 255, 0.1)',
        },
      },
      y: {
        title: {
          display: true,
          text: 'Цена ($)',
          color: '#fff',
        },
        ticks: {
          color: '#fff',
        },
        grid: {
          color: 'rgba(255, 255, 255, 0.1)',
        },
      },
    },
    plugins: {
      legend: {
        position: 'top',
        labels: {
          color: '#fff',
        },
      },
      tooltip: {
        mode: 'index',
        intersect: false,
        callbacks: {
          label: function(context) {
            const label = context.dataset.label || '';
            if (context.parsed.y !== null) {
              const time = new Date(context.label);
              const timeStr = time.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
              
              if (label.includes('Прогнозируемая')) {
                const nextTime = new Date(time.getTime() + 60000);
                const nextTimeStr = nextTime.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
                return `${label} (на ${nextTimeStr}): $${context.parsed.y.toFixed(2)}`;
              }
              return `${label} (в ${timeStr}): $${context.parsed.y.toFixed(2)}`;
            }
            return label;
          }
        }
      },
    },
    interaction: {
      mode: 'nearest',
      axis: 'x',
      intersect: false,
    },
  };

  return (
    <div className="subscribe-app">
      <div className="subscribe-container">
        <div className="chart-section">
          <h2 className="chart-title">
            {selectedStock} - Прогноз цен
          </h2>
          <div className="chart-container">
            {chartData.labels ? (
              <Line data={chartData} options={chartOptions} />
            ) : (
              <div className="chart-loading">
                Загрузка данных...
              </div>
            )}
          </div>
        </div>

        <div className="stocks-section-wrapper">
          <div className="stocks-section">
            <h2 className="stocks-title">
              Доступные акции
            </h2>
            <div className="stocks-list">
              {stocks.map((stock) => {
                const isSubscribed = subscribedStocks.includes(stock.ticker);
                const isSelected = selectedStock === stock.ticker;
                return (
                  <div 
                    key={stock.ticker}
                    className={`stock-item ${isSelected ? 'selected' : ''}`}
                    onClick={() => handleStockSelect(stock.ticker)}
                  >
                    <div className="stock-info">
                      <div className="stock-ticker">
                        {stock.ticker}
                      </div>
                      <div className="stock-name">
                        {stock.name}
                      </div>
                    </div>
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        handleSubscribe(stock.ticker, isSubscribed);
                      }}
                      className={`subscribe-btn ${isSubscribed ? 'unsubscribed' : 'subscribed'}`}
                    >
                      {isSubscribed ? 'Отписаться' : 'Подписаться'}
                    </button>
                  </div>
                );
              })}
            </div>
          </div>
          <button 
            onClick={handleLogout}
            className="logout-btn"
          >
            Выйти в главное меню
          </button>
        </div>
      </div>

      {notification && (
        <div className={`notification ${notification.isError ? 'error' : 'success'}`}>
          {notification.message}
        </div>
      )}
    </div>
  );
};

export default Subscribe;