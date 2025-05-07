import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './AdminTable.css';
import { useAuth } from '../Contexts/AuthContext';

function ItemList() {
  const [items, setItems] = useState([]);
  const navigate = useNavigate();
  const { user } = useAuth();
  const isAdmin = user?.role === 'ROLE_ADMIN';

  useEffect(() => {
    axios.get('http://localhost:8080/api/item')
      .then(res => setItems(res.data))
      .catch(err => {
        console.error("Failed to fetch items", err);
        navigate('/dashboard'); // Redirect to dashboard on error
      });
  }, [navigate]);

  return (
    <div className="admin-table-container">
      <div className="button-head">
        <h2>All Items</h2>
        {isAdmin && (
          <Link className="btn add" to="/add-item">
            ➕ Add Item
          </Link>
        )}
        
      </div>

      <table className="admin-table">
        <thead>
            <tr>
            <th>Item Name</th>
            <th>Category</th>
            <th>Limit</th>
            <th>Price</th>
            <th>Enabled</th>
            <th></th>
            </tr>
        </thead>
        <tbody>
            {items.map(item => (
            <tr key={item.itemId}>
                <td>{item.itemName}</td>
                <td>{item.category}</td>
                <td>{item.itemLimit}</td>
                <td>${item.pricePerUnit.toFixed(2)}</td>
                <td style={{ textAlign: 'center' }}>{item.enabled ? '✅' : '❌'}</td>
                <td style={{ display: 'flex', gap: '10px', justifyContent: 'center' }}>
                <Link className="btn add" to={`/edit-product/${item.itemName}`}>📝Edit</Link>
                {/* <Link className="btn delete" to={`/delete-item/${item.itemId}`}>Delete</Link> */}
                </td>
            </tr>
            ))}
        </tbody>
        </table>

    </div>
  );
}

export default ItemList;
