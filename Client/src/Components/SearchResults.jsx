import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import ProductGrid from './Grid';

const SearchResults = ({ addToCart }) => {
  const { searchTerm } = useParams();
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Fetch data from the correct API endpoint
    axios.get(`http://localhost:8080/api/item/name/${encodeURIComponent(searchTerm)}`)
      .then((response) => {
        setResults([response.data]); // Since findByName returns a single item
        setLoading(false);
      })
      .catch((error) => {
        console.error('Error fetching search results:', error);
        setLoading(false);
      });
  }, [searchTerm]);

  if (loading) return <div>Loading...</div>;

  return (
    <div>
      <h2>Search Results for "{searchTerm}"</h2>
      {results.length > 0 ? (
        <ProductGrid products={results} addToCart={addToCart} />
      ) : (
        <p>No items found.</p>
      )}
    </div>
  );
};

export default SearchResults;




