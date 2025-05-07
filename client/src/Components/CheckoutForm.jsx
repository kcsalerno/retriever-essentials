import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import CheckoutItemsList from './CheckoutItemsList';
import './ItemForm.css';

function CheckoutForm() {
  const { checkoutId } = useParams(); // changed to match your route
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    studentId: '',
    authorityId: '',
    selfCheckout: false,
    checkoutDate: ''
  });

  const [checkoutItems, setCheckoutItems] = useState([]);
  const [users, setUsers] = useState([]); // all enabled users

  useEffect(() => {
    // Load checkout order details
    axios.get(`http://localhost:8080/api/checkout-order/${checkoutId}`)
      .then(res => {
        const { checkoutOrderId, studentId, authorityId, selfCheckout, checkoutDate, checkoutItems } = res.data;
        setFormData({
          checkoutOrderId,
          studentId,
          authorityId: authorityId ?? '',
          selfCheckout,
          checkoutDate: checkoutDate?.slice(0, 16) ?? ''
        });
        setCheckoutItems(checkoutItems ?? []);
      })
      .catch(err => {
        console.error("Error loading checkout order", err);
        navigate('/dashboard');
      });

    // Load enabled users (admins and authorities)
    axios.get('http://localhost:8080/api/user')
      .then(res => {
        const enabledUsers = res.data.filter(user => user.enabled);
        setUsers(enabledUsers);
      })
      .catch(err => console.error("Failed to load users", err));
  }, [checkoutId, navigate]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const [errors, setErrors] = useState([]);

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    const payload = {
        ...formData,
        checkoutItems: checkoutItems.map(item => ({
          checkoutItemId: item.checkoutItemId, // 👈 this is the key fix
          itemId: item.item?.itemId ?? item.itemId,
          quantity: item.quantity
        }))
      };      
  
    try {
    console.log("Submitting payload:", JSON.stringify(payload, null, 2));
      await axios.put(`http://localhost:8080/api/checkout-order/${checkoutId}`, payload);
      alert('Checkout order updated!');
      navigate('/checkouts');
    } catch (err) {
      console.error("Failed to update checkout order", err);
      const messages = err.response?.data;
  
      if (Array.isArray(messages)) {
        setErrors(messages);
      } else if (typeof messages === 'string') {
        setErrors([messages]);
      } else {
        setErrors(["An unexpected error occurred while saving."]);
      }
    }
  };
  

  return (
    <div className="item-form-container">
        {errors.length > 0 && (
            <div className="error-box">
                <ul>
                {errors.map((msg, idx) => (
                    <li key={idx}>{msg}</li>
                ))}
                </ul>
            </div>
        )}
      <h1>Edit Checkout Order #{checkoutId}</h1>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Student ID</label>
          <input
            type="text"
            name="studentId"
            value={formData.studentId}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label>Authority</label>
          <select
            name="authorityId"
            value={formData.authorityId}
            onChange={handleChange}
            required
          >
            <option value="">-- Select User --</option>
            {users.map(user => (
              <option key={user.appUserId} value={user.appUserId}>
                {user.username}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group checkbox-group">
          <label htmlFor="selfCheckout">Self Checkout</label>
          <input
            type="checkbox"
            id="selfCheckout"
            name="selfCheckout"
            checked={formData.selfCheckout}
            onChange={handleChange}
          />
        </div>

        <div className="form-group">
          <label>Checkout Date</label>
          <input
            type="datetime-local"
            name="checkoutDate"
            value={formData.checkoutDate}
            onChange={handleChange}
            required
          />
        </div>

        <button type="submit" className="add-btn">Update Checkout Order</button>
      </form>

      <CheckoutItemsList checkoutItems={checkoutItems} />
    </div>
  );
}

export default CheckoutForm;
