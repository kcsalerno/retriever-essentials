import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './ItemForm.css';
import './AdminTable.css';
import { useNavigate } from 'react-router-dom';

function AddPurchaseForm() {
  const navigate = useNavigate();

  const [vendors, setVendors] = useState([]);
  const [admins, setAdmins] = useState([]);
  const [items, setItems] = useState([]);

  const [selectedVendorId, setSelectedVendorId] = useState('');
  const [selectedAdminId, setSelectedAdminId] = useState('');
  const [purchaseDate, setPurchaseDate] = useState('');
  const [selectedItems, setSelectedItems] = useState({});
  const [errors, setErrors] = useState([]);

  // Load vendors and admins
  useEffect(() => {
    axios.get('http://localhost:8080/api/vendor')
      .then(res => setVendors(res.data.filter(v => v.enabled)))
      .catch(err => console.error('Failed to load vendors', err));

    axios.get('http://localhost:8080/api/user')
      .then(res => setAdmins(res.data.filter(u =>
        u.enabled && u.authorities.some(a => a.authority === 'ROLE_ADMIN')
      )))
      .catch(err => console.error('Failed to load users', err));
  }, []);

  // Load items from selected vendor
  useEffect(() => {
    if (selectedVendorId) {
      axios.get('http://localhost:8080/api/item')
        .then(res => {
          const vendorItems = res.data.filter(item => item.enabled);
          setItems(vendorItems);
        })
        .catch(err => console.error('Failed to load items', err));
    } else {
      setItems([]);
    }
    setSelectedItems({});
  }, [selectedVendorId]);

  const handleItemSelection = (itemId, checked) => {
    setSelectedItems(prev => {
      const updated = { ...prev };
      if (checked) {
        updated[itemId] = 1;
      } else {
        delete updated[itemId];
      }
      return updated;
    });
  };

  const handleQuantityChange = (itemId, value) => {
    setSelectedItems(prev => ({
      ...prev,
      [itemId]: parseInt(value) || 0
    }));
  };

  const calculateTotalCost = () => {
    return Object.entries(selectedItems).reduce((sum, [itemId, quantity]) => {
      const item = items.find(i => i.itemId === parseInt(itemId));
      return sum + (item ? quantity * item.pricePerUnit : 0);
    }, 0).toFixed(2);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      adminId: parseInt(selectedAdminId),
      vendorId: parseInt(selectedVendorId),
      purchaseDate,
      purchaseItems: Object.entries(selectedItems).map(([itemId, quantity]) => ({
        itemId: parseInt(itemId),
        quantity
      })),
      totalCost: parseFloat(calculateTotalCost()) // optionally remove if unsupported
    };

    try {
      await axios.post('http://localhost:8080/api/purchase', payload);
      alert('Purchase order created!');
      window.dispatchEvent(new Event('categoryUpdated'));
      navigate('/purchases');
    } catch (err) {
      console.error('Failed to create purchase order', err);
      const messages = err.response?.data;
      if (Array.isArray(messages)) {
        setErrors(messages);
      } else {
        setErrors(['An unexpected error occurred while creating the purchase order.']);
      }
    }
  };

  return (
    <div className="item-form-container">
      <h1>Add Purchase Order</h1>

      {errors.length > 0 && (
        <div className="error-box">
          <ul>{errors.map((e, i) => <li key={i}>{e}</li>)}</ul>
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Admin</label>
          <select required value={selectedAdminId} onChange={(e) => setSelectedAdminId(e.target.value)}>
            <option value="">-- Select Admin --</option>
            {admins.map(admin => (
              <option key={admin.appUserId} value={admin.appUserId}>{admin.username}</option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Vendor</label>
          <select required value={selectedVendorId} onChange={(e) => setSelectedVendorId(e.target.value)}>
            <option value="">-- Select Vendor --</option>
            {vendors.map(v => (
              <option key={v.vendorId} value={v.vendorId}>{v.vendorName}</option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Purchase Date</label>
          <input type="datetime-local" required value={purchaseDate} onChange={e => setPurchaseDate(e.target.value)} />
        </div>

        {items.length > 0 && (
          <>
            <h3>Select Items</h3>
            <table className="admin-table">
              <thead>
                <tr>
                  <th>Select</th>
                  <th>Item Name</th>
                  <th>Price</th>
                  <th>Quantity</th>
                  <th>Line Total</th>
                </tr>
              </thead>
              <tbody>
                {items.map(item => {
                  const isSelected = selectedItems.hasOwnProperty(item.itemId);
                  const quantity = selectedItems[item.itemId] || 0;
                  const lineTotal = (quantity * item.pricePerUnit).toFixed(2);
                  return (
                    <tr key={item.itemId}>
                      <td><input type="checkbox" checked={isSelected} onChange={e => handleItemSelection(item.itemId, e.target.checked)} /></td>
                      <td>{item.itemName}</td>
                      <td>${item.pricePerUnit.toFixed(2)}</td>
                      <td>
                        {isSelected && (
                          <input
                            type="number"
                            min="1"
                            value={quantity}
                            onChange={e => handleQuantityChange(item.itemId, e.target.value)}
                            style={{ width: '60px' }}
                          />
                        )}
                      </td>
                      <td>{isSelected ? `$${lineTotal}` : '—'}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>

            <h3>Total: ${calculateTotalCost()}</h3>
          </>
        )}

        <button type="submit" className="add-btn">Create Purchase</button>
      </form>
    </div>
  );
}

export default AddPurchaseForm;
