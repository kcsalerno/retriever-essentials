import React, { useState } from 'react';
import ProductGrid from './Grid';
import Pagination from './Pages';
import './Grid.css';


const products = [
  { id: 13, name: "Tortillas", image: "/images/tort.png", quantity: 15 },
  { id: 14, name: "Salsa", image: "/images/sal.png", quantity: 12 },
  { id: 15, name: "Black Beans", image: "/images/bean.png", quantity: 20 }
];

function Mexican({ addToCart }) {
  const [currentPage, setCurrentPage] = useState(1);
  const productsPerPage = 3;

  
  const indexOfLastProduct = currentPage * productsPerPage;
  const indexOfFirstProduct = indexOfLastProduct - productsPerPage;
  const currentProducts = products.slice(indexOfFirstProduct, indexOfLastProduct);

  const totalPages = Math.ceil(products.length / productsPerPage);

  return (
    <div>
      <ProductGrid products={currentProducts}  addToCart={addToCart}/> 
      <Pagination 
        currentPage={currentPage} 
        totalPages={totalPages} 
        onPageChange={setCurrentPage} 
      />
    </div>
  );
}

export default Mexican;

