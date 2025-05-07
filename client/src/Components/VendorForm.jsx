import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import './ItemForm.css';

function VendorForm({ isEditMode }) {
  const { vendorId } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    vendorName: '',
    phoneNumber: '',
    contactEmail: '',
    enabled: true
  });

  const [errors, setErrors] = useState([]);

  useEffect(() => {
    if (isEditMode) {
      axios.get(`http://localhost:8080/api/vendor/vendor-id/${vendorId}`)
        .then(res => {
          setFormData(res.data);
        })
        .catch(() => {
          alert("Failed to load vendor data.");
          navigate('/vendors');
        });
    }
  }, [isEditMode, vendorId, navigate]);

  const handleChange = (e) => {
    const { name, type, value, checked } = e.target;
    const val = type === 'checkbox' ? checked : value;
    setFormData(prev => ({ ...prev, [name]: val }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      if (isEditMode) {
        await axios.put(`http://localhost:8080/api/vendor/${vendorId}`, formData);
        alert("Vendor updated.");
      } else {
        await axios.post('http://localhost:8080/api/vendor', formData);
        alert("Vendor added.");
      }
      navigate('/vendors');
    } catch (err) {
      console.error(err);
      const messages = err.response?.data;
      setErrors(Array.isArray(messages) ? messages : ["An unexpected error occurred."]);
    }
  };

  return (
    <div className="product-detail-container">
      <div className="product-detail-content">
        <h1>{isEditMode ? `Edit Vendor: ${formData.vendorName}` : 'Add Vendor'}</h1>

        {errors.length > 0 && (
          <div className="error-box">
            <ul>{errors.map((msg, idx) => <li key={idx}>{msg}</li>)}</ul>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Vendor Name</label>
            <input type="text" name="vendorName" value={formData.vendorName} onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label>Phone Number</label>
            <input type="text" name="phoneNumber" value={formData.phoneNumber} onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label>Email</label>
            <input type="text" name="contactEmail" value={formData.contactEmail} onChange={handleChange} required />
          </div>

          {isEditMode && (
            <div className="form-group">
              <label htmlFor="enabled">Enabled</label>
              <input
                type="checkbox"
                name="enabled"
                checked={formData.enabled}
                onChange={handleChange}
                style={{ marginLeft: '10px' }}
              />
            </div>
          )}

          <button type="submit" className="add-btn">
            {isEditMode ? 'Save Changes' : 'Add Vendor'}
          </button>
        </form>
      </div>
    </div>
  );
}

export default VendorForm;
