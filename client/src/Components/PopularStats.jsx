import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './PopularStats.css';

function PopularStats() {
  const [popularItems, setPopularItems] = useState([]);
  const [popularCategories, setPopularCategories] = useState([]);

  useEffect(() => {
    axios.get('http://localhost:8080/api/checkout-item/popular-items')
      .then(res => setPopularItems(res.data))
      .catch(err => console.error("Error fetching popular items:", err));

    axios.get('http://localhost:8080/api/checkout-item/popular-categories')
      .then(res => setPopularCategories(res.data))
      .catch(err => console.error("Error fetching popular categories:", err));
  }, []);

  return (
    <div className="popular-stats-container">
      <h2>📦 Most Popular Items</h2>
      <ul className="stats-list">
        {popularItems.map((item, index) => (
          <li key={index}>
            <strong>{item.item_name}</strong> — {item.total_checkouts} checkouts
          </li>
        ))}
      </ul>

      <h2>📊 Most Popular Categories</h2>
      <ul className="stats-list">
        {popularCategories.map((cat, index) => (
          <li key={index}>
            <strong>{cat.category}</strong> — {cat.total_checkouts} checkouts
          </li>
        ))}
      </ul>
    </div>
  );
}

export default PopularStats;
