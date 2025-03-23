import React, { useState } from 'react';
import { Link } from 'react-router-dom';  // Make sure to import Link
import ProductGrid from './Grid';
import Pagination from './Pages';
import './Grid.css';

const products = [
  { id: 1, name: "Whole Wheat Bread", image: "/images/wheat.png", quantity: 10, nutrition: { fat: 2, protein: 4, carbs: 21, sodium: 170, calories: 110 } },
  { id: 2, name: "Bagels", image: "/images/bag.png", quantity: 5, nutrition: { fat: 1.5, protein: 10, carbs: 53, sodium: 450, calories: 270 } },
  { id: 3, name: "Croissants", image: "/images/cro.png", quantity: 8, nutrition: { fat: 17, protein: 4, carbs: 27, sodium: 105, calories: 270 } }
];

function Bread({ addToCart }) {
  const [currentPage, setCurrentPage] = useState(1);
  const productsPerPage = 3;

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



