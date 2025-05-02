import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './ProductDetails.css';
import { UserContext } from './UserContext';

function ProductDetails({ addToCart }) {
  const { name } = useParams();
  const [product, setProduct] = useState(null);
  const navigate = useNavigate();
  const { isAdmin } = useContext(UserContext); 


  useEffect(() => {
    axios.get(`http://localhost:8080/api/item/name/${name}`)
      .then(response => {
        console.log("Nutrition Facts:", response.data.nutritionFacts);  // Log the nutrition facts
        setProduct(response.data);
      })
      .catch(error => {
        console.error('Error fetching product:', error);
      });
  }, [name]);
  
  useEffect(() => {
    // Using the `name` endpoint to search by itemName
    axios.get(`http://localhost:8080/api/item/name/${name}`)
      .then(response => {
        setProduct(response.data);
      })
      .catch(error => {
        console.error('Error fetching product:', error);
      });
  }, [name]);

  if (!product) return <div>Loading...</div>;

const parseNutritionFacts = (nutritionString) => {
  const nutritionObj = {};
  const nutritionArray = nutritionString.split(','); // Split by commas
  nutritionArray.forEach(item => {
    const [key, value] = item.split(':'); // Split by colon to get key and value
    if (key && value) {
      nutritionObj[key.trim()] = value.trim(); // Remove extra spaces around keys and values
    }
  });
  return nutritionObj;
};


  const nutrition = parseNutritionFacts(product.nutritionFacts);

  return (
    <div className="product-detail-container">
      <div className="product-detail-content">
        <img src={product.picturePath} alt={product.itemName} className="product-image" />
        <h1>{product.itemName}</h1>
        <p>Quantity: {product.currentCount}</p>
        <h3>Nutrition Facts</h3>
        <ul>
          <li>Calories: {nutrition['Calories']}</li>
          <li>Protein: {nutrition['Protein']}</li>
          <li>Carbs: {nutrition['Carbs']}</li>
          <li>Fat: {nutrition['Fat']}</li>
          <li>Sodium: {nutrition['Sodium']}</li>
        </ul>
        <button
        className="add-to-cart-btn"
        onClick={() => {
        if (product.currentCount <= 0) {
        alert("There's none of this left.");
        } else {
        addToCart(product);
      }
    }}
>
    Add to Cart
    </button>


        {isAdmin && (
          <button className="update-item-btn" onClick={() => navigate(`/edit-product/${product.itemName}`)}>
          Edit Item
         </button>
        )}
      </div>
    </div>
  );
}

export default ProductDetails;









