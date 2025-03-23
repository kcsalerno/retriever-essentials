import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import './ProductDetails.css';

function ProductDetail({ products, addToCart }) {
  const { id } = useParams();
  const [product, setProduct] = useState(null);

  useEffect(() => {
    const foundProduct = products.find((product) => product.id === parseInt(id));
    setProduct(foundProduct);
  }, [id, products]);

  if (!product) return <div>Loading...</div>;

  return (
    <div className="product-detail-container">
      <div className="product-detail-content">
        <img src={product.image} alt={product.name} className="product-image" />
        <h1>{product.name}</h1>
        <p>Quantity: {product.quantity}</p>
        <h3>Nutrition Facts</h3>
        <ul>
          <li>Calories: {product.nutrition.calories}</li>
          <li>Protein: {product.nutrition.protein}g</li>
          <li>Carbs: {product.nutrition.carbs}g</li>
          <li>Fat: {product.nutrition.fat}g</li>
          <li>Sodium: {product.nutrition.sodium}mg</li>
        </ul>
        <button
          className="add-to-cart-btn"
          onClick={() => addToCart(product)}
        >
          Add to Cart
        </button>
      </div>
    </div>
  );
}

export default ProductDetail;





