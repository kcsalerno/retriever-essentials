// src/Components/Unauthorized.jsx
import React from 'react';
import { useAuth  } from "../Contexts/AuthContext";
import { Link } from 'react-router-dom';
import './Unauthorized.css';



function Unauthorized() {
  const { selfCheckoutEnabled } = useAuth();

  return (
    <div className="unauthorized-container">
      <div className="unauthorized-box">
        <h1>🚫 Unauthorized Access</h1>
        <p>You do not have permission to view this page.</p>
        {selfCheckoutEnabled ? (
          <Link to="/popular" className="unauthorized-link">Return</Link>)
          : ( <Link to="/" className="unauthorized-link">Return to Login</Link>
          )}
      </div>
    </div>
  );
}

export default Unauthorized;
