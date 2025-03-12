import "./Pages.css"; // Make sure to import the CSS file for styling

function Pagination({ currentPage, totalPages, onPageChange }) {
  return (
    <div className="pagination-container">
      <div className="pagination">
        <button 
          disabled={currentPage === 1} 
          onClick={() => onPageChange(currentPage - 1)}>
          Previous
        </button>
        <span> Page {currentPage} of {totalPages} </span>
        <button 
          disabled={currentPage === totalPages} 
          onClick={() => onPageChange(currentPage + 1)}>
          Next
        </button>
      </div>
    </div>
  );
}

export default Pagination;

  