import React from 'react';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Box from '@mui/material/Box';
import { Outlet } from 'react-router-dom';
import { Link } from 'react-router-dom';
import { useLocation } from 'react-router-dom';
import { ProjectDataProvider } from '../../context/ProjectDataContext';
import HeroBackground from '../../assets/background/AnimatedShape.svg'

const DashboardTabs = () => {
  const location = useLocation();
 
  return (
    <Box sx={{ width: '100%', mt: 0.5 }}>
      <ProjectDataProvider>
        <Box sx={{ borderBottom: 1, borderColor: 'divider', display: 'flex', justifyContent: 'center', backgroundImage: `url(${HeroBackground})` }}>
          <Tabs value={location.pathname}>
            <Tab label="Projects Overview" value={"/dashboard/projects"} component={Link} to="/dashboard/projects" />
            <Tab label="Add New Project" value={"/dashboard/add-project"} component={Link} to="/dashboard/add-project" />
          </Tabs>
        </Box>
        <Box sx={{ p: 3, flexGrow: 1 }}>
          <Outlet />
        </Box>
      </ProjectDataProvider>
    </Box>
  );
}

export default DashboardTabs;