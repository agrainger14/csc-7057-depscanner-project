import React from 'react'
import { Container, Box, TextField, FormLabel, FormControl, FormControlLabel, RadioGroup, Radio } from '@mui/material'

const ProjectDetail = ({projectName, projectDescription, setProjectName, setProjectDescription, scanFrequency, setScanFrequency, setIsFormValid }) => {
  const handleProjectNameChange = (e) => {
    setProjectName(e.target.value);
    setIsFormValid(e.target.value.trim().length >= 3 && projectDescription !== "");
  };

  const handleProjectDescriptionChange = (e) => {
    setProjectDescription(e.target.value);
    setIsFormValid(projectName !== null && projectName.length >= 3 && e.target.value !== "");
  };

  return (
    <Box sx={{mt:2}}>
        <Box sx={{ width: '100%', display: 'flex', justifyContent:'center', minHeight: '70vh' }}>
          <Box sx={{width:'100%'}}>
            <TextField
              label="Project Name"
              value={projectName}
              onChange={(e) => handleProjectNameChange(e)}
              fullWidth
              required
              sx={{ mt: 5 }}
              helperText={
                projectName && projectName.trim().length < 3 ? 'Project name must be at least 3 characters' : ''
              }
            />
            <TextField
              label="Description"
              value={projectDescription}
              onChange={(e) => handleProjectDescriptionChange(e)}
              fullWidth
              required
              multiline
              rows={16}
              sx={{ mt: 3 }}
              helperText={
                projectDescription === '' ? 'Description is required' : ''
              }
            />
            <Container
              sx={{
                display: 'flex',
                justifyContent: 'flex-start',
                alignItems: 'center',
                mt: 3,
              }}
            >
              <Container
                sx={{
                  display: 'flex',
                  justifyContent: 'center', 
                  alignItems: 'center',
                  mt: 2,
                  mb:2,
                }}
              >
            <FormControl component="fieldset">
              <FormLabel component="legend">Scan Frequency</FormLabel>
              <RadioGroup
                row
                aria-label="scanFrequency"
                name="scanFrequency"
                value={scanFrequency || "NONE"}
                onChange={(e) => setScanFrequency(e.target.value)}
              >
                <FormControlLabel value="NONE" control={<Radio />} label="No Scheduled Scans" />
                <FormControlLabel value="DAILY" control={<Radio />} label="Daily" />
                <FormControlLabel value="WEEKLY" control={<Radio />} label="Weekly" />
              </RadioGroup>
            </FormControl>
            </Container>
            </Container>
          </Box>
        </Box>
    </Box>
  )
}

export default ProjectDetail