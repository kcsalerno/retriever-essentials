import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './AdminTable.css';

function CheckoutItemsList({ checkoutItems }) {
  const navigate = useNavigate();

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this checkout item?")) return;
    try {
      await axios.delete(`http://localhost:8080/api/checkout-item/${id}`);
      alert('Checkout item deleted!');
      window.dispatchEvent(new Event('categoryUpdated'));
      navigate('/checkouts');
    } catch (err) {
      console.error("Error deleting checkout item:", err);
      alert('Failed to delete checkout item.');
    }
  };

  return (
    <div className="admin-table-container">
      <h3>Checkout Items</h3>
      <table className="admin-table">
        <thead>
          <tr>
            <th>Item Name</th>
            <th>Quantity</th>
            <th className="text-center">Actions</th>
          </tr>
        </thead>
        <tbody>
          {checkoutItems.map(item => (
            <tr key={item.checkoutItemId}>
              <td>{item.item?.itemName ?? '—'}</td>
              <td>{item.quantity}</td>
              <td style={{ display: 'flex', gap: '10px', justifyContent: 'center' }}>
                <Link className="btn add" to={`/edit-checkout-item/${item.checkoutItemId}`}>
                  📝 Edit
                </Link>
                <button
                  className="btn delete"
                  onClick={() => handleDelete(item.checkoutItemId)}
                >
                  🗑️ Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default CheckoutItemsList;
