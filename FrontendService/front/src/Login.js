import { useNavigate } from 'react-router-dom';

const Login = () => {
  const navigate = useNavigate();
  
  return (
    <div className="login-page">
      <h1>Login Page</h1>
      <button onClick={() => navigate('/')}>Back to Home</button>
    </div>
  );
};

export default Login;