import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './ItemForm.css';
import { Link } from 'react-router-dom';

function InventoryLogForm() {
  const { logId } = useParams();
  const navigate = useNavigate();
  const isEditMode = !!logId;

  const [formData, setFormData] = useState({
    authorityId: '',
    itemId: '',
    quantityChange: '',
    reason: '',
    timeStamp: ''
  });

  const [users, setUsers] = useState([]);
  const [items, setItems] = useState([]);
  const [errors, setErrors] = useState([]);

  // Load dropdown data
  useEffect(() => {
    axios.get('http://localhost:8080/api/user')
      .then(res => setUsers(res.data.filter(user => user.enabled)))
      .catch(err => console.error("Failed to fetch users", err));

    axios.get('http://localhost:8080/api/item')
      .then(res => setItems(res.data.filter(item => item.enabled)))
      .catch(err => console.error("Failed to fetch items", err));
  }, []);

  // Load log if editing
  useEffect(() => {
    if (isEditMode) {
      axios.get(`http://localhost:8080/api/inventory-log/${logId}`)
        .then(res => {
          const { authorityId, itemId, quantityChange, reason, timeStamp } = res.data;
          setFormData({
            authorityId,
            itemId,
            quantityChange,
            reason,
            timeStamp: timeStamp?.slice(0, 16) ?? ''
          });
        })
        .catch(err => {
          console.error("Error loading inventory log", err);
          navigate('/logs');
        });
    }
  }, [isEditMode, logId, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      ...formData,
      quantityChange: parseInt(formData.quantityChange, 10)
    };

    try {
      if (isEditMode) {
        await axios.put(`http://localhost:8080/api/inventory-log/${logId}`, { logId: parseInt(logId), ...payload });
        alert("Log updated!");
      } else {
        await axios.post(`http://localhost:8080/api/inventory-log`, payload);
        alert("Log added!");
      }
      navigate('/inventory-logs');
    } catch (err) {
      console.error("Error saving log:", err);
      const messages = err.response?.data;
      if (Array.isArray(messages)) {
        setErrors(messages);
      } else {
        setErrors(["An unexpected error occurred while saving."]);
      }
    }
  };

  return (
    <div className="item-form-container">
      <h1>{isEditMode ? `Edit Log #${logId}` : 'Add Inventory Log'}</h1>
      {errors.length > 0 && (
        <div className="error-box">
          <ul>
            {errors.map((e, idx) => <li key={idx}>{e}</li>)}
          </ul>
        </div>
      )}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Admin/Authority Username</label>
          <select name="authorityId" value={formData.authorityId} onChange={handleChange} required>
            <option value="">Select a user</option>
            {users.map(user => (
              <option key={user.appUserId} value={user.appUserId}>
                {user.username}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Item</label>
          <select name="itemId" value={formData.itemId} onChange={handleChange} required>
            <option value="">Select an item</option>
            {items.map(item => (
              <option key={item.itemId} value={item.itemId}>
                {item.itemName}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Quantity Change</label>
          <input
            type="number"
            name="quantityChange"
            value={formData.quantityChange}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label>Reason</label>
          <input
            type="text"
            name="reason"
            value={formData.reason}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label>Timestamp</label>
          <input
            type="datetime-local"
            name="timeStamp"
            value={formData.timeStamp}
            onChange={handleChange}
            required
          />
        </div>

        <button type="submit" className="add-btn">
          {isEditMode ? 'Update Log' : 'Add Log'}
        </button>
        <Link to="/dashboard" className="btn-cancel">Cancel</Link>
      </form>
    </div>
  );
}

export default InventoryLogForm;
