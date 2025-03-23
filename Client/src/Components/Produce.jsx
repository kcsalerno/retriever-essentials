import React, { useState } from 'react';
import ProductGrid from './Grid';
import Pagination from './Pages';
import './Grid.css';


const products = [
  { id: 16, name: "Apples", image: "/images/apple.png", quantity: 30 },
  { id: 17, name: "Carrots", image: "/images/carrot.png", quantity: 25 },
  { id: 18, name: "Lettuce", image: "/images/lettuce.png", quantity: 10 }
];

function Produce({ addToCart }) {
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

export default Produce;
