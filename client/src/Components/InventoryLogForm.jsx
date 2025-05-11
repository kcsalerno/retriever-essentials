import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import './ItemForm.css';

function InventoryLogForm() {
  const { logId } = useParams();
  const navigate = useNavigate();
  const isEditMode = !!logId;

  const [formData, setFormData] = useState({
    itemId: '',
    quantityChange: '',
    reason: '',
    timeStamp: ''
  });

  const [currentUser, setCurrentUser] = useState(null);
  const [items, setItems] = useState([]);
  const [errors, setErrors] = useState([]);

  // Load logged-in user from email
  useEffect(() => {
    const storedEmail = localStorage.getItem("email");
    if (!storedEmail) {
      console.error("No stored email found.");
      return;
    }

    axios.get(`http://localhost:8080/api/user/email/${storedEmail}`)
      .then(res => setCurrentUser(res.data))
      .catch(err => console.error("Failed to fetch user by email:", err));
  }, []);

  // Load items
  useEffect(() => {
    axios.get('http://localhost:8080/api/item')
      .then(res => setItems(res.data.filter(item => item.enabled)))
      .catch(err => console.error("Failed to fetch items", err));
  }, []);

  // Load existing log data for edit mode
  useEffect(() => {
    if (isEditMode) {
      axios.get(`http://localhost:8080/api/inventory-log/${logId}`)
        .then(res => {
          const { itemId, quantityChange, reason, timeStamp } = res.data;
          setFormData({
            itemId,
            quantityChange,
            reason,
            timeStamp: timeStamp?.slice(0, 16) ?? ''
          });
        })
        .catch(err => {
          console.error("Error loading inventory log", err);
          navigate('/inventory-logs');
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
      authorityId: currentUser?.appUserId,
      quantityChange: parseInt(formData.quantityChange, 10)
    };

    try {
      if (isEditMode) {
        await axios.put(`http://localhost:8080/api/inventory-log/${logId}`, {
          logId: parseInt(logId, 10),
          ...payload
        });
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
          <input
            type="text"
            value={currentUser?.username ?? "Loading..."}
            disabled
          />
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
