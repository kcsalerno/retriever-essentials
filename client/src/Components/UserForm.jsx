import React, { useEffect, useState } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import axios from 'axios';
import './ItemForm.css';

function UserForm() {
  const { userId } = useParams();
  const navigate = useNavigate();
  const isEditMode = !!userId;

  const [formData, setFormData] = useState({
    username: '',
    password: '',
    userRole: 'ADMIN',
    enabled: true,
  });

  const [errors, setErrors] = useState([]);

  useEffect(() => {
    if (isEditMode) {
      axios.get(`http://localhost:8080/api/user/${userId}`)
        .then(res => {
          const { username, userRole, enabled } = res.data;
          setFormData({
            username,
            password: '',
            userRole: userRole || 'ADMIN',
            enabled
          });
        })
        .catch(err => {
          console.error("Error fetching user", err);
          navigate('/users');
        });
    }
  }, [isEditMode, userId, navigate]);

  const handleChange = e => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async e => {
    e.preventDefault();
    setErrors([]);

    try {
      if (!isEditMode) {
        const payload = {
          username: formData.username,
          password: formData.password,
          userRole: formData.userRole
        };

        await axios.post('http://localhost:8080/api/user', payload);
        alert('User added!');
        navigate('/users');
      }
    } catch (err) {
      console.error("Error saving user:", err);
      const messages = err.response?.data;
      if (Array.isArray(messages)) {
        setErrors(messages);
      } else {
        setErrors(["An unexpected error occurred."]);
      }
    }
  };

  const handleToggleEnabled = async () => {
    try {
      const endpoint = formData.enabled ? 'disable' : 'enable';
      await axios.put(`http://localhost:8080/api/user/${endpoint}/${userId}`);
      setFormData(prev => ({ ...prev, enabled: !prev.enabled }));
      navigate('/users');
    } catch (err) {
      console.error("Error toggling user status", err);
      alert("Failed to update user status.");
    }
  };

  return (
    <div className="item-form-container">
      <h1>{isEditMode ? 'Edit User' : 'Add New User'}</h1>
      <form onSubmit={handleSubmit}>

        <div className="form-group">
          <label>Username</label>
          <input
            type="email"
            name="username"
            value={formData.username}
            onChange={handleChange}
            disabled={isEditMode}
            required
          />
        </div>

        {!isEditMode && (
          <>
            <div className="form-group">
              <label>Password</label>
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label>User Role</label>
              <select
                name="userRole"
                value={formData.userRole}
                onChange={handleChange}
                required
              >
                <option value="ADMIN">Admin</option>
                <option value="AUTHORITY">Authority</option>
              </select>
            </div>
          </>
        )}

        {isEditMode && (
            <div className="form-group">
              <button
                type="button"
                onClick={handleToggleEnabled}
                className={formData.enabled ? 'btn disable-btn' : 'btn enable-btn'}
              >
                {formData.enabled ? 'Disable User 🔒' : 'Enable User ✅'}
              </button>
            </div>
        )}

        {!isEditMode && (
          <button type="submit" className="add-btn">
            Add User
          </button>
        )}

        <div className="form-group">
            <Link to="/users" className="btn cancel">Cancel</Link>
        </div>

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

export default UserForm;
