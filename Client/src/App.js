import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Link, useLocation } from 'react-router-dom';
import Header from './Components/Header';
import Sidebar from './Components/Sidebar';
import ProductGrid from './Components/Grid';
import Pagination from './Components/Pages';
import BottomNav from './Components/Nav';
import FAQ from './Components/FAQ';
import Location from './Components/Location';
import Pantry from './Components/Pantry';
import Produce from './Components/Produce';
import Meat from './Components/Meat';
import Frozen from './Components/Frozen';
import American from './Components/American';
import Mexican from './Components/Mexican';
import Indian from './Components/Indian';
import Bread from './Components/Bread';
import Asian from './Components/Asian';
import AboutUs from './Components/AboutUs';
import ScanID from './Components/Scan';
import './App.css';
import Checkout from './Components/Checkout';
import ProductDetails from './Components/ProductDetails';

function App() {
  const [cart, setCart] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const totalPages = 5;

  const clearCart = () => {
    setCart([]);  // Clears the cart
  };
  const products = [
    { id: 1, name: "Whole Wheat Bread", image: "/images/wheat.png", quantity: 10, nutrition: { fat: 2, protein: 4, carbs: 21, sodium: 170, calories: 110 } },
    { id: 2, name: "Bagels", image: "/images/bag.png", quantity: 5, nutrition: { fat: 1.5, protein: 10, carbs: 53, sodium: 450, calories: 270 } },
    { id: 3, name: "Croissants", image: "/images/cro.png", quantity: 8, nutrition: { fat: 17, protein: 4, carbs: 27, sodium: 105, calories: 270 } },
    { id: 25, name: "Mac & Cheese", image: "/images/mac.png", quantity: 10, nutrition: { fat: 2.5, protein: 9, carbs: 48, sodium: 560, calories: 260 } },
    { id: 26, name: "Burgers", image: "/images/bur.png", quantity: 5, nutrition: { fat: 28, protein: 18, carbs: 0, sodium: 80, calories: 330 } },
    { id: 27, name: "Hot Dogs", image: "/images/hot.png", quantity: 8, nutrition: { fat: 10, protein: 6, carbs: 2, sodium: 400, calories: 150 } },
    { id: 22, name: "Rice", image: "/images/Rice.png", quantity: 10, nutrition: { fat: 0.5, protein: 4, carbs: 38, sodium: 0, calories: 170 } },
    { id: 23, name: "Pocky Sticks", image: "/images/Pocky.png", quantity: 5, nutrition: { fat: 9, protein: 4, carbs: 26, sodium: 70, calories: 200 } },
    { id: 24, name: "Ramen 12 Pack", image: "/images/Ramen.png", quantity: 8, nutrition: { fat: 7, protein: 4, carbs: 27, sodium: 760, calories: 190 } },
    { id: 4, name: "Frozen Pizza", image: "/images/pizza.png", quantity: 12, nutrition: { fat: 17, protein: 4, carbs: 27, sodium: 105, calories: 270 } },
    { id: 5, name: "Ice Cream", image: "/images/cream.png", quantity: 20, nutrition: { fat: 7, protein: 4, carbs: 32, sodium: 40, calories: 210 } },
    { id: 6, name: "Frozen Broccoli", image: "/images/bro.png", quantity: 15, nutrition: { fat: 0.5, protein: 3, carbs: 12, sodium: 20, calories: 55 } },
    { id: 7, name: "Basmati Rice", image: "/images/bas.png", quantity: 15, nutrition: { fat: 0.5, protein: 4, carbs: 45, sodium: 0, calories: 200 } },
    { id: 8, name: "Lentils", image: "/images/len.png", quantity: 10, nutrition: { fat: 0.5, protein: 18, carbs: 40, sodium: 10, calories: 230 } },
    { id: 9, name: "Curry Powder", image: "/images/curry.png", quantity: 8, nutrition: { fat: 1.5, protein: 1, carbs: 12, sodium: 0, calories: 35 } },
    { id: 10, name: "Chicken Breast", image: "/images/chicken.png", quantity: 15, nutrition: { fat: 3, protein: 27, carbs: 0, sodium: 60, calories: 150 } },
    { id: 11, name: "Ground Beef", image: "/images/beef.png", quantity: 10, nutrition: { fat: 22, protein: 22, carbs: 0, sodium: 75, calories: 250 } },
    { id: 12, name: "Bacon", image: "/images/bacon.png", quantity: 8, nutrition: { fat: 14, protein: 12, carbs: 1, sodium: 850, calories: 150 } },
    { id: 13, name: "Tortillas", image: "/images/tort.png", quantity: 15, nutrition: { fat: 3, protein: 3, carbs: 19, sodium: 180, calories: 150 } },
    { id: 14, name: "Salsa", image: "/images/sal.png", quantity: 12, nutrition: { fat: 0.5, protein: 1, carbs: 6, sodium: 200, calories: 35 } },
    { id: 15, name: "Black Beans", image: "/images/bean.png", quantity: 20, nutrition: { fat: 1, protein: 15, carbs: 40, sodium: 0, calories: 220 } },
    { id: 19, name: "Frosted Flakes", image: "/images/ff.png", quantity: 12, nutrition: { fat: 1.5, protein: 2, carbs: 28, sodium: 200, calories: 110 } },
    { id: 20, name: "Spaghetti", image: "/images/spaghetti.png", quantity: 20, nutrition: { fat: 1, protein: 7, carbs: 42, sodium: 0, calories: 200 } },
    { id: 21, name: "Peanut Butter", image: "/images/pb.png", quantity: 15, nutrition: { fat: 16, protein: 8, carbs: 6, sodium: 2, calories: 190 } },
    { id: 16, name: "Apples", image: "/images/apple.png", quantity: 30, nutrition: { fat: 0.5, protein: 0.5, carbs: 25, sodium: 0, calories: 95 } },
    { id: 17, name: "Carrots", image: "/images/carrot.png", quantity: 25, nutrition: { fat: 0.1, protein: 1, carbs: 12, sodium: 50, calories: 50 } },
    { id: 18, name: "Lettuce", image: "/images/lettuce.png", quantity: 10, nutrition: { fat: 0.1, protein: 1, carbs: 3, sodium: 15, calories: 15 } }
  ];
  
  const addToCart = (product) => {
    setCart((prevCart) => {
      const productExists = prevCart.find(item => item.id === product.id);
      if (productExists) {
        return prevCart.map(item =>
          item.id === product.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      } else {
        return [...prevCart, { ...product, quantity: 1 }];
      }
    });
  };

  // Product Grid with Pagination Component
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
        <Header cart={cart} /> {/* Pass cart state as a prop */}
        <div className="main-content">
          <Sidebar />
          <div className="content">
            <Routes>
              <Route path="/" element={<ScanID />} />
              <Route path="/about-us" element={<AboutUs />} />
              <Route path="/location" element={<Location />} />
              <Route path="/pantry" element={<Pantry addToCart={addToCart}/>} />
              <Route path="/produce" element={<Produce addToCart={addToCart}/>} />
              <Route path="/meat" element={<Meat addToCart={addToCart}/>} />
              <Route path="/frozen" element={<Frozen addToCart={addToCart}/>} />
              <Route path="/american" element={<American addToCart={addToCart}/>} />
              <Route path="/mexican" element={<Mexican addToCart={addToCart}/>} />
              <Route path="/indian" element={<Indian addToCart={addToCart} />} />
              <Route path="/bread" element={<Bread addToCart={addToCart} />} />
              <Route path="/asian" element={<Asian addToCart={addToCart}/>} />
              <Route path="/faq" element={<FAQ />} />
              <Route path='/checkout' element={<Checkout cart={cart} clearCart={clearCart} />} />
              <Route path="/product-grid" element={<ProductGridWithPagination products={[]} />} />
              <Route path="/product/:id" element={<ProductDetails products={products} addToCart={addToCart}/>} />
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

export default App;






