import { Navigate } from 'react-router-dom';
import { useAuth } from '../Contexts/AuthContext';

const PrivateRoute = ({ children, allowedRoles }) => {
  const { user, selfCheckoutEnabled } = useAuth();

  if (!user) return <Navigate to="/" />;
  if (allowedRoles && !allowedRoles.includes(user.role)) return <Navigate to="/unauthorized" />;
  if (selfCheckoutEnabled) return <Navigate to="/unauthorized" />;

  return children;
};

export default PrivateRoute;
