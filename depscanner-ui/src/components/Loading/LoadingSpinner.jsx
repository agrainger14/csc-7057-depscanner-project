import React from 'react';
import { Box, Typography } from '@mui/material';
import { CircularProgress } from '@mui/material';

const LoadingSpinner = () => {
  return (
    <Box style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', flexDirection:'column', height: '80vh' }}>
      <CircularProgress color="primary" />
      <Typography sx={{mt:2}}>Please wait... Loading information..</Typography>
    </Box>
  );
};

export default LoadingSpinner;
