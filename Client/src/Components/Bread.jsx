import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';  // Make sure to import Link
import axios from 'axios';  // Import axios to make API requests
import ProductGrid from './Grid';
import Pagination from './Pages';
import './Grid.css';

function Bread({ addToCart }) {
  const [products, setProducts] = useState([]);  // State to hold products fetched from backend
  const [currentPage, setCurrentPage] = useState(1);
  const productsPerPage = 3;

  useEffect(() => {
    // Fetch Bread products from the backend API
    axios.get('http://localhost:8080/api/item/category/Bread') // Adjust based on your backend API path
      .then((response) => {
        setProducts(response.data);  // Set the fetched products to state
      })
      .catch((error) => {
        console.error('Error fetching Bread products:', error);
      });
  }, []);

  const indexOfLastProduct = currentPage * productsPerPage;
  const indexOfFirstProduct = indexOfLastProduct - productsPerPage;
  const currentProducts = products.slice(indexOfFirstProduct, indexOfLastProduct);

  const totalPages = Math.ceil(products.length / productsPerPage);

  return (
    <div>
      <ProductGrid products={currentProducts} addToCart={addToCart} />
      <Pagination currentPage={currentPage} totalPages={totalPages} onPageChange={setCurrentPage} />
    </div>
  );
}

export default Bread;




