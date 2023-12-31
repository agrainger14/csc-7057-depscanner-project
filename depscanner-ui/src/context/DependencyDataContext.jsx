import React, { createContext, useRef } from 'react'
import LoadingSpinner from "../components/Loading/LoadingSpinner";
import { useParams } from 'react-router-dom';
import { axiosDefault } from '../utils/axios';
import { useNavigate } from 'react-router-dom';

const DependencyDataContext = createContext();

export const DependencyDataProvider = ({ children }) => {
  const params = useParams();
  const effectRan = useRef(false);
  const { name, system, version } = params;
  const [dependencyData, setDependencyData] = React.useState(null);
  const [isLoading, setIsLoading] = React.useState(true);
  const navigate = useNavigate();

  React.useEffect(() => {
    setIsLoading(true);

    const controller = new AbortController();

    if (effectRan.current === true) {
      const getDependenciesData = async () => {
        try {
          const res = await axiosDefault.get(`vuln/dependencies`, {
            params: {
              name: `${name}`,
              system: `${system}`,
              version: `${version}`,
            },
            signal: controller.signal
          });
          setDependencyData(res.data);
        } catch (err) {
          console.log(err);
          navigate('/404');
        } finally {
          setIsLoading(false);
        }
      }
  
      getDependenciesData(); 
    }

    return () => {
      controller.abort();
      effectRan.current = true;
    }   
  }, [])

  return (
    <DependencyDataContext.Provider value={{ dependencyData, name, system, version }}>
      {isLoading ? <LoadingSpinner /> : children}
    </DependencyDataContext.Provider>
  );
}

export default DependencyDataContext