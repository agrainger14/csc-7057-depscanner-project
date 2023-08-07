import React from 'react'
import { Box } from '@mui/material'
import styled from '@emotion/styled'
import HeroBackground from '../assets/AnimatedShape.svg'
import ProjectDetailCard from '../components/ProjectDetailCard'

const ProjectDisplayBox = styled(Box)({
    backgroundImage: `url(${HeroBackground})`,
    height: '300px',
})

const ProjectDisplay = ({ project }) => {
  return (
    <Box>
        <ProjectDetailCard key={project.id} project={project} />
    </Box>
  )
}

export default ProjectDisplay