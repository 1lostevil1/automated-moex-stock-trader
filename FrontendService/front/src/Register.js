import { useNavigate } from 'react-router-dom';

const Register = () => {
  const navigate = useNavigate();
  
  return (
    <div className="register-page">
      <h1>Register Page</h1>
      <button onClick={() => navigate('/')}>Back to Home</button>
    </div>
  );
};

export default Register;