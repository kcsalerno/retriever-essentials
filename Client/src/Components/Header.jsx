import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../Contexts/AuthContext";
import { FaUserCircle } from "react-icons/fa";
import "./Header.css";
import "./AdminTable.css";

const Header = ({ cart }) => {
  const [searchTerm, setSearchTerm] = useState("");
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

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

  const handleConfirm = async () => {
    setError("");
    try {
      const res = await fetch("http://localhost:8080/api/auth/re-auth", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: user.email,
          password
        })
      });

      if (!res.ok) {
        setError("Incorrect password.");
        return;
      }

      enableSelfCheckout(); // disables it in your context
      setShowConfirmModal(false);
      setPassword("");
    } catch (err) {
      console.error("Re-auth error:", err);
      setError("Something went wrong.");
    }
  };

  return (
    <div className="header" style={{ backgroundImage: "url(/images/umbc.png)" }}>
      <img src="/images/logo.png" alt="Logo" className="logo" />
      <h1 className="title" style={{ fontSize: "2.5rem", marginTop: "20px" }}>Retriever Essentials</h1>

      <div className="header-right">
        {user ? (
          !selfCheckoutEnabled ? (
            <div className="user-controls">
              <FaUserCircle className="user-icon" />
              <Link to="/dashboard" className="user-email">{user.email}</Link>
              <button className="logout-button" onClick={handleLogout}>Log Out</button>
            </div>
          ) : (
            <div className="user-controls">
              <button className="logout-button" onClick={() => setShowConfirmModal(true)}>
                Disable Self-Checkout
              </button>
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

      {showConfirmModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Confirm Action</h3>
            <p>Please enter your password to disable self-checkout.</p>
            <input
              type="password"
              className="form-control"
              placeholder="Enter password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            {error && <div className="alert alert-danger mt-2">{error}</div>}
            <div className="modal-buttons mt-3">
              <button className="btn add" onClick={handleConfirm}>Confirm</button>
              <button className="btn delete" onClick={() => setShowConfirmModal(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Header;
