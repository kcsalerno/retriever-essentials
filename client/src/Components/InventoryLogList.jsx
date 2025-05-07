import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './AdminTable.css';

function InventoryLogList() {
  const [logs, setLogs] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    axios.get('http://localhost:8080/api/inventory-log')
      .then(res => setLogs(res.data))
      .catch(err => {
        console.error("Failed to fetch inventory logs", err);
        navigate('/dashboard');
      });
  }, [navigate]);

  return (
    <div className="admin-table-container">
      <div className="button-head">
        <h2>Inventory Logs</h2>
        <Link className="btn add" to="/add-inventory-log">
          ➕ Add Log
        </Link>
      </div>

      <table className="admin-table">
        <thead>
          <tr>
            <th>Log ID</th>
            <th>Item</th>
            <th>Authority</th>
            <th>Change</th>
            <th>Reason</th>
            <th>Date</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {logs.map(log => {
            const formattedDate = new Date(log.timeStamp).toLocaleString('en-US', {
              year: '2-digit',
              month: 'numeric',
              day: 'numeric',
              hour: 'numeric',
              minute: '2-digit',
              hour12: true,
            });

            return (
              <tr key={log.logId}>
                <td>{log.logId}</td>
                <td>{log.item?.itemName ?? '—'}</td>
                <td>{log.authority?.username ?? '—'}</td>
                <td style={{ color: log.quantityChange < 0 ? 'red' : 'green' }}>
                  {log.quantityChange > 0 ? `+${log.quantityChange}` : log.quantityChange}
                </td>
                <td>{log.reason}</td>
                <td>{formattedDate}</td>
                <td style={{ display: 'flex', gap: '10px', justifyContent: 'center', textAlign: 'center' }}>
                  <Link className="btn add" to={`/edit-inventory-log/${log.logId}`}>📝Edit</Link>
                  <Link className="btn delete" to={`/delete-inventory-log/${log.logId}`}>🗑️Delete</Link>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

export default InventoryLogList;
