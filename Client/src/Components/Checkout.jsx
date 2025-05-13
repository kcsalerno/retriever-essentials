import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth  } from "../Contexts/AuthContext";
import './Checkout.css';

const Checkout = ({ cart, clearCart, removeItemFromCart, updateCartItems }) => {
  const [studentId, setStudentId] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { selfCheckoutEnabled } = useAuth();
  const navigate = useNavigate();
  const authorityId = 1;
  const selfCheckout = selfCheckoutEnabled;
  const cartItems = cart; // Passed down from App.js

  const handleQuantityChange = (itemId, delta) => {
    const updatedItems = cartItems.map(item => {
      if (item.itemId === itemId) {
        const newQty = item.quantity + delta;

        if (newQty > item.itemLimit) {
          alert("You've reached the limit for this item.");
          return item; // Don't update
        }

        if (newQty > item.currentCount) {
          alert("Not enough stock available for this item.");
          return item; // Don't update
        }

        if (newQty < 1) {
          alert("Quantity cannot be less than 1.");
          return item; // Don't update
        }

        return { ...item, quantity: newQty };
      }
      return item;
    });
    updateCartItems(updatedItems);
  };  

  const handleRemoveItem = (itemId) => {
    const updatedItems = cartItems.filter(item => item.itemId !== itemId);
    updateCartItems(updatedItems);
  };

  const handleClearCart = () => {
    clearCart();
  };

  const handleSubmit = async () => {
    if (!studentId || cartItems.length === 0) return;
  
    const insufficientStock = cartItems.some(item => item.quantity > item.currentCount);
    if (insufficientStock) {
      alert('One or more items in your cart have insufficient stock.');
      return;
    }
  
    setIsSubmitting(true);
  
    try {
      const orderPayload = {
        studentId,
        authorityId,
        selfCheckout,
        checkoutDate: new Date().toISOString(),
        checkoutItems: cartItems.map(item => ({
          itemId: item.itemId,
          quantity: item.quantity
        }))
      };
  
      console.log("Submitting order payload:", orderPayload); // for debug
  
      const response = await fetch('http://localhost:8080/api/checkout-order', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(orderPayload)
      });
  
      if (!response.ok) {
        throw new Error('Checkout order failed');
      }
      else {
        alert('Checkout successful!');
        window.dispatchEvent(new Event('categoryUpdated'));
      }
  
      handleClearCart();
      if (selfCheckout) {
        navigate('/popular');
      }
      else {
        navigate('/dashboard');
      }
  
    } catch (err) {
      console.error(err);
      alert('Checkout failed!');
    } finally {
      setIsSubmitting(false);
    }
  };
  

  return (
    <div className="checkout-container">
      <h1>Checkout</h1>

      <input
        type="text"
        placeholder="Enter Student ID"
        value={studentId}
        onChange={(e) => setStudentId(e.target.value)}
        className="checkout-input"
      />

      {cartItems.length === 0 ? (
        <p>Your cart is empty.</p>
      ) : (
        <>
          <ul>
            {cartItems.map((item) => (
              <li key={item.itemId}>
                <strong>{item.itemName}</strong> :
                <button onClick={() => handleQuantityChange(item.itemId, -1)} disabled={item.quantity <= 1}>−</button>
                {` ${item.quantity} `}
                <button onClick={() => handleQuantityChange(item.itemId, 1)} disabled={item.quantity >= item.itemLimit}>+</button>
                {/* {` x $${item.pricePerUnit.toFixed(2)} `} */}
                <button onClick={() => handleRemoveItem(item.itemId)} style={{ color: 'red' }}>❌</button>
              </li>
            ))}
          </ul>

          {/* <div className="total">
            <strong>
              Total: ${cartItems.reduce((total, item) => total + item.pricePerUnit * item.quantity, 0).toFixed(2)}
            </strong>
          </div> */}

          <div style={{ marginTop: '20px' }}>
            <button onClick={handleSubmit} disabled={isSubmitting} className="checkout-button">
              {isSubmitting ? 'Processing...' : 'Submit Checkout'}
            </button>
            <button onClick={handleClearCart} className="clear-cart-button" style={{ marginLeft: '10px' }}>
              Empty Cart
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default Checkout;
