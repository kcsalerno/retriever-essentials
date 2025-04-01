import React, { useState } from 'react';
import axios from 'axios';

function AddItem() {
  const [formData, setFormData] = useState({
    name: '',
    category: '',
    description: '',
    quantity: '',
    nutrition: { calories: '', protein: '', carbs: '', fat: '', sodium: '' },
    image: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name in formData.nutrition) {
      setFormData((prevData) => ({
        ...prevData,
        nutrition: { ...prevData.nutrition, [name]: value }
      }));
    } else {
      setFormData((prevData) => ({
        ...prevData,
        [name]: value
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post('http://localhost:8080/api/item', formData);
      alert('Item added successfully!');
    } catch (error) {
      console.error('Error adding item:', error);
    }
  };

  return (
    <div>
      <h2>Add Item</h2>
      <form onSubmit={handleSubmit}>
        <input type="text" name="name" placeholder="Item Name" onChange={handleChange} required />
        <input type="text" name="category" placeholder="Category" onChange={handleChange} required />
        <input type="text" name="description" placeholder="Description" onChange={handleChange} required />
        <input type="number" name="quantity" placeholder="Quantity" onChange={handleChange} required />

        <h3>Nutrition Facts</h3>
        <input type="number" name="calories" placeholder="Calories" onChange={handleChange} required />
        <input type="number" name="protein" placeholder="Protein (g)" onChange={handleChange} required />
        <input type="number" name="carbs" placeholder="Carbs (g)" onChange={handleChange} required />
        <input type="number" name="fat" placeholder="Fat (g)" onChange={handleChange} required />
        <input type="number" name="sodium" placeholder="Sodium (mg)" onChange={handleChange} required />

        <input type="text" name="image" placeholder="Image Filename (e.g., bread.png)" onChange={handleChange} required />

        <button type="submit">Add Item</button>
      </form>
    </div>
  );
}

export default AddItem;
