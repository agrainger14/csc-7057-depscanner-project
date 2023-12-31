import React from 'react'
import { useNavigate } from 'react-router-dom';

function DashboardRedirect() {
    const navigate = useNavigate();
  
    React.useEffect(() => {
      navigate('/dashboard/projects')
    }, [navigate]);
}

export default DashboardRedirect