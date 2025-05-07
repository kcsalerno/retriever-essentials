import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './AdminTable.css';

function VendorList() {
  const [vendors, setVendors] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    axios.get('http://localhost:8080/api/vendor')
      .then(res => setVendors(res.data))
      .catch(err => {
        console.error("Failed to fetch vendors", err);
        navigate('/dashboard'); // Redirect to dashboard on error
      });
  }, [navigate]);

  return (
    <div className="admin-table-container">
      <div className="button-head">
        <h2>All Vendors</h2>
        <Link className="btn add" to="/add-vendor">
          ➕ Add Vendor
        </Link>
      </div>

      <table className="admin-table">
        <thead>
          <tr>
            <th>Vendor Name</th>
            <th>Phone</th>
            <th>Email</th>
            <th>Enabled</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {vendors.map(vendor => (
            <tr key={vendor.vendorId}>
              <td>{vendor.vendorName}</td>
              <td>{vendor.phoneNumber}</td>
              <td>{vendor.contactEmail}</td>
              <td style={{ textAlign: 'center' }}>{vendor.enabled ? '✅' : '❌'}</td>
              <td style={{ display: 'flex', gap: '10px', justifyContent: 'center' }}>
                <Link className="btn add" to={`/edit-vendor/${vendor.vendorId}`}>📝Edit</Link>
                {/* <Link className="btn delete" to={`/delete-vendor/${vendor.vendorId}`}>Disable</Link> */}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default VendorList;
