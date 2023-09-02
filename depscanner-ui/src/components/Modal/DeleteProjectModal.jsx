import React from 'react'
import { Modal, Fade, Typography, Backdrop, Button, Box, Snackbar, Alert, IconButton } from '@mui/material';
import ClearIcon from '@mui/icons-material/Clear';
import ProjectDataContext from '../../context/ProjectDataContext';

const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 400,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
    textAlign: 'center'
  };

const DeleteProjectModal = ({ id }) => {
  const { handleDelete, successDeleteProject, setSuccessDeleteProject } = React.useContext(ProjectDataContext);
  const [isOpen, setIsOpen] = React.useState(false);

  const handleModalOpen = () => {
    setIsOpen(true);
  };

  const handleModalClose = () => {
    setIsOpen(false);
  };

  return (
    <>
        <IconButton sx={{
            position: 'absolute',
            top: 0,
            right: 0,
        }}
        onClick={handleModalOpen}
        aria-label="Delete"
        >
            <ClearIcon />
        </IconButton>

        <Modal
        aria-labelledby="transition-modal-title"
        aria-describedby="transition-modal-description"
        open={isOpen}
        onClose={handleModalClose}
        closeAfterTransition
        slots={{ backdrop: Backdrop }}
        slotProps={{
          backdrop: {
            timeout: 500,
          },
        }}
      >
        <Fade in={isOpen}>
          <Box sx={style}>
            <Typography variant="h6" component="h2">
              Delete Project?
            </Typography>
            <Typography sx={{ mt: 2 }}>
              This project will be permanently deleted! Would you like to continue?
            </Typography>
            <Box sx={{mt:2}}>
            <Button onClick={() => handleDelete(id)}>
                Yes
            </Button>
            <Button onClick={handleModalClose}>
                No
            </Button>
            </Box>
          </Box>
        </Fade>
        </Modal>
        <Snackbar
        open={successDeleteProject}
        autoHideDuration={3000}
        onClose={() => setSuccessDeleteProject(false)}
        >
          <Alert onClose={() => setSuccessDeleteProject(false)} severity="success" sx={{ width: '100%' }}>
            Project successfully deleted!
          </Alert>
        </Snackbar>
  </>
  )
}

export default DeleteProjectModal