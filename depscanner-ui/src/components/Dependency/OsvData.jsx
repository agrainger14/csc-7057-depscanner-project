import React from 'react';
import { Container, Typography, Link, Box, Divider, Grid, Dialog, DialogTitle, DialogContent, Button, List, ListItem, ListItemText } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import Gradient from '../../assets/background/slanted-gradient.svg'
import Cvss3ScoreCalculator from '../Scorecards/Cvss3ScoreCalculator';
import ArrowCircleRightIcon from '@mui/icons-material/ArrowCircleRight';

const OsvData = ({ osvData }) => {
  const [isModalOpen, setIsModalOpen] = React.useState(false);
  const { id, summary, details, aliases, modified, published, references, affected, severity } = osvData;

  const handleModalOpen = () => {
    setIsModalOpen(true);
  };

  const handleModalClose = () => {
    setIsModalOpen(false);
  };
  
  return (
      <Box sx={{background: `url(${Gradient})`, backgroundSize: 'cover', backgroundPosition: 'center', minHeight: '100vh'}}>
        <Container maxWidth={'xl'} sx={{backgroundColor:'background.default', 
          display: 'flex',
          flexDirection: 'column',
          border: '1px solid white',
          boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
          borderRadius: '8px', 
          minHeight: '100%',
        }}
        >
          <Box py={3}>
            <Box sx={{mb:2}}>
              <Typography variant="h3" sx={{fontWeight:700}}>{summary}</Typography>
              <Divider/>
              <Box style={{ display: 'flex', alignItems: 'center'}}>
                <Typography sx={{ fontWeight: 700, mr:1 }}>Source: </Typography>
                <Link href={`http://osv.dev/vulnerability/${id}`}>
                <Typography>OSV</Typography>
                </Link>
              </Box>
              <Typography variant="subtitle2" sx={{mt:1}}>
                Published: {new Date(published).toLocaleDateString()}
              </Typography>
              <Typography variant="subtitle2">
                Modified: {new Date(modified).toLocaleDateString()}
              </Typography>
              <Divider/>
            </Box>
            <Box py={0.5}>
      <Typography variant='h4' sx={{ fontWeight: 700, mb: 1 }}>Overview</Typography>
      <Grid container spacing={2} alignItems="center">
        <Grid item>
          <Typography sx={{ fontWeight: 500, pb: 0.5 }}>Vulnerability ID:</Typography>
        </Grid>
        <Grid item>
          <Typography sx={{ pb: 0.5 }}>{id}</Typography>
        </Grid>
      </Grid>
      <Grid container spacing={2} alignItems="center">
        <Grid item>
        {aliases && 
          <Typography sx={{ fontWeight: 500, pb: 0.5 }}>Aliases:</Typography>
        }
        </Grid>
        <Grid item>
          <Typography sx={{ pb: 0.5 }} variant="body1">
            {aliases}
          </Typography>
        </Grid>
      </Grid>
      <Grid container alignItems="center">
        <Grid item>
          <Typography sx={{ fontWeight: 500, pb: 0.5, pr: 2 }}>Affected Dependency:</Typography>
        </Grid>
        <Grid item>
          <Typography sx={{ pb: 0.5 }} variant="body1">
              {affected[0].package.name}
          </Typography>
        </Grid>
      </Grid>
      <Grid container spacing={2} alignItems="center">
        <Grid item>
          <Typography sx={{ fontWeight: 500 }}>Severity:</Typography>
        </Grid>
        <Grid item>
          <Cvss3ScoreCalculator score={severity ? severity[0].score : 0} />
        </Grid>
      </Grid>
    </Box>
          <Box sx={{mt:2, mb:1}}>
            <Typography sx={{ fontWeight: 500 }}>Description: </Typography>
            <Typography variant="body2" sx={{fontSize:'20px'}}>{details}</Typography>
          </Box>
          <Divider/>
          <Box>
            <Typography variant='h4' sx={{fontWeight: 700, mt:2, mb:1}}>Affected Versions</Typography> 
            {
              affected.map((item, index) => (
                <Box key={index}>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb:1}}>
                    <Typography sx={{ fontWeight: 700, mr: 2 }}>
                      Introduced:
                    </Typography>
                    <Typography sx={{ color: 'error.main' }}>
                      {item.ranges[0].events[0].introduced}
                    </Typography>
                    <ArrowCircleRightIcon sx={{ ml: 2, mr: 2 }} />
                    {item.ranges[0].events[1].fixed ? (
                      <Typography sx={{ fontWeight: 700, mr: 2 }}>
                        Fixed:
                      </Typography>
                    ) : (
                      <Typography sx={{ fontWeight: 700, mr: 2 }}>
                        Last Affected:
                      </Typography>
                    )}
                    <Typography sx={{ color: 'success.main' }}>
                      {item.ranges[0].events[1].fixed || item.ranges[0].events[1].last_affected}
                    </Typography>
                  </Box>
                </Box>
              ))
            }
            {affected[0].versions && 
              <Button variant="outlined" sx={{mt:1, mb:1}} onClick={handleModalOpen}>
                Show All Affected Versions
              </Button>
            }
            <Dialog open={isModalOpen} onClose={handleModalClose} sx={{overflow: 'hidden' }}>
              <DialogTitle>All Affected Versions</DialogTitle>
              <DialogContent>
                {affected[0].versions && affected[0].versions.map((version, index) => (
                <RouterLink 
                  key={index} 
                  style={{color: 'inherit' }}
                  to={`/dependency/${encodeURIComponent(affected[0].package.name)}/${affected[0].package.ecosystem.toUpperCase()}/${version}`}
                >
                  <Typography key={index} sx={{ mt: 1}}>{version}</Typography>
                </RouterLink>
                ))}
              </DialogContent>
            </Dialog>
            <Divider/>
          </Box>
          <Box sx={{ display: 'flex', flexDirection: 'column' }}>
            <Typography variant='h4' sx={{fontWeight: 700, mt:2 }}>References</Typography> 
            <List>
            {references.map((ref, index) => (
                <ListItem key={index}>
                  <Link href={ref.url} target="_blank">
                    <ListItemText primary={ref.url} />
                  </Link>
                </ListItem>
            ))}
            </List>
          </Box>
          <Divider/>
        </Box>
        </Container>
        </Box>
      );
    };

export default OsvData;
