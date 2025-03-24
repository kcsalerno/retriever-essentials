import React from "react";
import { Link } from "react-router-dom";
import "./Header.css";

const Header = ({ cart }) => {
  return (
    <div className="header" style={{ backgroundImage: 'url(/images/umbc.png)' }}>
      <img src="/images/logo.png" alt="Logo" className="logo" />
      <h1 className="title">Retriever Essentials</h1>
      <input type="text" className="search-bar" placeholder="Search..." />

      {/* Cart Icon in the Top Right */}
      <Link to="/checkout" className="cart-icon">
        🛒 ({cart.length})
      </Link>
    </div>
  );
};

export default Header;






