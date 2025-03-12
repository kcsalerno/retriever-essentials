import React, { useState } from 'react';
import ProductGrid from './Grid';
import Pagination from './Pages';
import './Grid.css';


const products = [
  { id: 1, name: "Whole Wheat Bread", image: "/images/wheat.png", quantity: 10 },
  { id: 2, name: "Bagels", image: "/images/bag.png", quantity: 5 },
  { id: 3, name: "Croissants", image: "/images/cro.png", quantity: 8 }
];

function Bread() {
  const [currentPage, setCurrentPage] = useState(1);
  const productsPerPage = 3; 

  
  const indexOfLastProduct = currentPage * productsPerPage;
  const indexOfFirstProduct = indexOfLastProduct - productsPerPage;
  const currentProducts = products.slice(indexOfFirstProduct, indexOfLastProduct);

  const totalPages = Math.ceil(products.length / productsPerPage);

  return (
    <div>
      <ProductGrid products={currentProducts} /> {/* Pass the current products */}
      <Pagination 
        currentPage={currentPage} 
        totalPages={totalPages} 
        onPageChange={setCurrentPage} 
      />
    </div>
  );
}

export default Bread;

