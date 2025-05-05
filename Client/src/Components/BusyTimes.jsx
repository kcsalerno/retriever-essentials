// src/Components/BusyTimes.jsx
import React, { useEffect, useState } from 'react';
import { Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Tooltip, Legend } from 'chart.js';
import { useAuth } from '../Contexts/AuthContext';

ChartJS.register(CategoryScale, LinearScale, BarElement, Tooltip, Legend);

const hours = Array.from({ length: 13 }, (_, i) => {
  const hour = i + 8;
  const period = hour >= 12 ? 'PM' : 'AM';
  const hour12 = hour % 12 === 0 ? 12 : hour % 12;
  return `${hour12}:00 ${period}`;
});

const daysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

function BusyTimes() {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ROLE_ADMIN';
  const [data, setData] = useState({});
  const [selectedDay, setSelectedDay] = useState('Monday');

  useEffect(() => {
    fetch('http://localhost:8080/api/checkout-order/hourly-checkout-summary')
      .then(res => res.json())
      .then(busyData => {
        const formatted = {};

        // Initialize each day with 0s
        daysOfWeek.forEach(day => {
          formatted[day] = Array(13).fill(0);
        });

        busyData.forEach(({ day, hour, total_checkouts }) => {
          const hourIndex = hour - 8;
          if (formatted[day] && hourIndex >= 0 && hourIndex < 13) {
            formatted[day][hourIndex] = total_checkouts;
          }
        });

        setData(formatted);

        // Auto-select first populated day
        const firstPopulatedDay = daysOfWeek.find(day => formatted[day].some(count => count > 0));
        if (firstPopulatedDay) {
          setSelectedDay(firstPopulatedDay);
        }

      })
      .catch(err => console.error("Failed to fetch busy data", err));
  }, []);

  const updateValue = (index, value) => {
    if (!isAdmin) return;
    setData(prev => ({
      ...prev,
      [selectedDay]: prev[selectedDay].map((v, i) => (i === index ? parseInt(value) : v))
    }));
  };

  if (!data[selectedDay]) {
    return <div style={{ color: 'white' }}>Loading busy times...</div>;
  }

  const chartData = {
    labels: hours,
    datasets: [
      {
        label: `Busyness on ${selectedDay}`,
        data: data[selectedDay],
        backgroundColor: '#FFD700', // vibrant gold
        borderColor: '#FFD700',
        hoverBackgroundColor: '#e6c200'
      }
    ]
  };  

  const maxY = Math.max(...data[selectedDay]) + 1;

  const chartOptions = {
    scales: {
      x: {
        ticks: {
          color: '#ffffff'
        },
        grid: {
          color: '#555'
        }
      },
      y: {
        beginAtZero: true,
        max: maxY,
        ticks: {
          color: '#ffffff',
          stepSize: 1
        },
        grid: {
          color: '#555'
        }
      }
    },
    plugins: {
      legend: {
        labels: {
          color: '#ffffff'
        }
      }
    },
    responsive: true,
    maintainAspectRatio: false
  };
 
  return (
    <div style={{ padding: '20px', color: 'white' }}>
      <h2>{isAdmin ? 'Edit Busy Times (Not Saved)' : 'View Busy Times'}</h2>

      <label>
        Select Day:{" "}
        <select value={selectedDay} onChange={(e) => setSelectedDay(e.target.value)}>
          {daysOfWeek.map(day => (
            <option key={day} value={day}>{day}</option>
          ))}
        </select>
      </label>

      <div style={{ maxWidth: '1000px', height: '400px', marginTop: '20px' }}>
        <Bar data={chartData} options={chartOptions} />
      </div>

      {isAdmin && (
        <div style={{ marginTop: '30px' }}>
          <h3>Adjust Busyness (Does Not Save)</h3>
          {hours.map((hour, index) => (
            <div key={index} style={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
              <label style={{ width: '100px' }}>{hour}</label>
              <input
                type="range"
                min="0"
                max="5"
                value={data[selectedDay][index]}
                onChange={(e) => updateValue(index, e.target.value)}
                style={{ width: '200px', margin: '0 10px' }}
              />
              <span>{data[selectedDay][index]}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default BusyTimes;
