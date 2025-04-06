import React, { useState, useEffect } from 'react';
import axios from 'axios';
import ProductGrid from './Grid';
import Pagination from './Pages';
import './Grid.css';

function CategoryPage({ addToCart, category }) {
  const [products, setProducts] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const productsPerPage = 3;

  useEffect(() => {
    axios.get(`http://localhost:8080/api/item/category/${category}`)  // Dynamic category
      .then((response) => {
        setProducts(response.data);
      })
      .catch((error) => {
        console.error(`Error fetching ${category} products:`, error);
      });
  }, [category]);  // Re-run the effect when the category changes

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

export default CategoryPage;


