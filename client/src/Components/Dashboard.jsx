import React, { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../Contexts/AuthContext';
import './Dashboard.css';

function Dashboard() {
  const { user, enableSelfCheckout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) return;
    if (user.role !== 'ROLE_ADMIN' && user.role !== 'ROLE_AUTHORITY') {
      navigate('/about-us');
    }
  }, [user, navigate]);

  if (!user) return null;

  const isAdmin = user.role === 'ROLE_ADMIN';
  const userId = user.appUserId;

  return (
    <div className="dashboard-wrapper">
      <div className="dashboard-container">
        <h1>{isAdmin ? 'Admin' : 'Authority'} Dashboard</h1>
        <div className="dashboard-grid">
          <Link to="/items" className="dashboard-button">View Items</Link>
          {isAdmin && <Link to="/add-item" className="dashboard-button">Add Item</Link>}
          <Link to="/checkouts" className="dashboard-button">View Checkouts</Link>
          {isAdmin && <Link to="/vendors" className="dashboard-button">View Vendors</Link>}
          {isAdmin && <Link to="/add-vendor" className="dashboard-button">Add Vendor</Link>}
          {isAdmin && <Link to="/purchases" className="dashboard-button">View Purchases</Link>}
          {isAdmin && <Link to="/add-purchase" className="dashboard-button">Add Purchase</Link>}
          <Link to="/inventory-logs" className="dashboard-button">View Inventory Logs</Link>
          <Link to="/add-inventory-log" className="dashboard-button">Add Inventory Log</Link>
          {isAdmin && <Link to="/users" className="dashboard-button">View Users</Link>}
          {isAdmin && <Link to="/add-user" className="dashboard-button">Add User</Link>}
          <button className="self-checkout-button" onClick={enableSelfCheckout}>
            Enable Self Checkout
          </button>
            <Link to={`/change-password/${userId}`} className="change-pass-button">
              Change Password
            </Link>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
