// src/Components/NotFound.jsx
import React from 'react';
import { Link } from 'react-router-dom';
import './NotFound.css';

function NotFound() {
  return (
    <div className="not-found-container">
      <h1>🚫 404 - Page Not Found</h1>
      <p>The page you're looking for doesn't exist.</p>
      <Link to="/" className="back-home-button">Return to Login</Link>
    </div>
  );
}

export default NotFound;