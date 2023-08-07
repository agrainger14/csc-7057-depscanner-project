import React from 'react'
import { Box, Typography, Divider, Tab, Tabs } from '@mui/material';
import HeroBackground from '../assets/AnimatedShape.svg'
import DependencyOverview from '../components/DependencyOverview';
import DependencyTable from '../components/DependencyTable';
import DependencyGraph from '../components/DependencyGraph';
import DependencyDataContext from '../context/DependencyDataContext';

const DependencyDetail = () => {
  const { dependencyData, name, system, version } = React.useContext(DependencyDataContext);
  const [selectedTab, setSelectedTab] = React.useState(0);
  const [error, setError] = React.useState(false);

  const handleTabChange = (event, newValue) => {
    setSelectedTab(newValue);
  };

  return (
    <Box sx={{display:'flex', flexDirection:'column'}}>
      <Box sx={{ borderBottom: 1, borderColor: 'divider', display: 'flex', justifyContent: 'center', backgroundImage: `url(${HeroBackground})` }}>
        <Tabs value={selectedTab} onChange={handleTabChange}>
          <Tab label="Dependency Overview" />
          <Tab label="Related Dependencies" />
          <Tab label="Graph" />
        </Tabs>
      </Box>
      <Box>
      </Box>
      <Box>
      <Typography variant="h3" sx={{ mt: 2, ml: 2, fontWeight: 700, textAlign:'center'}}>
        {name} | {version} <span style={{ fontSize: '1.2rem' }}>{system}</span>
      </Typography>
      </Box>
      <Divider sx={{ ml: 2, mr: 2 }} />
      {selectedTab === 0 && <DependencyOverview dependencyData={dependencyData} />}
      {selectedTab === 1 && <DependencyTable dependencyData={dependencyData} />}
      {selectedTab === 2 && <DependencyGraph dependencyData={dependencyData}/>}
    </Box>
  )
}

export default DependencyDetail