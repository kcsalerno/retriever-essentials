import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../Contexts/AuthContext';

const ProductGrid = ({ products, addToCart }) => {
    const { user } = useAuth();
    var isAdmin = false
    var isAuthority = false
    if (user) {
      isAdmin = user?.role === 'ROLE_ADMIN';
      isAuthority = user?.role === 'ROLE_AUTHORITY';
    }

  // Function to determine the quantity class based on currentCount
  const getQuantityClass = (quantity) => {
    if (quantity <= 2) {
      return 'quantity-low';   // Red for low stock
    } else if (quantity <= 5) {
      return 'quantity-medium'; // Yellow for medium stock
    } else {
      return 'quantity-high';   // Green for high stock
    }
  };

  if (products.length === 0) {
    return (
      <div className="empty-state">
        <img src="no-items-image.jpg" alt="No items available" style={{ width: '300px', height: 'auto' }} />
        <p>No products available in this category at the moment.</p>
      </div>
    );
  }

  return (
    <div className="product-grid">
      {products.map((product) => (
        <div className="product-card" key={product.itemId}>
          <Link to={`/product/${product.itemName}`} className="product-link">
            <img
              src={product.picturePath || 'default-image.jpg'} // Fallback to default image if missing
              alt={product.itemName}
            />
          </Link>
          <h3>{product.itemName}</h3>
          <p className={`product-quantity ${getQuantityClass(product.currentCount)}`}>
            Quantity: {product.currentCount}
          </p>
          {(isAdmin || isAuthority) && (
            <button onClick={() => addToCart(product)} className="add-to-cart-btn">
              Add to Cart
            </button>
          )}
        </div>
      ))}
    </div>
  );
};

export default ProductGrid;






