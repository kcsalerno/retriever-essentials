import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import './ItemForm.css';

function EditPurchaseItemForm() {
  const { purchaseItemId } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    purchaseItemId: '',
    purchaseOrderId: '',
    itemId: '',
    quantity: ''
  });

  const [items, setItems] = useState([]);
  const [errors, setErrors] = useState([]);

  // Fetch item list and the specific purchase item on mount
  useEffect(() => {
    axios.get(`http://localhost:8080/api/item`)
      .then(res => setItems(res.data))
      .catch(err => console.error("Error fetching items:", err));

    axios.get(`http://localhost:8080/api/purchase-item/${purchaseItemId}`)
      .then(res => {
        setFormData({
          purchaseItemId: res.data.purchaseItemId,
          purchaseOrderId: res.data.purchaseOrderId,
          itemId: res.data.itemId,
          quantity: res.data.quantity
        });
      })
      .catch(err => {
        console.error("Error loading purchase item:", err);
        alert("Failed to load purchase item.");
        navigate('/purchases');
      });
  }, [purchaseItemId, navigate]);

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
      await axios.put(`http://localhost:8080/api/purchase-item/${purchaseItemId}`, formData);
      alert("Purchase item updated!");
      window.dispatchEvent(new Event('categoryUpdated'));
      navigate('/purchases');
    } catch (err) {
      console.error("Error updating purchase item:", err);
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
      <h1>Edit Purchase Item</h1>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="itemId">Item</label>
          <select
            name="itemId"
            value={formData.itemId}
            onChange={handleChange}
            required
          >
            <option value="">-- Select an Item --</option>
            {items.map(item => (
              <option key={item.itemId} value={item.itemId}>
                {item.itemName}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="quantity">Quantity</label>
          <input
            type="number"
            name="quantity"
            value={formData.quantity}
            onChange={handleChange}
            required
            min={1}
          />
        </div>

        <button type="submit" className="add-btn">Save Changes</button>

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

export default EditPurchaseItemForm;
