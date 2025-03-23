import React, { useState } from 'react';
import ProductGrid from './Grid';
import Pagination from './Pages';
import './Grid.css';


const products = [
  { id: 22, name: "Rice", image: "/images/Rice.png", quantity: 10 },
  { id: 23, name: "Pocky Sticks", image: "/images/Pocky.png", quantity: 5 },
  { id: 24, name: "Ramen 12 Pack", image: "/images/Ramen.png", quantity: 8 },
  // Add more products as needed
];

function Asian({ addToCart }) {
    const [currentPage, setCurrentPage] = useState(1);
    const productsPerPage = 3; 
  
    
    const indexOfLastProduct = currentPage * productsPerPage;
    const indexOfFirstProduct = indexOfLastProduct - productsPerPage;
    const currentProducts = products.slice(indexOfFirstProduct, indexOfLastProduct);
  
    const totalPages = Math.ceil(products.length / productsPerPage);

  return (
    <div>
      <ProductGrid products={currentProducts}  addToCart={addToCart}/> {/* Pass the current products */}
      <Pagination 
        currentPage={currentPage} 
        totalPages={totalPages} 
        onPageChange={setCurrentPage} 
      />
    </div>
  );
}

export default Asian;
