import React from 'react'
import { Box } from '@mui/material';
import ProjectDetailCard from '../../components/Project/ProjectDetailCard'
import AppPagination from '../../components/Pagination/AppPagination';
import ProjectDataContext from '../../context/ProjectDataContext';
import NoProjectDataAvailable from '../../components/NoData/NoProjectDataAvailable';

const UserProjects = () => {
  const { projectData, currentPage, projectsPerPage, startIndex, endIndex, handlePageChange } = React.useContext(ProjectDataContext);

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