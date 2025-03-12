import React, { useState } from 'react';
import ProductGrid from './Grid';
import Pagination from './Pages';
import './Grid.css';


const products = [
  { id: 1, name: "Frozen Pizza", image: "/images/pizza.png", quantity: 12 },
  { id: 2, name: "Ice Cream", image: "/images/cream.png", quantity: 20 },
  { id: 3, name: "Frozen Broccoli", image: "/images/bro.png", quantity: 15 }
];

function Frozen() {
  const [currentPage, setCurrentPage] = useState(1);
  const productsPerPage = 3;

  // Paginate the products
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

export default Frozen;

