import React from 'react'
import { Box } from '@mui/material'
import ProjectDetailCard from '../components/ProjectDetailCard'

const ProjectDisplay = ({ project }) => {
  return (
    <Box>
      <ProjectDetailCard key={project.id} project={project} />
    </Box>
  )
}

export default ProjectDisplay