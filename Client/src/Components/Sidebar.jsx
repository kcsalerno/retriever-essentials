import { Link } from 'react-router-dom';
import './Sidebar.css';

function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="types-of-food">
        <h3>Types of Food</h3>
        <ul>
          {["Pantry", "Produce", "Asian", "Meat", "Frozen", "American", "Mexican", "Indian", "Bread"]
            .map(category => (
              <li key={category}>
                {category === "Asian" ? (
                  <Link to="/product-grid">
                    <button>{category}</button>
                  </Link>
                ) : (
                  <button>{category}</button>
                )}
              </li>
          ))}
        </ul>
      </div>
      <div className="hours">
        <p>Location</p>
        <p>The Commons 1A10</p>
        <h3>Hours of Operation</h3>
        <p>Mon: 12PM - 5 PM</p>
        <p>Tues: 11AM - 5 PM</p>
        <p>Weds: 1PM - 7PM</p>
        <p>Thurs: Closed</p>
        <p>Fri: 11AM - 7PM</p>
        <p>Sat: Closed</p>
        <p>Sun: Closed</p>
        <p>Contact</p>
        <p><button>Contact US</button></p>
        <p><button>myUMBC</button></p>
        <p><button>Facebook</button></p>
        <p><button>Instagram</button></p>
      </div>
    </aside>
  );
}

export default Sidebar;



