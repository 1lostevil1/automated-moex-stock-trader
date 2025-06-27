import React, { useState, useEffect } from 'react';
import { Line } from 'react-chartjs-2';
import { useNavigate } from 'react-router-dom';
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

// Моковые данные для акций с начальными ценами
const mockStocks = [
  { ticker: 'AAPL', name: 'Apple Inc.', lastPrice: 189.37, predictedPrice: 190.12 },
  { ticker: 'GOOGL', name: 'Alphabet Inc.', lastPrice: 152.33, predictedPrice: 153.05 },
  { ticker: 'MSFT', name: 'Microsoft Corporation', lastPrice: 420.72, predictedPrice: 422.18 },
  { ticker: 'AMZN', name: 'Amazon.com Inc.', lastPrice: 178.75, predictedPrice: 179.40 },
  { ticker: 'TSLA', name: 'Tesla Inc.', lastPrice: 248.48, predictedPrice: 250.15 }
];

// Моковые подписки пользователя
const mockSubscribedStocks = ['AAPL', 'MSFT'];

// Генерация данных с правильным временным смещением
const generateForecastData = (ticker, baseLastPrice, basePredictedPrice) => {
  const now = new Date();
  const minutesToShow = 60;
  
  return Array.from({ length: minutesToShow }, (_, i) => {
    const time = new Date(now.getTime() - (minutesToShow - 1 - i) * 60000);
    const variation = (Math.random() - 0.5) * 2;
    
    return {
      id: i,
      ticker,
      closePrice: basePredictedPrice + variation * 0.5, // Прогноз на следующую минуту
      lastPrice: baseLastPrice + variation,            // Реальная цена
      timing: time.toISOString()
    };
  });
};

// Преобразование данных для графика
const prepareChartData = (forecastData) => {
  const labels = forecastData.map(item => new Date(item.timing));
  
  // Реальные цены
  const actualPrices = forecastData.map(item => item.lastPrice);
  
  // Прогнозируемые цены (берем closePrice предыдущей точки)
  const predictedPrices = forecastData.map((_, i, arr) => 
    i > 0 ? arr[i-1].closePrice : null
  );

  // Добавляем последний прогноз (для следующей минуты)
  predictedPrices.push(forecastData[forecastData.length-1].closePrice);
  labels.push(new Date(new Date(forecastData[forecastData.length-1].timing).getTime() + 60000));

  return {
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
        data: predictedPrices,
        borderColor: '#ff00ff',
        backgroundColor: 'rgba(255, 0, 255, 0.1)',
        borderDash: [5, 5],
        tension: 0.1,
        pointRadius: 3,
      },
    ],
  };
};

const SubscribeTest = () => {
  const navigate = useNavigate();
  const [stocks, setStocks] = useState(mockStocks);
  const [subscribedStocks, setSubscribedStocks] = useState(mockSubscribedStocks);
  const [selectedStock, setSelectedStock] = useState(mockStocks[0].ticker);
  const [chartData, setChartData] = useState(null);
  const [notification, setNotification] = useState(null);

  
  // Имитация получения данных
  useEffect(() => {
    const updateData = () => {
      // Обновляем цены в списке акций
      const updatedStocks = stocks.map(stock => {
        const variation = (Math.random() - 0.5) * 2;
        return {
          ...stock,
          lastPrice: stock.lastPrice + variation,
          predictedPrice: stock.predictedPrice + variation * 0.5
        };
      });
      setStocks(updatedStocks);

      // Генерируем данные для графика выбранной акции
      const selected = updatedStocks.find(s => s.ticker === selectedStock);
      const forecastData = generateForecastData(
        selected.ticker, 
        selected.lastPrice, 
        selected.predictedPrice
      );
      setChartData(prepareChartData(forecastData));
    };

    // Первоначальная загрузка
    updateData();
    
    // Имитация обновления через WebSocket
    const interval = setInterval(updateData, 5000);
    return () => clearInterval(interval);
  }, [selectedStock]);

  // Остальной код без изменений
  const handleStockSelect = (ticker) => {
    setSelectedStock(ticker);
  };

  const handleSubscribe = async (ticker, isSubscribed) => {
    try {
      setNotification({
        message: isSubscribed ? 'Вы успешно отписались' : 'Вы успешно подписались',
        isError: false,
      });

      if (isSubscribed) {
        setSubscribedStocks(subscribedStocks.filter(t => t !== ticker));
      } else {
        setSubscribedStocks([...subscribedStocks, ticker]);
      }
    } catch (error) {
      setNotification({
        message: 'Ошибка при изменении подписки',
        isError: true,
      });
    }

    setTimeout(() => setNotification(null), 3000);
  };

  const formatPrice = (price) => {
    return price.toFixed(2);
  };

  const getPriceChangeClass = (lastPrice, predictedPrice) => {
    return predictedPrice >= lastPrice ? 'price-up' : 'price-down';
  };

  const handleLogout = () => {
    // Здесь можно добавить очистку данных авторизации при необходимости
    navigate('/'); // Переход на стартовую страницу
  };

  return (
    <div className="subscribe-app">
      <div className="subscribe-container">
        <div className="chart-section">
          <h2 className="chart-title">
            {selectedStock} - Прогноз цен (тестовый режим)
          </h2>
          <div className="chart-container">
            {chartData ? (
              <Line data={chartData} options={{
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                  x: {
                    type: 'time',
                    time: { unit: 'minute', displayFormats: { minute: 'HH:mm' } },
                    title: { display: true, text: 'Время', color: '#fff' },
                    ticks: { color: '#fff' },
                    grid: { color: 'rgba(255, 255, 255, 0.1)' },
                  },
                  y: {
                    title: { display: true, text: 'Цена ($)', color: '#fff' },
                    ticks: { color: '#fff' },
                    grid: { color: 'rgba(255, 255, 255, 0.1)' },
                  },
                },
                plugins: {
                  legend: {
                    position: 'top',
                    labels: { color: '#fff' },
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
                          return `${label} (${timeStr}): $${context.parsed.y.toFixed(2)}`;
                        }
                        return label;
                      }
                    }
                  },
                },
              }} />
            ) : (
              <div>Загрузка данных...</div>
            )}
          </div>
        </div>
         <div className="stocks-section-wrapper">
          <div className="stocks-section">
            <h2 className="stocks-title">
            Доступные акции
            </h2>
            <div div className="stocks-list">
            {stocks.map((stock) => {
              const isSubscribed = subscribedStocks.includes(stock.ticker);
              const isSelected = selectedStock === stock.ticker;
              const priceChangeClass = getPriceChangeClass(stock.lastPrice, stock.predictedPrice);
              
              return (
                <div 
                  key={stock.ticker}
                  className={`stock-item ${isSelected ? 'selected' : ''}`}
                  onClick={() => handleStockSelect(stock.ticker)}
                >
                  <div className="stock-info">
                    <div className="stock-ticker">
                      {stock.ticker} - {stock.name}
                    </div>
                    <div className="stock-prices">
                      <span>Текущая: ${formatPrice(stock.lastPrice)}</span>
                      <span className="price-separator"> | </span>
                      <span className={`predicted-price ${priceChangeClass}`}>
                        Прогноз: ${formatPrice(stock.predictedPrice)}
                      </span>
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

export default SubscribeTest;