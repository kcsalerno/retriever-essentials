import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './AdminTable.css';

function UserList() {
  const [users, setUsers] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    axios.get('http://localhost:8080/api/user') // adjust endpoint if different
      .then(res => setUsers(res.data))
      .catch(err => {
        console.error("Failed to fetch users", err);
        navigate('/dashboard');
      });
  }, [navigate]);

  return (
    <div className="admin-table-container">
      <div className="button-head">
        <h2>All Users</h2>
        <Link className="btn add" to="/add-user">
          ➕ Add User
        </Link>
      </div>

      <table className="admin-table">
        <thead>
          <tr>
            <th>Username</th>
            <th>Role(s)</th>
            <th>Enabled</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {users.map(user => (
            <tr key={user.appUserId}>
              <td>{user.username}</td>
              <td>{user.authorities?.map(auth => auth.authority.replace('ROLE_', '')).join(', ')}</td>
              <td style={{ textAlign: 'center' }}>{user.enabled ? '✅' : '❌'}</td>
              <td>
                <Link className="btn add" to={`/edit-user/${user.appUserId}`}>📝Edit</Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default UserList;
