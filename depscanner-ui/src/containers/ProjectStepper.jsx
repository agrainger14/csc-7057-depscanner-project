import * as React from 'react';
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import ProjectForm from './ProjectForm';
import LoadingSpinner from '../components/LoadingSpinner';
import { axiosDefault } from '../utils/axios';
import ProjectDataContext from '../context/ProjectDataContext';
import { useOidcIdToken } from "@axa-fr/react-oidc";
import SubmittedSuccess from '../components/SubmittedSuccess';

const steps = ['Upload Project File', 'Review Dependencies', 'Name Project'];

const stepStyle = {
  boxShadow: 2,
  backgroundColor: 'rgba(255,255,255,0.1)',
  padding: 2,
}

export default function ProjectStepper() {
  const { projectData, setProjectData } = React.useContext(ProjectDataContext); 
  const [activeStep, setActiveStep] = React.useState(0);
  const [enableNext, setEnableNext] = React.useState(false);
  const [projectSubmitted, setProjectSubmitted] = React.useState(false);
  const [isLoading, setIsLoading] = React.useState(false);
  const [projectName, setProjectName] = React.useState(null);
  const [projectDescription, setProjectDescription] = React.useState(null);
  const [projectDependencies, setProjectDependencies] = React.useState(null);
  const { idToken } = useOidcIdToken();

  React.useEffect(() => {
    if (activeStep === steps.length && !projectSubmitted) {
      const submitProject = async () => {
        const controller = new AbortController();
        setIsLoading(true);
    
        const requestBody = {
          name: projectName,
          description: projectDescription,
          dependencies: projectDependencies,
        }

        const headers = {
          'Authorization': 'Bearer ' + idToken
        };

        try {
          const res = await axiosDefault.post('/project/user', requestBody, {
            headers: headers,
            signal: controller.signal
          })
          setProjectSubmitted(true);

          const allProjects = projectData ? [...projectData, res.data] : [res.data];
          setProjectData(allProjects);

          setProjectName('');
          setProjectDescription('');
          setProjectDependencies(null);
        } catch (err) {
          console.log(err)
        } finally {
          setIsLoading(false)
        }
        return () => controller.abort();
      }
      submitProject();
    }
  }, [activeStep]);

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  return (
    <Box sx={{ width: '100%'  }}>
      <Stepper activeStep={activeStep} sx={stepStyle}>
        {steps.map((label, index) => {
          const stepProps = {};
          const labelProps = {};
          return (
            <Step key={label} {...stepProps}>
              <StepLabel {...labelProps}>{label}</StepLabel>
            </Step>
          );
        })}
      </Stepper>
      {activeStep === steps.length ? (
        <React.Fragment>
          <Box sx={{display:'flex', justifyContent:'center', mt:2}}>
          {isLoading ? (
              <LoadingSpinner />
            ) 
            : projectSubmitted && !isLoading ? (
              <SubmittedSuccess/>
          ) : (
            <Typography sx={{ mt: 2, mb: 1 }}>
              Error submitting project, please try again.
            </Typography>
          )}
          </Box>
          <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
            <Box sx={{ flex: '1 1 auto' }} />
          </Box>
        </React.Fragment>
      ) : (
        <React.Fragment>
          <ProjectForm {...{projectDependencies, setProjectName, setProjectDescription, setProjectDependencies, activeStep, setEnableNext, setActiveStep, setProjectSubmitted, setIsLoading}}/>
          <Box sx={{ display: 'flex', flexDirection: 'row', pt: 2 }}>
            {activeStep === 0 ? null : ( 
            <Button
              color="inherit"
              onClick={handleBack}
              sx={{ mr: 1 }}
            >
              Back
            </Button>
            )}
            <Box sx={{ flex: '1 1 auto' }} />
            {activeStep === 0 ? null : (
            <Button onClick={handleNext} disabled={!enableNext}>
              {activeStep === steps.length ? 'Submit' : 'Next'}
            </Button>
            )}
          </Box>
        </React.Fragment>
      )}
    </Box>
  );
}