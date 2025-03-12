import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Link, useLocation } from 'react-router-dom';
import Header from './Components/Header';
import Sidebar from './Components/Sidebar';
import ProductGrid from './Components/Grid';
import Pagination from './Components/Pages';
import BottomNav from './Components/Nav';
import AboutUs from './Components/AboutUs'; // Import the About Us page
import './App.css';

function App() {
  const [currentPage, setCurrentPage] = useState(1);
  const totalPages = 5;

  return (
    <Router>
      <div className="app">
        <Header />
        <div className="main-content">
          <Sidebar />
          <div className="content">
            <Routes>
              <Route path="/" element={<AboutUs />} />
              <Route path="/product-grid" element={<ProductGridWithPagination currentPage={currentPage} setCurrentPage={setCurrentPage} totalPages={totalPages} />} />
              <Route path="/about-us" element={<AboutUs />} />
            </Routes>
          </div>
        </div>

        <div
          className="bottom-nav"
          style={{
            backgroundColor: 'black',
            color: 'white',
            textAlign: 'center',
            padding: '20px 0',
            fontSize: '18px',
            display: 'flex',
            justifyContent: 'space-around',
          }}
        >
          <Link to="/about-us" style={{ color: 'white', margin: '0 15px' }}>About Us</Link>
          <Link to="#location" style={{ color: 'white', margin: '0 15px' }}>Location</Link>
          <Link to="#faq" style={{ color: 'white', margin: '0 15px' }}>FAQ</Link>
        </div>

        {/* UMBC Banner inline style */}
        <div
          style={{
            backgroundImage: 'url(/images/umbc.png)',
            backgroundSize: 'cover',
            backgroundPosition: 'center',
            width: '100%',
            height: '90px',
          }}
        ></div>
      </div>
    </Router>
  );
}

// Create a component to handle pagination with location
function ProductGridWithPagination({ currentPage, setCurrentPage, totalPages }) {
  const location = useLocation();
  const showPagination = location.pathname === '/product-grid';

  return (
    <div>
      <ProductGrid />
      {/* Conditionally render Pagination */}
      {showPagination && (
        <Pagination currentPage={currentPage} totalPages={totalPages} onPageChange={setCurrentPage} />
      )}
    </div>
  );
}

export default App;



