// src/App.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  BrowserRouter as Router,
  Route,
  Routes,
  Link,
  useLocation
} from 'react-router-dom';

import Header from './Components/Header';
import Sidebar from './Components/Sidebar';
import ProductGrid from './Components/Grid';
import Pagination from './Components/Pages';
import FAQ from './Components/FAQ';
import Location from './Components/Location';
import CategoryPage from './Components/CategoryPage';
import AboutUs from './Components/AboutUs';
import Login from './Components/Login';
import Checkout from './Components/Checkout';
import ProductDetails from './Components/ProductDetails';
import ItemList from './Components/ItemList';
import SearchResults from './Components/SearchResults';
import VendorList from './Components/VendorList';
import BusyTimes from './Components/BusyTimes';
import NotFound from './Components/NotFound';
import PrivateRoute from './Components/PrivateRoute';
import Unauthorized from './Components/Unauthorized';
import Dashboard from './Components/Dashboard';
import PopularStats from './Components/PopularStats';
import ItemForm from './Components/ItemForm';
import VendorForm from './Components/VendorForm';
import CheckoutList from './Components/CheckoutList';
import PurchaseList from './Components/PurchaseList';
import UserList from './Components/UserList';
import InventoryLogList from './Components/InventoryLogList';
import CheckoutForm from './Components/CheckoutForm';
import AddPurchaseForm from './Components/AddPurchaseForm';
import InventoryLogForm from './Components/InventoryLogForm';
import UserForm from './Components/UserForm';
import ChangePasswordForm from './Components/ChangePasswordForm';
import EditCheckoutItemForm from './Components/EditCheckoutItemForm';

import { AuthProvider, useAuth } from './Contexts/AuthContext';
import './App.css';
import PurchaseForm from './Components/PurchaseForm';
import PurchaseItemForm from './Components/EditPurchaseItemForm';

function BottomNav() {
  const { user, selfCheckoutEnabled } = useAuth();
  const isAdmin = user?.role === 'ROLE_ADMIN';
  const isAuthority = user?.role === 'ROLE_AUTHORITY';

  return (
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
      <Link
        to="/busy-times"
        state={{ readOnly: !isAdmin }}
        style={{ color: 'white', margin: '0 15px' }}
      >
        Busy Times
      </Link>
      <Link to="/popular" style={{ color: 'white', margin: '0 15px' }}>Trending</Link>
      {(isAdmin || isAuthority) && !selfCheckoutEnabled && <Link to="/dashboard" style={{ color: 'white', margin: '0 15px' }}>Dashboard</Link>}
    </div>
  );
}

