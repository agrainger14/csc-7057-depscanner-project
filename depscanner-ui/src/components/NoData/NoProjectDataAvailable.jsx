import React from 'react'
import { Box, Typography, Paper } from '@mui/material'
import { ErrorOutline } from '@mui/icons-material'
import { Link } from 'react-router-dom';

const NoProjectDataAvailable = () => {
  return (
    <Paper>
      <Box
        display="flex"
        flexDirection="column"
        justifyContent="center"
        alignItems="center"
        minHeight="85vh"
      >
        <ErrorOutline style={{ fontSize: 68, mb:10 }} />
        <Typography variant="h4" align='center'>
          No Project Data detected!
        </Typography>
        <Typography variant="h5" align='center'>
          You can add a project by clicking <Link to="/dashboard/add-project" style={{ color: 'inherit' }}>here</Link>.
        </Typography>
      </Box>
    </Paper>
  );
};

export default NoProjectDataAvailable