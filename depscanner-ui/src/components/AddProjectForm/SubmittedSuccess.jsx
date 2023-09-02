import React from 'react';
import { Box, Typography, Snackbar, Alert } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

const SubmittedSuccess = ({ id }) => {
  const navigate = useNavigate();
  const [successSnackbarOpen, setSuccessSnackbarOpen] = React.useState(true);

  const handleCloseSnackbar = () => {
    setSuccessSnackbarOpen(false);
    navigate(`/project/${id}`);
  };

  return (
    <>
    <Box
          display="flex"
          flexDirection="column"
          justifyContent="center"
          alignItems="center"
          minHeight="45vh"
        >
      <CheckCircleIcon sx={{ fontSize: 68, color: 'green', marginRight: '8px' }} />
          <Typography variant="h4" align='center'>
            Project has successfully been created!
          </Typography>
    </Box>
            <Snackbar
            open={successSnackbarOpen}
            autoHideDuration={5000}
            onClose={handleCloseSnackbar}
            >
              <Alert onClose={handleCloseSnackbar} severity="success" sx={{ width: '100%' }}>
                Redirecting to Project Summary.....
              </Alert>
            </Snackbar>
    </>
  );
};

export default SubmittedSuccess;
