import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './AdminTable.css';

function PurchaseItemsList({ purchaseItems }) {
  const navigate = useNavigate();

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this purchase item?")) return;
    try {
      await axios.delete(`http://localhost:8080/api/purchase-item/${id}`);
      alert('Purchase item deleted!');
      window.dispatchEvent(new Event('categoryUpdated'));
      navigate('/purchases');
    } catch (err) {
      console.error("Error deleting purchase item:", err);
      alert('Failed to delete purchase item.');
    }
  };

  return (
    <div className="admin-table-container">
      <h3>Purchase Items</h3>
      <table className="admin-table">
        <thead>
          <tr>
            <th>Item Name</th>
            <th>Quantity</th>
            <th className="text-center">Actions</th>
          </tr>
        </thead>
        <tbody>
          {purchaseItems.map(item => (
            <tr key={item.purchaseItemId}>
              <td>{item.item?.itemName ?? '—'}</td>
              <td>{item.quantity}</td>
              <td style={{ display: 'flex', gap: '10px', justifyContent: 'center' }}>
                <Link className="btn add" to={`/edit-purchase-item/${item.purchaseItemId}`}>
                  📝 Edit
                </Link>
                <button
                  className="btn delete"
                  onClick={() => handleDelete(item.purchaseItemId)}>
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

export default PurchaseItemsList;
