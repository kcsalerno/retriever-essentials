import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import './ItemForm.css';
import { Link } from 'react-router-dom';

function ItemForm({ isEditMode }) {
  const { name } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    itemName: '',
    category: '',
    itemDescription: '',
    pricePerUnit: '',
    currentCount: '',
    itemLimit: '',
    nutritionFactsParsed: {
      calories: '',
      protein: '',
      carbs: '',
      fat: '',
      sodium: ''
    },
    picturePath: '',
    enabled: true // default to true when adding
  });

  const [imageFile, setImageFile] = useState(null);
  const [errors, setErrors] = useState([]);

  useEffect(() => {
    if (isEditMode && name) {
      axios.get(`http://localhost:8080/api/item/name/${name}`)
        .then((res) => {
          const item = res.data;
          const parsed = {};
          item.nutritionFacts?.split(',').forEach(pair => {
            const [key, value] = pair.split(':');
            if (key && value) parsed[key.trim().toLowerCase()] = value.trim();
          });

          setFormData({
            itemName: item.itemName,
            category: item.category,
            itemDescription: item.itemDescription,
            pricePerUnit: item.pricePerUnit || 0.00,
            currentCount: item.currentCount,
            itemLimit: item.itemLimit || 5,
            nutritionFactsParsed: parsed,
            picturePath: item.picturePath,
            itemId: item.itemId,
            enabled: item.enabled ?? true
          });
        })
        .catch(() => {
          alert("Failed to load item for editing.");
          navigate('/items');
        });
    }
  }, [isEditMode, name, navigate]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;

    if (type === "checkbox") {
      setFormData(prev => ({ ...prev, [name]: checked }));
    } else if (name.startsWith("nutritionFacts.")) {
      const field = name.split('.')[1];
      setFormData(prev => ({
        ...prev,
        nutritionFactsParsed: { ...prev.nutritionFactsParsed, [field]: value }
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: value
      }));
    }
  };

  const handleImageChange = (e) => {
    setImageFile(e.target.files[0]);
  };

  const uploadImageToCloudinary = async () => {
    if (!imageFile) return formData.picturePath;

    const cloudinaryUrl = 'https://api.cloudinary.com/v1_1/re-images/image/upload';
    const uploadPreset = 'unsigned_upload';

    const data = new FormData();
    data.append('file', imageFile);
    data.append('upload_preset', uploadPreset);

    try {
      const res = await axios.post(cloudinaryUrl, data);
      return res.data.secure_url;
    } catch (err) {
      console.error("Image upload failed", err);
      return null;
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const imageUrl = await uploadImageToCloudinary();
    if (!imageUrl && !formData.picturePath) {
      alert('Image is required.');
      return;
    }

    const nutritionString = Object.entries(formData.nutritionFactsParsed)
      .map(([key, value]) => `${capitalize(key)}: ${value}`)
      .join(', ');

    const payload = {
      itemName: formData.itemName,
      category: formData.category,
      itemDescription: formData.itemDescription,
      nutritionFacts: nutritionString,
      picturePath: imageUrl || formData.picturePath,
      currentCount: Number(formData.currentCount),
      itemLimit: Number(formData.itemLimit) || 5,
      pricePerUnit: parseFloat(formData.pricePerUnit).toFixed(2),
      enabled: formData.enabled
    };

    if (isEditMode) {
      payload.itemId = formData.itemId;
    }

    try {
      if (isEditMode) {
        await axios.put(`http://localhost:8080/api/item/${formData.itemId}`, payload);
        window.dispatchEvent(new Event('categoryUpdated'));
        alert('Item updated!');
      } else {
        await axios.post('http://localhost:8080/api/item', payload);
        window.dispatchEvent(new Event('categoryUpdated'));
        alert('Item added!');
      }
      navigate('/items');
    } catch (err) {
      console.error("Error saving item:", err);

      const messages = err.response?.data;
      if (Array.isArray(messages)) {
        setErrors(messages);
      } else {
        setErrors(["An unexpected error occurred while saving."]);
      }
    }
  };

  const capitalize = (s) => s.charAt(0).toUpperCase() + s.slice(1);

  return (
    <div className="product-detail-container">
      <div className="product-detail-content">
        <h1>{isEditMode ? `Edit Product: ${formData.itemName}` : 'Add Item'}</h1>

        {errors.length > 0 && (
          <div className="error-box">
            <ul>
              {errors.map((msg, idx) => (
                <li key={idx}>{msg}</li>
              ))}
            </ul>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Item Name</label>
            <input type="text" name="itemName" value={formData.itemName} onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label>Category</label>
            <input type="text" name="category" value={formData.category} onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label>Description</label>
            <input type="text" name="itemDescription" value={formData.itemDescription} onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label>Price Per Unit ($)</label>
            <input
                type="number"
                name="pricePerUnit"
                step="0.01"
                value={formData.pricePerUnit !== '' ? Number(formData.pricePerUnit).toFixed(2) : ''}
                onChange={handleChange}
                required
            />
          </div>

          <div className="form-group">
            {isEditMode && <label>Current Count</label>}
            {!isEditMode && <label>Quantity</label>}
            <input type="number" name="currentCount" value={formData.currentCount} onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label>Item Limit</label>
            <input type="number" name="itemLimit" value={formData.itemLimit} onChange={handleChange} required />
          </div>

          <h3>Nutrition Facts</h3>
          <div className="form-group">
            <label>Calories</label>
            <input
                type="number"
                name="nutritionFacts.calories"
                value={formData.nutritionFactsParsed.calories || ''}
                onChange={handleChange}
            />
          </div>

          {['protein', 'carbs', 'fat',].map((field) => (
            <div className="form-group" key={field}>
                <label>{capitalize(field)} (g)</label>
                    <input
                    type="number"
                    name={`nutritionFacts.${field}`}
                    value={formData.nutritionFactsParsed[field] || ''}
                    onChange={handleChange}
                    />
            </div>
          ))}

          <div className="form-group">
            <label>Sodium (mg)</label>
              <input
                type="number"
                name="nutritionFacts.sodium"
                value={formData.nutritionFactsParsed.sodium || ''}
                onChange={handleChange}
              />
          </div>

          {isEditMode && (
            <div className="form-group">
              <label>
                Enabled
                <input
                  type="checkbox"
                  name="enabled"
                  checked={formData.enabled}
                  onChange={handleChange}
                />
              </label>
            </div>
          )}

          <div className="form-group">
            <label>Upload Image</label>
            <input type="file" accept="image/*" onChange={handleImageChange} />
          </div>

          {formData.picturePath && !imageFile && (
            <img src={formData.picturePath} alt="Preview" style={{ width: '150px', marginBottom: '10px' }} />
          )}

          <button type="submit" className="add-btn">
            {isEditMode ? 'Save Changes' : 'Add Item'}
          </button>
          <Link to="/dashboard" className="btn-cancel">Cancel</Link>
        </form>
      </div>
    </div>
  );
}

export default ItemForm;
