import React, { createContext } from "react";
import { useNavigate } from "react-router-dom";
import LoadingSpinner from "../components/Loading/LoadingSpinner";
import { axiosDefault } from "../utils/axios";
import { useOidcIdToken } from "@axa-fr/react-oidc";

const ProjectDataContext = createContext();

export const ProjectDataProvider = ({ children }) => {
  const navigate = useNavigate();
  const [projectData, setProjectData] = React.useState(null);
  const [isLoading, setIsLoading] = React.useState(true);
  const [successSnackbarOpen, setSuccessSnackbarOpen] = React.useState(false);
  const [errorSnackbarOpen, setErrorSnackbarOpen] = React.useState(false);
  const [successDeleteProject, setSuccessDeleteProject] = React.useState(false);
  const [currentPage, setCurrentPage] = React.useState(1);
  const controller = new AbortController();
  const { idToken } = useOidcIdToken();

  const projectsPerPage = 5;
  const startIndex = (currentPage - 1) * projectsPerPage;
  const endIndex = startIndex + projectsPerPage;

  const headers = {
    'Authorization': 'Bearer ' + idToken
  };

  React.useEffect(() => {
    setIsLoading(true);
    
    const fetchProjects = async () => {
      try {
        const res = await axiosDefault.get('/project/user', { headers }, {
          signal: controller.signal
        });
        setProjectData(res.data);
      } catch (err) {
        console.log(err);
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
        headers,
        signal: controller.signal
      }); 
      const projects = projectData.filter(project => project.id !== id);
      setProjectData(projects);
      setSuccessDeleteProject(true);
      handlePageOnProjectDelete(projects);
    } catch (err) {
      setErrorSnackbarOpen(true);
      console.log(err)
    }

    return () => {
      controller.abort();
    }
  }

  const handlePageOnProjectDelete = (projects) => {
    if (startIndex >= projects.length && currentPage > 1) {
      setCurrentPage(currentPage - 1);
    }
  }

  const patchScanSchedule = async (id, updatedWeeklyScanned, updatedDailyScanned) => {
    const requestBody = {
        weeklyScanned: updatedWeeklyScanned,
        dailyScanned: updatedDailyScanned,
    }

    try {
      const res = await axiosDefault.patch(`/project/id/${id}`, requestBody, {
        headers,
        signal: controller.signal
      });
      const updatedProjectData = projectData.map(project =>
        project.id === res.data.id ? 
        {
          ...project,
          weeklyScanned: res.data.weeklyScanned,
          dailyScanned: res.data.dailyScanned,
        }
        : project
      );
        setProjectData(updatedProjectData);
        setSuccessSnackbarOpen(true);
      } catch (err) {
        setErrorSnackbarOpen(true);
        console.log(err)
    } 

    return () => {
      controller.abort();
    }
  }

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  }

  return (
    <ProjectDataContext.Provider value={{ projectData, handleDelete, setProjectData, patchScanSchedule, successSnackbarOpen, setSuccessSnackbarOpen, errorSnackbarOpen, setErrorSnackbarOpen, setSuccessDeleteProject, currentPage, handlePageChange, projectsPerPage, startIndex, endIndex }}>
      {isLoading ? <LoadingSpinner /> : children}
    </ProjectDataContext.Provider>
  );
}

export default ProjectDataContext;
