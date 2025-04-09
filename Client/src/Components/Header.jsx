import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Header.css";

const Header = ({ cart }) => {
  const [searchTerm, setSearchTerm] = useState("");
  const navigate = useNavigate();

  const handleKeyDown = (e) => {
    if (e.key === "Enter" && searchTerm.trim() !== "") {
      navigate(`/search/${encodeURIComponent(searchTerm.trim())}`);
    }
  };

  return (
    <div className="header" style={{ backgroundImage: 'url(/images/umbc.png)' }}>
      <img src="/images/logo.png" alt="Logo" className="logo" />
      <h1 className="title">Retriever Essentials</h1>
      <input
        type="text"
        className="search-bar"
        placeholder="Search..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        onKeyDown={handleKeyDown}
      />
      <Link to="/checkout" className="cart-icon">
        🛒 ({cart.length})
      </Link>
    </div>
  );
};

export default Header;








