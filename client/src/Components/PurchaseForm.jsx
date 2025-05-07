import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import PurchaseItemsList from './PurchaseItemsList';
import './ItemForm.css';

function PurchaseForm() {
  const { purchaseId } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    adminId: '',
    vendorId: '',
    purchaseDate: ''
  });

  const [purchaseItems, setPurchaseItems] = useState([]);
  const [admins, setAdmins] = useState([]);
  const [vendors, setVendors] = useState([]);
  const [errors, setErrors] = useState([]);

  useEffect(() => {
    axios.get(`http://localhost:8080/api/purchase/${purchaseId}`)
      .then(res => {
        const { adminId, vendorId, purchaseDate, purchaseItems } = res.data;
        setFormData({
          adminId,
          vendorId,
          purchaseDate: purchaseDate?.slice(0, 16) ?? ''
        });
        setPurchaseItems(purchaseItems ?? []);
      })
      .catch(err => {
        console.error("Failed to load purchase order", err);
        navigate('/dashboard');
      });

    axios.get('http://localhost:8080/api/user')
      .then(res => {
        const enabledAdmins = res.data.filter(u =>
          u.enabled && u.authorities.some(a => a.authority === 'ROLE_ADMIN')
        );
        setAdmins(enabledAdmins);
      });

    axios.get('http://localhost:8080/api/vendor')
      .then(res => setVendors(res.data.filter(v => v.enabled)));
  }, [purchaseId, navigate]);

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
      purchaseId: parseInt(purchaseId),
      purchaseItems: purchaseItems.map(item => ({
        purchaseItemId: item.purchaseItemId,
        itemId: item.item?.itemId ?? item.itemId,
        quantity: item.quantity
      }))
    };

    try {
      await axios.put(`http://localhost:8080/api/purchase/${purchaseId}`, payload);
      alert("Purchase order updated!");
      navigate('/purchases');
    } catch (err) {
      console.error("Failed to update purchase order", err);
      const messages = err.response?.data;
      setErrors(Array.isArray(messages) ? messages : ["Unexpected error."]);
    }
  };

  return (
    <div className="item-form-container">
      <h1>Edit Purchase Order #{purchaseId}</h1>

      {errors.length > 0 && (
        <div className="error-box">
          <ul>
            {errors.map((msg, idx) => <li key={idx}>{msg}</li>)}
          </ul>
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Admin</label>
          <select name="adminId" value={formData.adminId} onChange={handleChange} required>
            <option value="">Select Admin</option>
            {admins.map(admin => (
              <option key={admin.appUserId} value={admin.appUserId}>
                {admin.username}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Vendor</label>
          <select name="vendorId" value={formData.vendorId} onChange={handleChange} required>
            <option value="">Select Vendor</option>
            {vendors.map(v => (
              <option key={v.vendorId} value={v.vendorId}>
                {v.vendorName}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Purchase Date</label>
          <input
            type="datetime-local"
            name="purchaseDate"
            value={formData.purchaseDate}
            onChange={handleChange}
            required
          />
        </div>

        <button type="submit" className="add-btn">Update Purchase Order</button>
      </form>

      <PurchaseItemsList purchaseItems={purchaseItems} />
    </div>
  );
}

export default PurchaseForm;
