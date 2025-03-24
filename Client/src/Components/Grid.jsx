import { Link } from 'react-router-dom';

const ProductGrid = ({ products, addToCart }) => {
  return (
    <div className="product-grid">
      {products.map(product => (
        <div className="product-card" key={product.id}>
          <Link to={`/product/${product.id}`} className="product-link">
            <img src={product.image} alt={product.name} />
          </Link>
          <h3>{product.name}</h3>
          <p>Quantity: {product.quantity}</p>
          <button onClick={() => addToCart(product)}>Add to Cart</button>
        </div>
      ))}
    </div>
  );
};

export default ProductGrid;





