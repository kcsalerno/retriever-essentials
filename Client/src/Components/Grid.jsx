import './Grid.css';

function ProductGrid({ products }) {
  if (!products || !Array.isArray(products)) {
    return <p>No products available.</p>; 
  }

  return (
    <div className="product-grid">
      {products.map(product => (
        <div className="product-card" key={product.id}>
          <img src={product.image} alt={product.name} />
          <h3>{product.name}</h3>
          <p>Quantity: {product.quantity}</p>
        </div>
      ))}
    </div>
  );
}

export default ProductGrid;




