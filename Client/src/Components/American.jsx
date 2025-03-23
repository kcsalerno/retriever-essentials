import React, { useState } from 'react';
import ProductGrid from './Grid';
import Pagination from './Pages';
import './Grid.css';


const products = [
  { id: 25, name: "Mac & Cheese", image: "/images/mac.png", quantity: 10 },
  { id: 26, name: "Burgers", image: "/images/bur.png", quantity: 5 },
  { id: 27, name: "Hot Dogs", image: "/images/hot.png", quantity: 8 }
];

function American({ addToCart }) {
  const [currentPage, setCurrentPage] = useState(1);
  const productsPerPage = 3; 

  // Paginate the products
  const indexOfLastProduct = currentPage * productsPerPage;
  const indexOfFirstProduct = indexOfLastProduct - productsPerPage;
  const currentProducts = products.slice(indexOfFirstProduct, indexOfLastProduct);

  const totalPages = Math.ceil(products.length / productsPerPage);

  return (
    <div>
      <ProductGrid products={currentProducts}  addToCart={addToCart} /> 
      <Pagination 
        currentPage={currentPage} 
        totalPages={totalPages} 
        onPageChange={setCurrentPage} 
      />
    </div>
  );
}

export default American;

