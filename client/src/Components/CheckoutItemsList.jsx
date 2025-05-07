import React from 'react';
import { Link } from 'react-router-dom';
import './AdminTable.css';

function CheckoutItemsList({ checkoutItems }) {
  return (
    <div className="admin-table-container">
      <h3>Checkout Items</h3>
      <table className="admin-table">
        <thead>
          <tr>
            <th>Item Name</th>
            <th>Quantity</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {checkoutItems.map(item => (
            <tr key={item.checkoutItemId}>
              <td>{item.item?.itemName ?? '—'}</td>
              <td>{item.quantity}</td>
              <td>
                <Link className="btn add" to={`/edit-checkout-item/${item.checkoutItemId}`}>
                  📝Edit
                </Link>
                <Link className="btn delete" to={`/delete-checkout-item/${item.checkoutItemId}`}>
                  🗑️Delete
                </Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default CheckoutItemsList;
