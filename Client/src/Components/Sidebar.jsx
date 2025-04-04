import { Link } from 'react-router-dom';
import './Sidebar.css';

function Sidebar() {
  const handleButtonClick = (type) => {
    if (type === "ContactUs") {
      alert("Email: retrieversessentials@umbc.edu");
    } else if (type === "myUMBC") {
      window.location.href = "https://my.umbc.edu";
    } else if (type === "Facebook") {
      window.location.href = "https://www.facebook.com/RetrieverEssentials";
    } else if (type === "Instagram") {
      window.location.href = "https://www.instagram.com/umbcretrieveressentials/";
    }
  };

  return (
    <aside className="sidebar">
      <div className="types-of-food">
        <h3>Types of Food</h3>
        <ul>
          {["Pantry", "Produce", "Asian", "Meat", "Frozen", "American", "Mexican", "Indian", "Bread"]
            .map(category => (
              <li key={category}>
                <Link to={category === "Asian" ? "/Asian" : `/${category.toLowerCase()}`}>
                  <button>{category}</button>
                </Link>
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
        <div className="contact-buttons">
          <button className="contact-button" onClick={() => handleButtonClick("ContactUs")}>Contact Us</button>
          <button className="contact-button" onClick={() => handleButtonClick("myUMBC")}>myUMBC</button>
          <button className="contact-button" onClick={() => handleButtonClick("Facebook")}>Facebook</button>
          <button className="contact-button" onClick={() => handleButtonClick("Instagram")}>Instagram</button>
        </div>
      </div>
    </aside>
  );
}

export default Sidebar;


