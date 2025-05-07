import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './AdminTable.css';

function CheckoutList() {
  const [checkouts, setCheckouts] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    axios.get('http://localhost:8080/api/checkout-order')
      .then(res => setCheckouts(res.data))
      .catch(err => {
        console.error("Failed to fetch checkouts", err);
        navigate('/dashboard');
      });
  }, [navigate]);

  const handleDelete = async (checkOutOrderId) => {
    if (!window.confirm("Are you sure you want to delete this checkout order?")) return;
    try {
      await axios.delete(`http://localhost:8080/api/checkout-order/${checkOutOrderId}`);
      alert('Checkout order deleted!');
      navigate('/dashboard');
    } catch (err) {
      console.error("Error deleting checkout order:", err);
      alert('Failed to delete checkout order.');
    }
  };

return (
    <div className="admin-table-container">
        <div className="button-head">
            <h2>All Checkout Orders</h2>
        </div>

        <table className="admin-table">
            <thead>
                <tr>
                    <th>Order ID</th>
                    <th>Student</th>
                    <th>Authority</th>
                    <th>Self Checkout</th>
                    <th>Date</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                {checkouts.map(order => {
                    const formattedDate = new Date(order.checkoutDate).toLocaleString('en-US', {
                        year: '2-digit',
                        month: 'numeric',
                        day: 'numeric',
                        hour: 'numeric',
                        minute: '2-digit',
                        hour12: true,
                    });

                    return (
                        <tr key={order.checkoutOrderId}>
                            <td>{order.checkoutOrderId}</td>
                            <td>{order.studentId}</td>
                            <td>{order.authority.username ?? '—'}</td>
                            <td style={{ textAlign: 'center' }}>{order.selfCheckout ? '✅' : '❌'}</td>
                            <td>{formattedDate}</td>
                            <td style={{ display: 'flex', gap: '10px', justifyContent: 'center', textAlign: 'center' }}>
                                <Link className="btn add" to={`/edit-checkout/${order.checkoutOrderId}`}>📝Edit</Link>
                                <button className="btn delete"
                                    onClick={() => handleDelete(order.checkoutOrderId)}>
                                    🗑️ Delete
                                </button>
                            </td>
                        </tr>
                    );
                })}
            </tbody>
        </table>
    </div>
);
}

export default CheckoutList;
