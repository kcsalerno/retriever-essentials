import './Grid.css';

const products = [
  { id: 1, name: "Rice", image: "/images/Rice.png", quantity: 10 },
  { id: 2, name: "Pocky Sticks", image: "/images/Pocky.png", quantity: 5 },
  { id: 3, name: "Ramen 12 Pack", image: "/images/Ramen.png", quantity: 8 }
];

function ProductGrid() {
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



