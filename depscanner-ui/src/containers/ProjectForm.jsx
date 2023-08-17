import React from 'react'
import { Paper, FormGroup } from '@mui/material';
import FileUpload from '../components/AddProjectForm/FileUpload';
import ReviewTable from '../components/AddProjectForm/ReviewTable';
import ProjectDetail from '../components/AddProjectForm/ProjectDetail';

const ProjectForm = ({projectDependencies, setProjectName, setProjectDescription, setProjectDependencies, activeStep, setEnableNext, setActiveStep, scanFrequency, setScanFrequency }) => {
  return (
    <>
      <Paper sx={{mt: 2}}>
        <form>
          <FormGroup sx={{ display: activeStep === 0 ? '' : 'none', m: 2 }}>
            {activeStep === 0 && <FileUpload {...{activeStep, setActiveStep, setProjectDependencies, setEnableNext }} />}
          </FormGroup>
          <FormGroup sx={{ display: activeStep === 1 ? '' : 'none', m: 2 }}>
            {activeStep === 1 && <ReviewTable {...{projectDependencies, setProjectDependencies, setEnableNext}} />}
          </FormGroup>
          <FormGroup sx={{ display: activeStep === 2 ? '' : 'none', m: 2 }}>
            {activeStep === 2 && <ProjectDetail {...{setProjectName, setProjectDescription, scanFrequency, setScanFrequency}} />}
          </FormGroup>
        </form>
      </Paper>
    </>
  );
}

export default ProjectForm