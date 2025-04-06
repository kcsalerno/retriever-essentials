import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BrowserRouter as Router, Route, Routes, Link, useLocation } from 'react-router-dom';
import Header from './Components/Header';
import Sidebar from './Components/Sidebar';
import ProductGrid from './Components/Grid';
import Pagination from './Components/Pages';
import BottomNav from './Components/Nav';
import FAQ from './Components/FAQ';
import Location from './Components/Location';
import CategoryPage from './Components/CategoryPage';  // Import the new CategoryPage component
import AboutUs from './Components/AboutUs';
import ScanID from './Components/Scan';
import Checkout from './Components/Checkout';
import ProductDetails from './Components/ProductDetails';
import AddItem from './Components/AddItem';  // New Add Item Page
import './App.css';

function App() {
  const [cart, setCart] = useState([]);
  const [products, setProducts] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const totalPages = 5;

  useEffect(() => {
    axios.get('http://localhost:8080/api/item')
      .then(response => {
        setProducts(response.data);
      })
      .catch(error => {
        console.error('Error fetching products:', error);
      });
  }, []);

  const clearCart = () => {
    setCart([]);
  };

  const addToCart = (product) => {
    setCart(prevCart => {
      const productExists = prevCart.find(item => item.id === product.id);
      if (productExists) {
        return prevCart.map(item =>
          item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
        );
      } else {
        return [...prevCart, { ...product, quantity: 1 }];
      }
    });
  };

  function ProductGridWithPagination({ products }) {
    const location = useLocation();
    const showPagination = location.pathname === '/product-grid';

    return (
      <div>
        <ProductGrid products={products} addToCart={addToCart} />
        {showPagination && (
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={setCurrentPage}
          />
        )}
      </div>
    );
  }

  return (
    <Router>
      <div className="app">
        <Header cart={cart} />
        <div className="main-content">
          <Sidebar />
          <div className="content">
            <Routes>
              <Route path="/" element={<ScanID />} />
              <Route path="/about-us" element={<AboutUs />} />
              <Route path="/location" element={<Location />} />
              <Route path="/pantry" element={<CategoryPage addToCart={addToCart} category="Pantry" />} />
              <Route path="/produce" element={<CategoryPage addToCart={addToCart} category="Produce" />} />
              <Route path="/meat" element={<CategoryPage addToCart={addToCart} category="Meat" />} />
              <Route path="/frozen" element={<CategoryPage addToCart={addToCart} category="Frozen" />} />
              <Route path="/american" element={<CategoryPage addToCart={addToCart} category="American" />} />
              <Route path="/mexican" element={<CategoryPage addToCart={addToCart} category="Mexican" />} />
              <Route path="/indian" element={<CategoryPage addToCart={addToCart} category="Indian" />} />
              <Route path="/bread" element={<CategoryPage addToCart={addToCart} category="Bread" />} />
              <Route path="/asian" element={<CategoryPage addToCart={addToCart} category="Asian" />} />
              <Route path="/faq" element={<FAQ />} />
              <Route path='/checkout' element={<Checkout cart={cart} clearCart={clearCart} />} />
              <Route path="/product-grid" element={<ProductGridWithPagination products={products} />} />
              <Route path="/product/:id" element={<ProductDetails addToCart={addToCart} />} />
              <Route path="/add-item" element={<AddItem />} /> {/* New Add Item Page */}
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
          <Link to="/location" style={{ color: 'white', margin: '0 15px' }}>Location</Link>
          <Link to="/faq" style={{ color: 'white', margin: '0 15px' }}>FAQ</Link>
          <Link to="/add-item">Add New Item</Link>
        </div>

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

export default App;







