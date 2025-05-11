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
  const isAuthority = user.role === 'ROLE_AUTHORITY';
  const userId = user.appUserId;

  return (
    <div className="dashboard-wrapper">
      <div className="dashboard-container">
        <h1>{isAdmin ? 'Admin' : 'Authority'} Dashboard</h1>
        <div className="dashboard-grid">
          <div className="dash-row">
            <div className="dash-column">
              <Link to="/items" className="dashboard-button">View Items</Link>
              {isAdmin && <Link to="/vendors" className="dashboard-button">View Vendors</Link>}
              {isAdmin && <Link to="/purchases" className="dashboard-button">View Purchases</Link>}
              <Link to="/inventory-logs" className="dashboard-button">View Inventory Logs</Link>
              {isAdmin && <Link to="/users" className="dashboard-button">View Users</Link>}
              {isAdmin && <Link to="/checkouts" className="dashboard-button">View Checkouts</Link>}
            </div>
            <div className="dash-column">
              {isAdmin && <Link to="/add-item" className="dashboard-button">Add Item</Link>}
              {isAdmin && <Link to="/add-vendor" className="dashboard-button">Add Vendor</Link>}
              {isAdmin && <Link to="/add-purchase" className="dashboard-button">Add Purchase</Link>}
              {isAuthority ? (<Link to="/checkouts" className="dashboard-button">View Checkouts</Link>) :
                 <Link to="/add-inventory-log" className="dashboard-button">Add Inventory Log</Link>}
              {isAuthority && <Link to="/add-inventory-log" className="dashboard-button">Add Inventory Log</Link>}
              {isAdmin && <Link to="/add-user" className="dashboard-button">Add User</Link>}
            </div>
            <div className="dash-column">
              <button className="self-checkout-button" onClick={enableSelfCheckout}>
                Enable Self Checkout
              </button>
              <Link to={`/change-password/${userId}`} className="change-pass-button">
                Change Password
              </Link>
            </div>
          </div> 
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
