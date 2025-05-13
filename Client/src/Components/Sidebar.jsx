import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import axios from 'axios';
import './Sidebar.css';

function Sidebar() {
  const [categories, setCategories] = useState([]);
  const location = useLocation();
  // const currentCategory = decodeURIComponent(location.pathname.replace('/category/', ''));

  useEffect(() => {
    const fetchCategories = () => {
      axios.get('http://localhost:8080/api/item')
        .then(res => {
          const enabledItems = res.data.filter(item => item.enabled).filter(item => item.currentCount > 0);
          const uniqueCategories = [...new Set(enabledItems.map(item => item.category.trim()))];
          setCategories(uniqueCategories);
        })
        .catch(err => console.error("Error fetching categories:", err));
    };
  
    fetchCategories();
  
    const handleCategoryUpdate = () => fetchCategories();
  
    window.addEventListener('categoryUpdated', handleCategoryUpdate);
    return () => window.removeEventListener('categoryUpdated', handleCategoryUpdate);
  }, []);
  
  const isActive = (category) =>
    decodeURIComponent(location.pathname.toLowerCase()) === `/category/${category.toLowerCase()}`;  

  const handleButtonClick = (type) => {
    if (type === "ContactUs") {
      alert("Email: retrieversessentials@umbc.edu");
    } else if (type === "myUMBC") {
      window.location.href = "https://my.umbc.edu";
    } else if (type === "Facebook") {
      window.location.href = "https://www.facebook.com/RetrieverEssentials";
    } else if (type === "Instagram") {
      window.location.href = "https://www.instagram.com/umbcretrieveressentials/";
    }
  };

  return (
    <aside className="sidebar">
      <div className="types-of-food">
        <h3>Types of Food</h3>
        <ul>
          {categories.map(category => (
            <li key={category}>
              <Link to={`/category/${encodeURIComponent(category)}`}>
                <button className={`category-button ${isActive(category) ? 'active-category' : ''}`}>
                  {category}
                </button>
              </Link>
            </li>
          ))}
        </ul>
      </div>
      <div className="hours">
        <h3>Location</h3>
        <p>The Commons 1A10</p>
        <h3>Hours of Operation</h3>
        <p>Mon: 12PM - 5 PM</p>
        <p>Tues: 11AM - 5 PM</p>
        <p>Weds: 1PM - 7PM</p>
        <p>Thurs: Closed</p>
        <p>Fri: 11AM - 7PM</p>
        <p>Sat: Closed</p>
        <p>Sun: Closed</p>
        <p>Contact</p>
        <div className="contact-buttons">
          <button className="contact-button" onClick={() => handleButtonClick("ContactUs")}>Contact Us</button>
          <button className="contact-button" onClick={() => handleButtonClick("myUMBC")}>myUMBC</button>
          <button className="contact-button" onClick={() => handleButtonClick("Facebook")}>Facebook</button>
          <button className="contact-button" onClick={() => handleButtonClick("Instagram")}>Instagram</button>
        </div>
      </div>
    </aside>
  );
}

export default Sidebar;


