import React from 'react'
import { Modal, FormControl, FormLabel, RadioGroup, FormControlLabel, Button, Radio, Box, Paper, Divider, Snackbar, Alert } from '@mui/material';
import { Close } from '@mui/icons-material';
import ProjectDataContext from '../../context/ProjectDataContext';

const UpdateScanModal = ({ selectedFrequency, projectId }) => {
    const [isOpen, setIsOpen] = React.useState(false);
    const [selected, setSelected] = React.useState(selectedFrequency);
    const { patchScanSchedule, successSnackbarOpen, setSuccessSnackbarOpen, errorSnackbarOpen, setErrorSnackbarOpen } = React.useContext(ProjectDataContext);

    const handleModalOpen = () => {
      setIsOpen(true);
    };
  
    const handleModalClose = () => {
      setIsOpen(false);
      setSelected(selectedFrequency);
    };

    const handleUpdate = () => {
        let updatedWeeklyScanned = false;
        let updatedDailyScanned = false;
    
        switch (selected) {
            case 'Weekly':
                updatedWeeklyScanned = true;
                break;
            case 'Daily':
                updatedDailyScanned = true;
                break;
            default:
        }

        patchScanSchedule(projectId, updatedWeeklyScanned, updatedDailyScanned);
        setIsOpen(false);
    };

    return (
        <>
        <Button sx={{ ml: 2 }} onClick={handleModalOpen}>
          UPDATE FREQUENCY
        </Button>
  
        <Modal open={isOpen} onClose={handleModalClose}>
          <Box
            sx={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              minHeight: '100vh'
            }}
          >
            <Box
              sx={{
                position: 'relative',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
              }}
            >
            <Paper elevation={1} sx={{ p: '30px'}}> 
              <Close
                style={{
                  position: 'absolute',
                  top: '10px',
                  right: '10px',
                  cursor: 'pointer',
                }}
                onClick={handleModalClose}
              />
              <Box>
              <FormControl component="fieldset">
                <FormLabel component="legend">Update Scan Frequency</FormLabel>
                <Divider/>
                <RadioGroup
                  aria-label="frequency"
                  name="frequency"
                  value={selected}
                  onChange={(e) => setSelected(e.target.value)}
                >
                  <FormControlLabel value="Weekly" control={<Radio />} label="Weekly"/>
                  <FormControlLabel value="Daily" control={<Radio />} label="Daily" />
                  <FormControlLabel value="None" control={<Radio />} label="None" />
                </RadioGroup>
              </FormControl>
              </Box>
              <Button variant="outlined" onClick={handleUpdate} sx={{mt:2, width:'100%'}} disabled={selected === selectedFrequency}>
                Update
              </Button>
              </Paper>
            </Box>
          </Box>
        </Modal>
        <Snackbar
        open={successSnackbarOpen}
        autoHideDuration={3000}
        onClose={() => setSuccessSnackbarOpen(false)}
        >
          <Alert onClose={() => setSuccessSnackbarOpen(false)} severity="success" sx={{ width: '100%' }}>
            Scan schedule has successfully been updated!
          </Alert>
        </Snackbar>
        <Snackbar
        open={errorSnackbarOpen}
        autoHideDuration={3000}
        onClose={() => setErrorSnackbarOpen(false)}
        >
          <Box>
          <Alert onClose={() => setSuccessSnackbarOpen(false)} severity="error" sx={{ width: '100%' }}>
            Error updating scan schedule. Please try again later.
          </Alert>
          </Box>
        </Snackbar>
      </>
      );
}

export default UpdateScanModal