import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './AdminTable.css';

function PurchaseList() {
  const [purchases, setPurchases] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    axios.get('http://localhost:8080/api/purchase')
      .then(res => setPurchases(res.data))
      .catch(err => {
        console.error("Failed to fetch purchases", err);
        navigate('/dashboard');
      });
  }, [navigate]);

  const handleDelete = async (purchaseId) => {
    if (!window.confirm("Are you sure you want to delete this purchase order?")) return;
    try {
      await axios.delete(`http://localhost:8080/api/purchase/${purchaseId}`);
      alert('Purchsae order deleted!');
      window.dispatchEvent(new Event('categoryUpdated'));
      navigate('/dashboard');
    } catch (err) {
      console.error("Error deleting purchase order:", err);
      alert('Failed to delete purhcase order.');
    }
  };

  return (
    <div className="admin-table-container">
      <div className="button-head">
        <h2>All Purchase Orders</h2>
        <Link className="btn add" to="/add-purchase">
          ➕ Add Purchase
        </Link>
      </div>

      <table className="admin-table">
        <thead>
          <tr>
            <th>Purchase ID</th>
            <th>Vendor</th>
            <th>Admin</th>
            <th>Date</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {purchases.map(purchase => {
            const formattedDate = new Date(purchase.purchaseDate).toLocaleString('en-US', {
              year: '2-digit',
              month: 'numeric',
              day: 'numeric',
              hour: 'numeric',
              minute: '2-digit',
              hour12: true,
            });

            return (
              <tr key={purchase.purchaseId}>
                <td>{purchase.purchaseId}</td>
                <td>{purchase.vendor?.vendorName ?? '—'}</td>
                <td>{purchase.admin?.username ?? '—'}</td>
                <td>{formattedDate}</td>
                <td style={{ display: 'flex', gap: '10px', justifyContent: 'center', textAlign: 'center' }}>
                  <Link className="btn add" to={`/edit-purchase/${purchase.purchaseId}`}>📝Edit</Link>
                  <button className="btn delete"
                    onClick={() => handleDelete(purchase.purchaseId)}>
                    🗑️ Delete
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

export default PurchaseList;
