import React, { createContext, useState } from 'react';

export const UserContext = createContext();

export const UserProvider = ({ children }) => {
  const [userId, setUserId] = useState('');
  const isAdmin = userId === 'WT36136';

  return (
    <UserContext.Provider value={{ userId, setUserId, isAdmin }}>
      {children}
    </UserContext.Provider>
  );
};
