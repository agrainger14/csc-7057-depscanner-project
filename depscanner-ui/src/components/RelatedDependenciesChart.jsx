import React from 'react';
import { Box, Typography, Button } from '@mui/material';

const RelatedDependenciesChart = ({ dependencyData, setSelectedTab }) => {
  const [self, setSelf] = React.useState(0);
  const [directCount, setDirectCount] = React.useState(0);
  const [indirectCount, setIndirectCount] = React.useState(0);
  const [totalCount, setTotalCount] = React.useState(0);
    
  React.useEffect(() => {
    setSelf(() => dependencyData.dependency.filter((dependency) => dependency.relation === 'SELF').length);
    setDirectCount(() => dependencyData.dependency.filter((dependency) => dependency.relation === 'DIRECT').length);
    setIndirectCount(() => dependencyData.dependency.filter((dependency) => dependency.relation === 'INDIRECT').length);
    setTotalCount(() => directCount + indirectCount);
  })

  const directPercentage = (directCount / totalCount) * 100;
  const indirectPercentage = (indirectCount / totalCount) * 100;

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', mt: 2 }}>
      <Box sx={{ display: 'flex', alignItems: 'center' }}>
        <Box sx={{
          position: 'absolute',
          top: 10,
          right: 20,
          width: '40px',
          height: '40px',
          backgroundColor: 'transparent',
          color: 'white',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          borderRadius: '20%',
          borderColor: 'white',
          borderWidth: 1,
          borderStyle: 'solid'
          }}
        >
          <Typography variant="subtitle2">
            {totalCount}
          </Typography>
        </Box>
        <Typography variant="subtitle1" sx={{ flex: 1, ml:4 }}>
          Direct
        </Typography>
        <Typography variant="subtitle2" sx={{ flex: 0.2 }}>
          {directCount}
        </Typography>
        <Box
          sx={{
            flexGrow: 7,
            mr:2,
            display: 'flex',
            alignItems: 'center',
            height: '5px',
            backgroundColor: '#CCCCCC',
            borderRadius: '2.5px',
          }}
        >
          <Box
            sx={{
              width: `${directPercentage}%`,
              height: '100%',
              backgroundColor: '#0084FF',
              borderRadius: '2.5px',
            }}
          />
        </Box>
      </Box>
      <Box sx={{ display: 'flex', alignItems: 'center' }}>
        <Typography variant="subtitle1" sx={{ flex: 1, ml:4 }}>
          Indirect
        </Typography>
        <Typography variant="subtitle2" sx={{ flex: 0.2 }}>
          {indirectCount}
        </Typography>
        <Box
          sx={{
            flexGrow: 7,
            mr:2,
            display: 'flex',
            alignItems: 'center',
            height: '5px',
            backgroundColor: '#CCCCCC',
            borderRadius: '2.5px',
          }}
        >
          <Box
            sx={{
              width: `${indirectPercentage}%`,
              height: '100%',
              backgroundColor: '#FFB100',
              borderRadius: '2.5px',
            }}
          />
        </Box>
      </Box>
      <Button disableRipple disableElevation style={{ backgroundColor: 'transparent' }} onClick={() => setSelectedTab(1)}>
        View All Dependencies
      </Button>
    </Box>
  );
}

export default RelatedDependenciesChart;
