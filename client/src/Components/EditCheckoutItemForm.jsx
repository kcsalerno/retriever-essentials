import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import './ItemForm.css';

function EditCheckoutItemForm() {
  const { checkoutItemId } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    checkoutItemId: '',
    checkoutOrderId: '',
    itemId: '',
    quantity: ''
  });

  const [items, setItems] = useState([]);
  const [errors, setErrors] = useState([]);

  useEffect(() => {
    // Fetch item list
    axios.get('http://localhost:8080/api/item')
      .then(res => setItems(res.data.filter(i => i.enabled)))
      .catch(err => console.error("Error fetching items:", err));

    // Fetch current checkout item
    axios.get(`http://localhost:8080/api/checkout-item/${checkoutItemId}`)
      .then(res => setFormData(res.data))
      .catch(err => {
        console.error("Error loading checkout item:", err);
        navigate('/checkouts');
      });
  }, [checkoutItemId, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === "quantity" ? parseInt(value) : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors([]);

    try {
      await axios.put(`http://localhost:8080/api/checkout-item/${checkoutItemId}`, formData);
      alert('Checkout item updated!');
      window.dispatchEvent(new Event('categoryUpdated'));
      navigate('/checkouts');
    } catch (err) {
      console.error("Error updating checkout item:", err);
      const messages = err.response?.data;
      setErrors(Array.isArray(messages) ? messages : ["An unexpected error occurred."]);
    }
  };

//   const handleDelete = async () => {
//     if (!window.confirm("Are you sure you want to delete this checkout item?")) return;
//     try {
//       await axios.delete(`http://localhost:8080/api/checkout-item/${checkoutItemId}`);
//       alert('Checkout item deleted!');
//       navigate('/checkouts');
//     } catch (err) {
//       console.error("Error deleting checkout item:", err);
//       alert('Failed to delete checkout item.');
//     }
//   };

  return (
    <div className="item-form-container">
      <h1>Edit Checkout Item</h1>
      <form onSubmit={handleSubmit}>

        <div className="form-group">
          <label>Item</label>
          <select name="itemId" value={formData.itemId} onChange={handleChange} required>
            <option value="">-- Select an item --</option>
            {items.map(item => (
              <option key={item.itemId} value={item.itemId}>
                {item.itemName}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Quantity</label>
          <input
            type="number"
            name="quantity"
            min="1"
            value={formData.quantity}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-actions">
          <button type="submit" className="add-btn">Update</button>
          <Link to="/checkouts" className="btn-cancel">Cancel</Link>
          {/* <button type="button" onClick={handleDelete} className="btn btn-danger">Delete</button> */}
        </div>

        {errors.length > 0 && (
          <div className="error-box">
            <ul>
              {errors.map((msg, idx) => <li key={idx}>{msg}</li>)}
            </ul>
          </div>
        )}
      </form>
    </div>
  );
}

export default EditCheckoutItemForm;
