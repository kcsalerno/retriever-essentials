import React, { useState } from 'react';
import { useNavigate, Navigate } from 'react-router-dom';
import { useAuth } from '../Contexts/AuthContext';
import './Login.css';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const { login } = useAuth();

  const { user } = useAuth();

  if (user) {
    const isAdmin = user.role === 'ROLE_ADMIN' || user.role === 'ROLE_AUTHORITY';
    return <Navigate to={isAdmin ? "/dashboard" : "/about-us"} />;
  }
  

  const handleSubmit = async () => {
    setError('');
    const result = await login({ email, password });

    if (result.ok) {
      console.log("✅ Login success:", result);
      // Save token and basic user info to localStorage
    localStorage.setItem("token", result.token);
    localStorage.setItem("email", result.email);
    localStorage.setItem("role", result.role);
      navigate('/dashboard');
    } else {
      console.error("❌ Login failed:", result.error);
      setError('Login failed. Please check your email and password.');
    }
  };

  return (
    <div className="scan-container">
      <div className="scan-box">
        <h1>Login</h1>
        {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}

        <input
          className="id-input"
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <input
          className="id-input"
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <div className="submit-button">
          <button onClick={handleSubmit}>Login</button>
        </div>
      </div>
    </div>
  );
}

export default Login;
