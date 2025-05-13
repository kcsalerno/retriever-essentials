import React, { useState, useEffect } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import axios from 'axios';
import './ItemForm.css';

function ChangePasswordForm() {
  const { userId } = useParams();
  const navigate = useNavigate();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errors, setErrors] = useState([]);
  
  useEffect(() => {
    axios.get(`http://localhost:8080/api/user/${userId}`)
      .then(res => {
        setUsername(res.data.username);
      })
      .catch(err => {
        console.error("Error fetching user info", err);
        setUsername('Unknown User');
      });
  }, [userId]);

  const handleSubmit = async e => {
    e.preventDefault();
    setErrors([]);

    if (password !== confirmPassword) {
      setErrors(['Passwords do not match.']);
      return;
    }

    try {
      await axios.put('http://localhost:8080/api/user/password', { password },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        }
      );
      alert('Password updated!');
      navigate('/dashboard');
    } catch (err) {
      console.error("Error changing password:", err);
      const messages = err.response?.data;
      if (Array.isArray(messages)) {
        setErrors(messages);
      } else {
        setErrors(["An unexpected error occurred."]);
      }
    }
  };

  return (
    <div className="item-form-container">
      <h1>Change Password</h1>
      <p className="subtext">User: <strong>{username}</strong></p>

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>New Password</label>
          <input
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label>Confirm Password</label>
          <input
            type="password"
            value={confirmPassword}
            onChange={e => setConfirmPassword(e.target.value)}
            required
          />
        </div>

        <button type="submit" className="add-btn">
          Save Password
        </button>
        <Link to="/dashboard" className="btn cancel-btn ms-2">
          Cancel
        </Link>

        {errors.length > 0 && (
          <div className="error-box">
            <ul>
              {errors.map((msg, idx) => (
                <li key={idx}>{msg}</li>
              ))}
            </ul>
          </div>
        )}
      </form>
    </div>
  );
}

export default ChangePasswordForm;
