import React from 'react';
import './Pages.css';

function Pagination({ currentPage, totalPages, onPageChange }) {
  const handlePageChange = (page) => {
    if (page >= 1 && page <= totalPages) {
      onPageChange(page);
    }
  };

  return (
    <div className="pagination">
      <li>
        <button 
          onClick={() => handlePageChange(currentPage - 1)} 
          disabled={currentPage === 1}
        >
          Prev
        </button>
      </li>
      <span>{currentPage} of {totalPages}</span>
      <li>
        <button 
          onClick={() => handlePageChange(currentPage + 1)} 
          disabled={currentPage === totalPages}
        >
          Next
        </button>
      </li>
    </div>
  );
}

export default Pagination;



  