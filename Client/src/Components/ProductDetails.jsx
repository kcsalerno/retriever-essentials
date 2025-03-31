import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import './ProductDetails.css';

function ProductDetail({ addToCart }) {
  const { id } = useParams();
  const [product, setProduct] = useState(null);

  useEffect(() => {
    axios.get(`http://localhost:8080/api/item/${id}`)
      .then(response => {
        setProduct(response.data);
      })
      .catch(error => {
        console.error('Error fetching product:', error);
      });
  }, [id]);

  if (!product) return <div>Loading...</div>;

  return (
    <div className="product-detail-container">
      <div className="product-detail-content">
      <img src={`/images/${product.image}`} alt={product.name} className="product-image" />
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
        <button className="add-to-cart-btn" onClick={() => addToCart(product)}>Add to Cart</button>
      </div>
    </div>
  );
}

export default ProductDetail;






