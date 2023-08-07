import React from 'react'
import { Box } from '@mui/material';
import ProjectDetailCard from '../components/ProjectDetailCard'
import AppPagination from '../components/AppPagination';
import ProjectDataContext from '../context/ProjectDataContext';
import NoProjectDataAvailable from '../components/NoProjectDataAvailable';

const UserProjects = () => {
  const { projectData } = React.useContext(ProjectDataContext);

  const [currentPage, setCurrentPage] = React.useState(1);

  const projectsPerPage = 5;
  const startIndex = (currentPage - 1) * projectsPerPage;
  const endIndex = startIndex + projectsPerPage;

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  }

  return (
    <Box>
      { projectData && projectData.length > 0 ? (
      <>
        {projectData.slice(startIndex, endIndex).map((project) => (
          <ProjectDetailCard key={project.id} project={project} />
        ))}
        <AppPagination
          totalItems={projectData.length}
          pageSize={projectsPerPage}
          currentPage={currentPage}
          onPageChange={handlePageChange}
        />
      </>
    ) : (
      <NoProjectDataAvailable />
    )}
  </Box>
  )
}

export default UserProjects