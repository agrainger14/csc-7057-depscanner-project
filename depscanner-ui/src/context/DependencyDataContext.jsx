import React, { createContext } from 'react'
import LoadingSpinner from "../components/LoadingSpinner";
import { useParams } from 'react-router-dom';
import { axiosDefault } from '../utils/axios';

const DependencyDataContext = createContext();

export const DependencyDataProvider = ({ children }) => {
  const params = useParams();
  const { name, system, version } = params;
  const [dependencyData, setDependencyData] = React.useState(null);
  const [isLoading, setIsLoading] = React.useState(true);

  React.useEffect(() => {
    setIsLoading(true);

    const controller = new AbortController();

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
          console.log(res.data);
      } catch (err) {
        console.log(err);
      } finally {
        setIsLoading(false);
      }
    }

    getDependenciesData();
    
    return () => {
      controller.abort();
    }   
  }, [])

  return (
    <DependencyDataContext.Provider value={{ dependencyData, name, system, version }}>
      {isLoading ? <LoadingSpinner /> : children}
    </DependencyDataContext.Provider>
  );
}

export default DependencyDataContext