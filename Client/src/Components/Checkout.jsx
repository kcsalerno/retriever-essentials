import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Checkout.css';  // Import your custom CSS file

const Checkout = ({ clearCart }) => {
  const [studentId, setStudentId] = useState('');
  const [cartItems, setCartItems] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();
  const authorityId = 1;
  const selfCheckout = true;

  useEffect(() => {
    const storedCart = localStorage.getItem('cart');
    if (storedCart) {
      setCartItems(JSON.parse(storedCart));
    }
  }, []);

  const handleSubmit = async () => {
    if (!studentId || cartItems.length === 0) return;

    // Check if any item in the cart has a quantity greater than the available stock (currentCount)
    const insufficientStock = cartItems.some(item => item.quantity > item.currentCount);

    if (insufficientStock) {
      alert('One or more items in your cart have insufficient stock.');
      return; // Prevent checkout if stock is insufficient
    }

    setIsSubmitting(true);

    try {
      const orderRes = await fetch('http://localhost:8080/api/checkout-order', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          studentId,
          authorityId,
          selfCheckout,
          checkoutDate: new Date().toISOString()
        })
      });

      if (!orderRes.ok) throw new Error('Checkout order failed');
      const createdOrder = await orderRes.json();
      const checkoutOrderId = createdOrder.checkoutOrderId;

      // Update item quantities in the database and add checkout items
      for (const item of cartItems) {
        // Decrease the item quantity in the database
        await fetch(`http://localhost:8080/api/item/${item.itemId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            ...item,
            currentCount: item.currentCount - item.quantity
          })
        });

        // Add the checkout item
        await fetch('http://localhost:8080/api/checkout-item', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            checkoutOrderId,
            itemId: item.itemId,
            quantity: item.quantity
          })
        });
      }

      // Clear the cart in localStorage and state
      localStorage.removeItem('cart');
      setCartItems([]);
      clearCart();  // Call the parent function to clear cart in App.js

      navigate('/'); // Navigate back to home or another relevant page after checkout

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
        <div>
          <ul>
            {cartItems.map((item) => (
              <li key={item.itemId}>
                {item.itemName} — {item.quantity} x ${item.pricePerUnit.toFixed(2)}
              </li>
            ))}
          </ul>

          <div className="total">
            <strong>
              Total: ${cartItems.reduce((total, item) => total + item.pricePerUnit * item.quantity, 0).toFixed(2)}
            </strong>
          </div>
        </div>
      )}

      <button onClick={handleSubmit} disabled={isSubmitting} className="checkout-button">
        {isSubmitting ? 'Processing...' : 'Submit Checkout'}
      </button>
    </div>
  );
};

export default Checkout;





