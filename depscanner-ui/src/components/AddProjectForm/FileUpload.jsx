import React from 'react';
import { Box, Typography, CircularProgress, Button, Paper } from '@mui/material';
import AddCircleOutlinedIcon from '@mui/icons-material/AddCircleOutlined';
import { axiosFormSubmit } from '../../utils/axios';

const FileUpload = ({ setActiveStep, setProjectDependencies, setEnableNext }) => {
  const [isLoading, setIsLoading] = React.useState(false);
  const [errMessage, setErrMessage] = React.useState('');
  const errRef = React.useRef();

  React.useEffect(() => {
    setEnableNext(false);
  }, []);

  const handleFileChange = async (e) => {
    setErrMessage();
    setIsLoading(true);
    const formData = new FormData();
    formData.append('file', e.target.files[0]);

    try {
      const res = await axiosFormSubmit.post(`upload/file`, formData);
      setProjectDependencies(res.data);
      setActiveStep((step) => step + 1);
    } catch (err) {
      setErrMessage(err.response.data.message);
      errRef.current.focus();
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mt: 3, minHeight: '70vh'}}>
      <Box variant="body2" sx={{ maxWidth: '600px', textAlign: 'center', p: 2 }}>
        Step 1 is to upload your project build tool file, which is located in the root of your project folder. 
        Upon uploading your project file, the open-source dependencies will automatically be parsed.
      </Box>
      <Box sx={{ position: 'relative', p: 6 }}>
        <Paper elevation={7} 
          sx={{ 
            width: '100%', 
            p: 8,
            border: '1px dashed #888',
            borderRadius: '4px' 
            }}
          >
          <Box
            sx={{
              display: 'flex',
              height: '100%',
              p:4,
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            {isLoading ? (
              <CircularProgress color="primary" />
            ) : (
              <Button sx={{ boxShadow: 4 }} variant="contained" component="label">
                <AddCircleOutlinedIcon style={{ fontSize: '2rem' }} />
                <Typography sx={{ ml: 1 }}>Upload</Typography>
                <input
                  type="file"
                  onChange={handleFileChange}
                  style={{ display: 'none' }}
                />
              </Button>
            )}
            <Typography
              sx={{ mt: 2 }}
              component="h1"
              variant="h5"
              align="center"
              ref={errRef}
              className={errMessage ? 'errmsg' : 'offscreen'}
              aria-live="assertive"
            >
              {errMessage ? errMessage : ''}
            </Typography>
            <Typography variant="subtitle1" align="center" sx={{ mt: 2, fontWeight: 700 }}>
              Current supported build tool files:
            </Typography>
            <Typography variant="subtitle3" align="center">
              pom.xml (MAVEN)
            </Typography>
            <Typography variant="subtitle3" align="center">
              package.json (NPM)
            </Typography>
          </Box>
        </Paper>
      </Box>
    </Box>
  );  
}

export default FileUpload;
