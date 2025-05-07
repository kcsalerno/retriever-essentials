import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth  } from "../Contexts/AuthContext";
import { FaUserCircle } from "react-icons/fa"; // icon
import "./Header.css";

const Header = ({ cart }) => {
  const [searchTerm, setSearchTerm] = useState("");
  const navigate = useNavigate();
  const { user, logout, selfCheckoutEnabled, enableSelfCheckout } = useAuth();

  const handleKeyDown = (e) => {
    if (e.key === "Enter" && searchTerm.trim() !== "") {
      navigate(`/search/${encodeURIComponent(searchTerm.trim())}`);
    }
  };

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  const getTotalCartQuantity = () => {
    return cart.reduce((total, item) => total + item.quantity, 0);
  };

  return (
    <div className="header" style={{ backgroundImage: 'url(/images/umbc.png)' }}>
      <img src="/images/logo.png" alt="Logo" className="logo" />
      <h1 className="title" style={{ fontSize: '2.5rem', marginTop: '20px' }}>Retriever Essentials</h1>
  
      <div className="header-right">
        {user ? (
          !selfCheckoutEnabled ? (
            <div className="user-controls">
              <FaUserCircle className="user-icon" />
              <Link to="/dashboard" className="user-email">{user.email}</Link>
              <button className="logout-button" onClick={logout}>Log Out</button>
            </div>
          ) : (
            <div className="user-controls">
              <button className="logout-button" onClick={enableSelfCheckout}>Disable Self-Checkout</button>
            </div>
          )
        ) : (
          <button className="login-button" onClick={() => navigate("/")}>Log In</button>
        )}
        <Link to="/checkout" className="cart-icon">🛒 ({getTotalCartQuantity()})</Link>
        <input
          type="text"
          className="search-bar"
          placeholder="Search..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          onKeyDown={handleKeyDown}
        />
      </div>
    </div>
  );
};

export default Header;
