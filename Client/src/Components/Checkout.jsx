import React from 'react';
import './Checkout.css';

function Checkout({ cart, clearCart }) {
  return (
    <div className="checkout-container">
      <h1>Checkout</h1>
      {cart.length === 0 ? (
        <p>Your cart is empty.</p>
      ) : (
        <div>
          <ul>
            {cart.map((item) => (
              <li key={item.id}>
                {item.name} - Quantity: {item.quantity}
              </li>
            ))}
          </ul>
          <button onClick={clearCart}>Checkout</button>
        </div>
      )}
    </div>
  );
}

export default Checkout;
