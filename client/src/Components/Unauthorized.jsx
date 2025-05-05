// src/Components/Unauthorized.jsx
import React from 'react';
import { Link } from 'react-router-dom';
import './Unauthorized.css';

function Unauthorized() {
  return (
    <div className="unauthorized-container">
      <div className="unauthorized-box">
        <h1>🚫 Unauthorized Access</h1>
        <p>You do not have permission to view this page.</p>
        <Link to="/" className="unauthorized-link">Return to Login</Link>
      </div>
    </div>
  );
}

export default Unauthorized;
