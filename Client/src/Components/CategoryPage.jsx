import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';
import Pagination from './Pages';
import './Grid.css';

function CategoryPage({ addToCart }) {
  const { category } = useParams();
  const [products, setProducts] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const productsPerPage = 3;

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

  useEffect(() => {
    axios.get(`http://localhost:8080/api/item/category/${category}`)
      .then((response) => {
        setProducts(response.data);
      })
      .catch((error) => {
        console.error(`Error fetching ${category} products:`, error);
      });
  }, [category]);

  const indexOfLastProduct = currentPage * productsPerPage;
  const indexOfFirstProduct = indexOfLastProduct - productsPerPage;
  const currentProducts = products.slice(indexOfFirstProduct, indexOfLastProduct);
  const totalPages = Math.ceil(products.length / productsPerPage);

  return (
    <div className="category-page">
      <div className="product-grid">
        {currentProducts.map(product => (
          <div key={product.id} className="product-card">
            <Link to={`/product/${product.itemName}`} style={{ textDecoration: 'none', color: 'inherit' }}>
              <img src={product.picturePath} alt={product.itemName} className="product-image" />
              <h3>{product.itemName}</h3>
              <p className={`product-quantity ${getQuantityClass(product.currentCount)}`}>
                Quantity: {product.currentCount}
              </p>
              <p>${product.pricePerUnit.toFixed(2)}</p>
            </Link>
            <button onClick={() => addToCart(product)} className="add-to-cart-btn">Add to Cart</button>
          </div>
        ))}
      </div>
      <div className="pagination-container">
        <Pagination currentPage={currentPage} totalPages={totalPages} onPageChange={setCurrentPage} />
      </div>
    </div>
  );
}

export default CategoryPage;



