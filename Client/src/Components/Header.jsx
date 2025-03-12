import React from "react";
import "./Header.css";

const Header = () => {
  return (
    <div className="header" style={{ backgroundImage: 'url(/images/umbc.png)' }}>
      <img src="/images/logo.png" alt="Logo" className="logo" />
      <h1 className="title">Retriever Essentials</h1>
      <input type="text" className="search-bar" placeholder="Search..." />
    </div>
  );
};

export default Header;





