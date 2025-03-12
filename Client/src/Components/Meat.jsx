import React, { useState } from 'react';
import ProductGrid from './Grid';
import Pagination from './Pages';
import './Grid.css';


const products = [
  { id: 1, name: "Chicken Breast", image: "/images/chicken.png", quantity: 15 },
  { id: 2, name: "Ground Beef", image: "/images/beef.png", quantity: 10 },
  { id: 3, name: "Bacon", image: "/images/bacon.png", quantity: 8 }
];

function Meat() {
  const [currentPage, setCurrentPage] = useState(1);
  const productsPerPage = 3; 

  
  const indexOfLastProduct = currentPage * productsPerPage;
  const indexOfFirstProduct = indexOfLastProduct - productsPerPage;
  const currentProducts = products.slice(indexOfFirstProduct, indexOfLastProduct);

  const totalPages = Math.ceil(products.length / productsPerPage);

  return (
    <div>
      <ProductGrid products={currentProducts} />
      <Pagination 
        currentPage={currentPage} 
        totalPages={totalPages} 
        onPageChange={setCurrentPage} 
      />
    </div>
  );
}

export default Meat;
