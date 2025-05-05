// src/Contexts/AuthContext.js
import { createContext, useContext, useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';
import * as authService from '../Services/auth';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [selfCheckoutEnabled, setSelfCheckoutEnabled] = useState(() =>
    JSON.parse(localStorage.getItem('selfCheckoutEnabled')) || false
  );

  // Load token from localStorage on first load
  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    if (storedToken) {
      try {
        const decoded = jwtDecode(storedToken);
        if (decoded.exp * 1000 > Date.now()) {
          setUser({
            appUserId: decoded.appUserId,
            email: decoded.sub,
            role: decoded.role
          });
          setToken(storedToken);
        } else {
          localStorage.removeItem('token');
        }
      } catch (err) {
        console.error('Invalid token:', err);
        localStorage.removeItem('token');
      }
    }
  }, []);

  // Persist self-checkout flag
  useEffect(() => {
    localStorage.setItem('selfCheckoutEnabled', JSON.stringify(selfCheckoutEnabled));
  }, [selfCheckoutEnabled]);

  const login = async (credentials) => {
    const result = await authService.login(credentials);
    if (result.ok) {
      localStorage.setItem('token', result.token);
      const decoded = jwtDecode(result.token);
      setUser({
        appUserId: decoded.appUserId,
        email: decoded.sub,
        role: decoded.role
      });
      setToken(result.token);
    }
    return result;
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
    setToken(null);
    setSelfCheckoutEnabled(false); // Reset when logging out
  };

  const refreshToken = async () => {
    const result = await authService.refresh();
    if (result.ok) {
      localStorage.setItem('token', result.token);
      const decoded = jwtDecode(result.token);
      setUser({
        appUserId: decoded.appUserId,
        email: decoded.sub,
        role: decoded.role
      });
      setToken(result.token);
    } else {
      logout();
    }
    return result;
  };

  const toggleSelfCheckout = () => {
    setSelfCheckoutEnabled(prev => !prev);
  };

  return (
    <AuthContext.Provider value={{
      user,
      token,
      login,
      logout,
      refreshToken,
      selfCheckoutEnabled,
      enableSelfCheckout: toggleSelfCheckout
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
