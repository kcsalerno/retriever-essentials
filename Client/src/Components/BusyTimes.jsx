import React, { useContext, useEffect, useState } from 'react';
import { Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Tooltip, Legend } from 'chart.js';
import { UserContext } from './UserContext';

ChartJS.register(CategoryScale, LinearScale, BarElement, Tooltip, Legend);

const hours = Array.from({ length: 13 }, (_, i) => `${i + 8}:00`);
const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

function BusyTimes() {
  const { isAdmin } = useContext(UserContext);
  const [data, setData] = useState({});
  const [selectedDay, setSelectedDay] = useState('Saturday');

  useEffect(() => {
    fetch('http://localhost:8080/api/checkout-order/busiest-hours')
      .then(res => res.json())
      .then(busyData => {
        const formatted = days.reduce((acc, day) => {
          acc[day] = Array(13).fill(0);
          return acc;
        }, {});
        busyData.forEach(({ day, hour, count }) => {
          const hourIndex = hour - 8;
          if (formatted[day] && hourIndex >= 0 && hourIndex < 13) {
            formatted[day][hourIndex] = count;
          }
        });
        setData(formatted);
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

  if (!data[selectedDay]) return <div style={{ color: 'white' }}>Loading busy times...</div>;

  const chartData = {
    labels: hours,
    datasets: [
      {
        label: `Busyness on ${selectedDay}`,
        data: data[selectedDay],
        backgroundColor: 'rgba(75,192,192,0.6)',
        borderColor: 'rgba(75,192,192,1)',
        borderWidth: 1
      }
    ]
  };

  const chartOptions = {
    scales: {
      y: {
        beginAtZero: true,
        max: 5,
        ticks: { stepSize: 1 }
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
          {days.map(day => <option key={day} value={day}>{day}</option>)}
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




