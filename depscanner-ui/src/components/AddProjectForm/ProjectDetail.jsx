import React from 'react'
import { Container, Box, TextField, FormLabel, FormControl, FormControlLabel, RadioGroup, Radio } from '@mui/material'

const ProjectDetail = ({projectName, projectDescription, setProjectName, setProjectDescription, scanFrequency, setScanFrequency }) => {
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