function ProductGridWithPagination({ products, addToCart, currentPage, totalPages, setCurrentPage }) {
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

function App() {
  const [cart, setCart] = useState([]);
  const [products, setProducts] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const totalPages = 5;

  useEffect(() => {
    const storedCart = localStorage.getItem('cart');
    if (storedCart) {
      setCart(JSON.parse(storedCart));
    }

    axios.get('http://localhost:8080/api/item')
      .then(response => setProducts(response.data))
      .catch(error => console.error('Error fetching products:', error));
  }, []);

  const clearCart = () => {
    setCart([]);
    localStorage.removeItem('cart');
  };

  const addToCart = (product) => {
    setCart(prevCart => {
      const existing = prevCart.find(item => item.itemId === product.itemId);
      let updatedCart;
  
      if (existing) {
        if (existing.quantity >= product.itemLimit) {
          alert("You've reached the limit for this item.");
          return prevCart; // don't update
        }
  
        updatedCart = prevCart.map(item =>
          item.itemId === product.itemId
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      } else {
        updatedCart = [...prevCart, { ...product, quantity: 1 }];
      }
  
      localStorage.setItem('cart', JSON.stringify(updatedCart));
      return updatedCart;
    });
  };

  const updateCartItems = (updatedItems) => {
    setCart(updatedItems);
    localStorage.setItem('cart', JSON.stringify(updatedItems));
  };
  
  const removeItemFromCart = (itemId) => {
    const updatedCart = cart.filter(item => item.itemId !== itemId);
    setCart(updatedCart);
    localStorage.setItem('cart', JSON.stringify(updatedCart));
  };  

  return (
    <AuthProvider>
      <Router>
        <div className="app">
          <Header cart={cart} />
          <div className="main-content">
            <Sidebar />
            <div className="content">
              <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/about-us" element={<AboutUs />} />
                <Route path="/location" element={<Location />} />
                {/* <Route path="/:category" element={<CategoryPage addToCart={addToCart} />} /> */}
                <Route path="/category/:category" element={<CategoryPage addToCart={addToCart} />} />
                <Route path="/faq" element={<FAQ />} />
                <Route path='/checkout' element={<Checkout cart={cart} clearCart={clearCart} removeItemFromCart={removeItemFromCart}  updateCartItems={updateCartItems}/>} />
                <Route
                  path="/product-grid"
                  element={
                    <ProductGridWithPagination
                      products={products}
                      addToCart={addToCart}
                      currentPage={currentPage}
                      totalPages={totalPages}
                      setCurrentPage={setCurrentPage}
                    />
                  }
                />
                <Route path="/product/:name" element={<ProductDetails addToCart={addToCart} />} />
                <Route path="/add-item" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN']} selfCheckoutEnabled={true}>
                    <ItemForm isEditMode={false} />
                  </PrivateRoute>
                } />
                <Route path="/search/:searchTerm" element={<SearchResults addToCart={addToCart} />} />
                <Route path="/edit-product/:name" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN', 'ROLE_AUTHORITY']} selfCheckoutEnabled={true}>
                    <ItemForm isEditMode={true} />
                  </PrivateRoute>
                } />
                <Route path="/items" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN', 'ROLE_AUTHORITY']} selfCheckoutEnabled={true}>
                    <ItemList />
                  </PrivateRoute>
                } />
                <Route path="/busy-times" element={<BusyTimes />} />
                <Route path="/dashboard" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN', 'ROLE_AUTHORITY']} selfCheckoutEnabled={true}>
                    <Dashboard />
                  </PrivateRoute>
                } />
                <Route path="/vendors" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN']} selfCheckoutEnabled={true}>
                    <VendorList />
                  </PrivateRoute>
                } />
                <Route path="/add-vendor" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN']} selfCheckoutEnabled={true}>
                    <VendorForm isEditMode={false} />
                  </PrivateRoute>
                } />
                <Route path="/edit-vendor/:vendorId" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN']} selfCheckoutEnabled={true}>
                    <VendorForm isEditMode={true} />
                  </PrivateRoute>
                } />
                <Route path="/checkouts" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN', 'ROLE_AUTHORITY']} selfCheckoutEnabled={true}>
                    <CheckoutList />
                  </PrivateRoute>
                } />
                {<Route path="/edit-checkout/:checkoutId" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN', 'ROLE_AUTHORITY']} selfCheckoutEnabled={true}>
                    <CheckoutForm />
                  </PrivateRoute>
                } />
                /*{ <Route path="/delete-checkout/:checkoutId" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN']} selfCheckoutEnabled={true}>
                    <CheckoutList isEditMode={true} />
                  </PrivateRoute>
                } /> */}
                <Route path="/edit-checkout-item/:checkoutItemId" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN', 'ROLE_AUTHORITY']} selfCheckoutEnabled={true}>
                    <EditCheckoutItemForm />
                  </PrivateRoute>
                } />
                <Route path="/purchases" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN',]} selfCheckoutEnabled={true}>
                    <PurchaseList />
                  </PrivateRoute>
                } />
                <Route path="/add-purchase" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN']} selfCheckoutEnabled={true}>
                    <AddPurchaseForm isEditMode={false} />
                  </PrivateRoute>
                } />
                <Route path="/edit-purchase/:purchaseId" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN']} selfCheckoutEnabled={true}>
                    <PurchaseForm isEditMode={true} />
                  </PrivateRoute>
                } />
                <Route path="/edit-purchase-item/:purchaseItemId" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN']} selfCheckoutEnabled={true}>
                    <PurchaseItemForm isEditMode={true} />
                  </PrivateRoute>
                } />
                {/* <Route path="/delete-purchase/:purchaseId" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN']} selfCheckoutEnabled={true}>
                    <PurchaseList isEditMode={true} />
                  </PrivateRoute>
                } /> */}
                <Route path="/inventory-logs" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN', 'ROLE_AUTHORITY']} selfCheckoutEnabled={false}>
                    <InventoryLogList />
                  </PrivateRoute>
                } />
                <Route path="/add-inventory-log" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN', 'ROLE_AUTHORITY']} selfCheckoutEnabled={true}>
                    <InventoryLogForm isEditMode={false} />
                  </PrivateRoute>
                } />
                <Route path="/edit-inventory-log/:logId" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN', 'ROLE_AUTHORITY']} selfCheckoutEnabled={true}>
                    <InventoryLogForm isEditMode={true} />
                  </PrivateRoute>
                } />
                {/* <Route path="/delete-inventory-log/:logId" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN', 'ROLE_AUTHORITY']} selfCheckoutEnabled={true}>
                    <InventoryLogList isEditMode={true} />
                  </PrivateRoute>
                } /> */}
                <Route path="/users" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN']} selfCheckoutEnabled={true}>
                    <UserList />
                  </PrivateRoute>
                } />
                <Route path="/add-user" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN']} selfCheckoutEnabled={true}>
                    <UserForm isEditMode={false} />
                  </PrivateRoute>
                } />
                <Route path="/edit-user/:userId" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN']} selfCheckoutEnabled={true}>
                    <UserForm isEditMode={true} />
                  </PrivateRoute>
                } />
                <Route path="/change-password/:userId" element={
                  <PrivateRoute allowedRoles={['ROLE_ADMIN']} selfCheckoutEnabled={true}>
                    <ChangePasswordForm />
                  </PrivateRoute>
                } />
                <Route path="/unauthorized" element={<Unauthorized />} />
                <Route path="/popular" element={<PopularStats />} />
                <Route path="*" element={<NotFound />} />
              </Routes>
            </div>
          </div>

          <BottomNav />

          <div
            style={{
              backgroundImage: 'url(/images/umbc.png)',
              backgroundSize: 'cover',
              backgroundPosition: 'center',
              width: '100%',
              height: '90px',
            }}
          />
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
