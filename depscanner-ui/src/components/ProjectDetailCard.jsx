import React from 'react';
import styled from '@emotion/styled';
import { Box, Tooltip, Divider, IconButton } from '@mui/material';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import Chip from '@mui/material/Chip';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';
import HelpOutlinedIcon from '@mui/icons-material/HelpOutlined';
import { Link } from 'react-router-dom';
import ClearIcon from '@mui/icons-material/Clear';
import ProjectDataContext from '../context/ProjectDataContext';
import JavaIcon from '../assets/icons/java/icons8-java.svg';
import NPMIcon from '../assets/icons/javascript/icons8-npm.svg';

const CardBox = styled(Box)({
  display: 'flex',
  flexWrap: 'wrap',
  justifyContent: 'center',
  margin: 2,
  zIndex: 2,
})

const CardLayout = styled(Card)({
  margin: 1,
  boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.1)',
  borderRadius: 1,
  marginBottom: "16px",
  zIndex: 2, 
})

const ChipLayout = styled(Box)({
  display: 'flex',
  flexWrap: 'wrap',
  marginBottom: 2,
})

const displayProjectIcons = ( projectType ) => {
  switch (projectType) {
    case 'JAVA':
      return [
        <img key="java-icon" src={JavaIcon} style={{ width: 300, height: 300, marginRight: '10px' }} alt="Java" />
      ];
    case 'JAVASCRIPT':
      return [
        <img key="npm-icon" src={NPMIcon} style={{ width: 300, height: 300, marginRight: '10px' }} alt="NPM" />,
      ];
    default:
      return null;
  }
}

const ProjectCard = ({ project }) => {
  const { handleDelete } = React.useContext(ProjectDataContext);

  return (
    <CardBox>
      <CardLayout>
        <Box style={{ position: 'relative' }}>
          <CardContent sx={{flexGrow: 1, zIndex: 2, position: 'relative'}}>
            <IconButton sx={{
                position: 'absolute',
                top: 0,
                right: 0,
              }}
              onClick={() => handleDelete(project.id)}
              aria-label="Delete"
            >
              <ClearIcon />
            </IconButton>
            <Link to={`/project/${project.id}`} style={{ textDecoration: 'none', color:'inherit'}}>
              <Typography variant="h5" sx={{ mb: 2, display: 'flex', justifyContent: 'space-between', fontWeight: 700}}>
                {project.name}
              </Typography>
            </Link>
            <Box sx={{zIndex:2, position: 'relative'}}>
              <Divider sx={{mb:1, mt:1}}/>
              <Typography variant="body2" color="textSecondary">
                {project?.description} 
              </Typography>
              <Divider sx={{mb:1, mt:1}}/>
              <Typography variant="body2" color="textSecondary">
                Upload Date/Time: {new Date(project.createdAt).toLocaleString()} 
              </Typography>
              <Typography variant="body2" color="textSecondary">
                Total Project Dependencies: {project.projectDependenciesCount}
              </Typography>
              <Typography variant="body2" color="textSecondary">
                Vulnerable Dependencies Detected: {project.vulnerableDependenciesCount}
              </Typography>
            </Box>
          </CardContent>
          <ChipLayout>
            {project.dependencies.map((dependency) => (
              <Tooltip
                key={dependency.id}
                title={`Status: ${dependency.isVulnerable ? "Vulnerabilities Detected" : dependency.isVulnerable === null ? "No Information Available" : "No Vulnerabilities Detected"}`}
                placement="top"
              >
                <Link to={`/dependency/${encodeURIComponent(dependency.name)}/${dependency.system}/${dependency.version}`}>
                  <Chip
                    sx={{mr: 1, mb:2, ml:1, zIndex: 1, position: 'relative', borderColor: 'black', cursor:'pointer'}}
                    key={dependency.id}
                    label={dependency.name + ' (' + dependency.version + ')'}
                    icon={dependency.isVulnerable ? <ErrorIcon /> : (dependency.isVulnerable === null ? <HelpOutlinedIcon/> : <CheckCircleIcon />)}
                    color={dependency.isVulnerable ? 'secondary' : (dependency.isVulnerable === null ? 'warning' : 'primary')}
                  />
                </Link>
              </Tooltip>
            ))}
          </ChipLayout>
          <Box sx={{
            position: 'absolute',
            right: 0,
            bottom: 0,
            display: 'flex',
            alignItems: 'center',
            pointerEvents: 'none',
          }}
          >
            {displayProjectIcons(project.projectType)}
          </Box>
       </Box>
      </CardLayout>
    </CardBox>
  );
};

export default ProjectCard;