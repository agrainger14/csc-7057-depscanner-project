import React from 'react'
import { Container, Box, TextField } from '@mui/material'

const ProjectDetail = ({projectName, projectDescription, setProjectName, setProjectDescription }) => {
  return (
    <Box sx={{mt:2}}>
        <Box sx={{ width: '100%', display: 'flex', justifyContent:'center', minHeight: '70vh' }}>
          <Box sx={{width:'100%'}}>
            <TextField
              label="Project Name"
              value={projectName}
              onChange={(e) => setProjectName(e.target.value)}
              fullWidth
              required
              sx={{ mt: 5 }}
            />
            <TextField
              label="Description"
              value={projectDescription}
              onChange={(e) => setProjectDescription(e.target.value)}
              fullWidth
              required
              multiline
              rows={16}
              sx={{ mt: 3 }}
            />
            <Container
              sx={{
                display: 'flex',
                justifyContent: 'flex-start',
                alignItems: 'center',
                mt: 3,
              }}
            >
            </Container>
          </Box>
        </Box>
    </Box>
  )
}

export default ProjectDetail