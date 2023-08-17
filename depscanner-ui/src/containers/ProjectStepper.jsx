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
  const [id, setId] = React.useState(null);

  const [projectSubmitted, setProjectSubmitted] = React.useState(false);
  const [isLoading, setIsLoading] = React.useState(false);

  const [projectName, setProjectName] = React.useState(null);
  const [projectDescription, setProjectDescription] = React.useState(null);
  const [projectDependencies, setProjectDependencies] = React.useState(null);
  const [scanFrequency, setScanFrequency] = React.useState(null);
  const [dailyScanned, setDailyScanned] = React.useState(false);
  const [weeklyScanned, setWeeklyScanned] = React.useState(false);

  const { idToken } = useOidcIdToken();

  React.useEffect(() => {
    handleScanFrequency();
    if (activeStep === steps.length && !projectSubmitted) {
      const submitProject = async () => {
        const controller = new AbortController();
        setIsLoading(true);
    
        const requestBody = {
          name: projectName,
          description: projectDescription,
          dependencies: projectDependencies,
          dailyScanned: dailyScanned,
          weeklyScanned: weeklyScanned
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
          setId(res.data.id);

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
  }, [activeStep, scanFrequency]);

  const handleScanFrequency = () => {
    switch (scanFrequency) {
      case 'WEEKLY':
        setWeeklyScanned(true);
        setDailyScanned(false);
        break;
      case 'DAILY':
        setDailyScanned(true);
        setWeeklyScanned(false);
        break;
      default:
        setWeeklyScanned(false);
        setDailyScanned(false);
    }
  };

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
              id && <SubmittedSuccess id={id}/>
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
          <ProjectForm {...{projectDependencies, setProjectName, setProjectDescription, setProjectDependencies, activeStep, setEnableNext, setActiveStep, scanFrequency, setScanFrequency}}/>
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