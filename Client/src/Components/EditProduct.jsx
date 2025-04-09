import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './ProductDetails.css'; // Use the same CSS file to keep the styles consistent

function EditProduct() {
  const { name } = useParams();  // `name` is the itemName from the URL
  const [product, setProduct] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    // Fetch the product details using the `name` parameter
    axios.get(`http://localhost:8080/api/item/name/${name}`)
      .then(response => {
        setProduct(response.data);
      })
      .catch(error => {
        console.error('Error fetching product:', error);
      });
  }, [name]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
  
    // Directly update the quantity (currentCount)
    if (name === 'currentCount') {
      setProduct({
        ...product,
        [name]: Number(value)  // Ensure value is treated as a number
      });
    } else {
      setProduct({
        ...product,
        [name]: value,
      });
    }
  };
  
  const handleSaveChanges = () => {
    // Ensure currentCount is a valid number before saving
    if (product.currentCount < 0) {
      alert("Quantity can't be negative!");
      return;
    }
  
    // Make the API call to update the product
    axios.put(`http://localhost:8080/api/item/${product.itemId}`, product)
      .then(response => {
        // After saving, update localStorage cart
        const storedCart = localStorage.getItem('cart');
        if (storedCart) {
          const cart = JSON.parse(storedCart);
          const updatedCart = cart.map(item => 
            item.itemId === product.itemId ? { ...item, currentCount: product.currentCount } : item
          );
          localStorage.setItem('cart', JSON.stringify(updatedCart));
        }
  
        // Navigate back to the product details page
        navigate(`/product-details/${product.itemName}`);
      })
      .catch(error => {
        console.error('Error saving product:', error);
      });
  };
  
  

  if (!product) return <div>Loading...</div>;

  return (
    <div className="product-detail-container">
      <div className="product-detail-content">
        <img src={product.picturePath} alt={product.itemName} className="product-image" />
        <h1>Edit Product: {product.itemName}</h1>
        
        {/* Editable fields */}
        <div className="form-group">
          <label>Item Name</label>
          <input
            type="text"
            name="itemName"
            value={product.itemName}
            onChange={handleInputChange}
            className="input-field"
          />
        </div>

        <div className="form-group">
          <label>Quantity</label>
          <input
            type="number"
            name="currentCount"
            value={product.currentCount}
            onChange={handleInputChange}
            className="input-field"
          />
        </div>

        <h3>Nutrition Facts</h3>
        <div className="form-group">
          <label>Calories</label>
          <input
            type="number"
            name="nutritionFacts.calories"
            value={product.nutritionFacts.calories}
            onChange={handleInputChange}
            className="input-field"
          />
        </div>

        <div className="form-group">
          <label>Protein (g)</label>
          <input
            type="number"
            name="nutritionFacts.protein"
            value={product.nutritionFacts.protein}
            onChange={handleInputChange}
            className="input-field"
          />
        </div>

        <div className="form-group">
          <label>Carbs (g)</label>
          <input
            type="number"
            name="nutritionFacts.carbs"
            value={product.nutritionFacts.carbs}
            onChange={handleInputChange}
            className="input-field"
          />
        </div>

        <div className="form-group">
          <label>Fat (g)</label>
          <input
            type="number"
            name="nutritionFacts.fat"
            value={product.nutritionFacts.fat}
            onChange={handleInputChange}
            className="input-field"
          />
        </div>

        <div className="form-group">
          <label>Sodium (mg)</label>
          <input
            type="number"
            name="nutritionFacts.sodium"
            value={product.nutritionFacts.sodium}
            onChange={handleInputChange}
            className="input-field"
          />
        </div>

        {/* Save Changes Button */}
        <button className="add-to-cart-btn" onClick={handleSaveChanges}>Save Changes</button>
      </div>
    </div>
  );
}

export default EditProduct;

