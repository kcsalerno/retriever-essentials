import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Scan.css';

function ScanID() {
  const [id, setId] = useState('');
  const navigate = useNavigate();

  const handleInput = (value) => {
    if (id.length < 7) {
      setId(id + value.toUpperCase());
    }
  };

  const handleBackspace = () => {
    setId(id.slice(0, -1));
  };

  const handleSubmit = () => {
    if (/^[A-Z]{2}\d{5}$/.test(id)) {
    console.log("Valid ID entered:", id);
    navigate('/about-us');
  } else {
    console.log("Invalid ID format:", id);
  };
  }

  return (
    <div className="scan-container">
      <div className="scan-box">
        <h1>Enter ID</h1>
        <input className="id-input" type="text" value={id} readOnly />

        <div className="keypad">
          <div className="key-row">
            {'QWERTYUIOP'.split('').map(letter => (
              <button key={letter} onClick={() => handleInput(letter)}>{letter}</button>
            ))}
          </div>
          <div className="key-row">
            {'ASDFGHJKL'.split('').map(letter => (
              <button key={letter} onClick={() => handleInput(letter)}>{letter}</button>
            ))}
          </div>
          <div className="key-row">
            {'ZXCVBNM'.split('').map(letter => (
              <button key={letter} onClick={() => handleInput(letter)}>{letter}</button>
            ))}
          </div>
          <div className="key-row">
            {'1234567890'.split('').map(number => (
              <button key={number} onClick={() => handleInput(number)}>{number}</button>
            ))}
          </div>
          <div className="key-row">
            <button onClick={handleBackspace}>←</button>
            <button onClick={handleSubmit} className="submit-button">Submit</button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ScanID;
