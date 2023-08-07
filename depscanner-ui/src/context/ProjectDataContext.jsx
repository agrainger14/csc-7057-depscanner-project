import React, { createContext } from "react";
import LoadingSpinner from "../components/LoadingSpinner";
import { axiosDefault } from "../utils/axios";
import { useOidcIdToken } from "@axa-fr/react-oidc";

const ProjectDataContext = createContext();

export const ProjectDataProvider = ({ children }) => {
    const [projectData, setProjectData] = React.useState(null);
    const [isLoading, setIsLoading] = React.useState(true);
    const { idToken } = useOidcIdToken();

    React.useEffect(() => {
        setIsLoading(true);
        const controller = new AbortController();

        const headers = {
            'Authorization': 'Bearer ' + idToken
        };

        const fetchProjects = async () => {
          try {
            const res = await axiosDefault.get('/project/user', { headers }, {
              signal: controller.signal
            });
              setProjectData(res.data);
            } catch (err) {
              console.log(err)
            } finally {
              setIsLoading(false);
          }
        }
        
        fetchProjects();
        
        return () => {
          controller.abort();
        }
    }, []);

    const handleDelete = async (id) => {
      try {
        await axiosDefault.delete(`/project/id/${id}`, {
          headers: {
          'Authorization': 'Bearer ' + idToken
          }
        }); 
        const projects = projectData.filter(project => project.id !== id);
        setProjectData(projects);
      } catch (err) {
          console.log(err)
      }
    }

    return (
      <ProjectDataContext.Provider value={{ projectData, handleDelete, setProjectData }}>
        {isLoading ? <LoadingSpinner /> : children}
      </ProjectDataContext.Provider>
    );
}

export default ProjectDataContext;