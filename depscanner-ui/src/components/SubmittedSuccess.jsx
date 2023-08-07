import React from 'react';
import { Box, Typography, Stack, Snackbar, Alert as MuiAlert } from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

const Alert = React.forwardRef(function Alert(props, ref) {
    return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
  });

const SubmittedSuccess = () => {
    const [open, setOpen] = React.useState(false);

    const handleClick = () => {
      setOpen(true);
    };
  
    const handleClose = (event, reason) => {
      if (reason === 'clickaway') {
        return;
      }
  
      setOpen(false);
    };

  return (
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
  );
};

export default SubmittedSuccess;
