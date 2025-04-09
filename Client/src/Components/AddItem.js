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

  const [imageFile, setImageFile] = useState(null);
  const [imageUrl, setImageUrl] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name in formData.nutrition) {
      setFormData((prevData) => ({
        ...prevData,
        nutrition: { ...prevData.nutrition, [name]: value },
      }));
    } else {
      setFormData((prevData) => ({
        ...prevData,
        [name]: value,
      }));
    }
  };

  const handleImageChange = (e) => {
    setImageFile(e.target.files[0]); // Set the image file selected by the user
  };

  const uploadImageToCloudinary = async () => {
    const cloudinaryUrl = 'https://api.cloudinary.com/v1_1/re-images/image/upload'; // Cloudinary URL
    const uploadPreset = 'unsigned_upload'; // Use your upload preset

    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('upload_preset', uploadPreset);

    try {
      const response = await axios.post(cloudinaryUrl, formData);
      setImageUrl(response.data.secure_url); // Get the image URL from Cloudinary
      return response.data.secure_url; // Return the URL
    } catch (error) {
      console.error('Error uploading image:', error);
      return null;
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    // Upload the image to Cloudinary before submitting the form
    const imageUrl = await uploadImageToCloudinary();
    if (!imageUrl) {
      alert('Image upload failed!');
      return;
    }
  
    const newItem = {
      itemName: formData.name,
      itemDescription: formData.description,
      nutritionFacts: `Calories: ${formData.nutrition.calories} per serving`,
      picturePath: imageUrl,
      category: formData.category,
      currentCount: Number(formData.quantity),
      itemLimit: Number(5),
      pricePerUnit: 1.99,
      enabled: false
    };
    
    
    console.log('Sending newItem to backend:', newItem);

    try {
      await axios.post('http://localhost:8080/api/item', newItem, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      alert('Item added successfully!');
      setFormData({
        name: '',
        category: '',
        description: '',
        quantity: '',
        nutrition: { calories: '', protein: '', carbs: '', fat: '', sodium: '' },
        image: '',
      });
      setImageFile(null);
      setImageUrl('');
    } catch (error) {
      console.error('Error adding item:', error.response ? error.response.data : error.message);
      alert('Error adding item');
    }
  };
  

  return (
    <div>
      <h2>Add Item</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="name"
          placeholder="Item Name"
          onChange={handleChange}
          value={formData.name}
          required
        />
        <input
          type="text"
          name="category"
          placeholder="Category"
          onChange={handleChange}
          value={formData.category}
          required
        />
        <input
          type="text"
          name="description"
          placeholder="Description"
          onChange={handleChange}
          value={formData.description}
          required
        />
        <input
          type="number"
          name="quantity"
          placeholder="Quantity"
          onChange={handleChange}
          value={formData.quantity}
          required
        />

        <h3>Nutrition Facts</h3>
        <input
          type="number"
          name="calories"
          placeholder="Calories"
          onChange={handleChange}
          value={formData.nutrition.calories}
          required
        />
        <input
          type="number"
          name="protein"
          placeholder="Protein (g)"
          onChange={handleChange}
          value={formData.nutrition.protein}
          required
        />
        <input
          type="number"
          name="carbs"
          placeholder="Carbs (g)"
          onChange={handleChange}
          value={formData.nutrition.carbs}
          required
        />
        <input
          type="number"
          name="fat"
          placeholder="Fat (g)"
          onChange={handleChange}
          value={formData.nutrition.fat}
          required
        />
        <input
          type="number"
          name="sodium"
          placeholder="Sodium (mg)"
          onChange={handleChange}
          value={formData.nutrition.sodium}
          required
        />

        <div>
          <label>Upload Image:</label>
          <input
            type="file"
            accept="image/*"
            onChange={handleImageChange}
            required
          />
        </div>

        <button type="submit">Add Item</button>
      </form>

      {imageUrl && (
        <div>
          <h4>Uploaded Image:</h4>
          <img src={imageUrl} alt="Uploaded item" style={{ width: '200px' }} />
        </div>
      )}
    </div>
  );
}

export default AddItem;
