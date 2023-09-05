import React from 'react'
import { Grid, Paper, Typography, Box, Container, Stack, Divider, Button, styled } from '@mui/material';
import { Link } from 'react-router-dom';
import ScoreCard from '../Scorecards/Scorecard'
import PublishedCard from './PublishedCard';
import LinkCard from './LinkCard';
import RelatedDependenciesChart from './RelatedDependenciesChart';
import SSFScoreCard from '../Scorecards/SSFScoreCard';
import LicensesChart from './LicensesChart';

const SummaryBox = styled(Box)({
  position: 'absolute',
  top: 10,
  right: 20,
  width: '40px',
  height: '40px',
  backgroundColor: 'darkred',
  color: 'white',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  borderRadius: '20%',
})

const DependencyOverview = ({ dependencyData, setSelectedTab }) => {
  const totalAdvisory = dependencyData && dependencyData.dependency.reduce((count, item) => count + item.advisoryDetail.length, 0);

  return (
    <Box sx={{ml:2, mt:2}}>
      <Grid item xs={11.5} sm={12} container>
        <Grid item xs={12} sm={7}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <Paper variant="outlined" sx={{ flex: 1, minHeight: '200px', position: 'relative' }}>
                {dependencyData && totalAdvisory > 0 && 
                  (<SummaryBox>
                    <Typography variant="subtitle2">
                      {totalAdvisory}
                    </Typography>
                  </SummaryBox>)
                }
                <Container sx={{ pt: 2 }}>
                  <Typography variant="h4">
                    Security Advisories
                  </Typography>
                  <Divider />
                  {dependencyData && dependencyData.dependency.some((dependency) => dependency.relation === 'SELF' && dependency.advisoryDetail.length > 0) &&
                    <Box>
                      <Typography variant="h5" sx={{ mb: 1, mt: 1, fontWeight: '500'}}>
                        In this dependency
                      </Typography>
                      {dependencyData.dependency
                        .filter((dependency) => dependency.relation === 'SELF')
                        .map((dependency, index) => (
                          <Box sx={{ mt: 1 }} key={index}>
                            {dependency.advisoryDetail.map((advisoryDetail, index) => (
                              <Stack key={index} sx={{ mb: 2 }}>
                                <Typography variant="subtitle3">
                                  {advisoryDetail.title}
                                </Typography>
                                <Grid container alignItems="center" justifyContent="space-between" textAlign="right">
                                  <Grid item>
                                    <Typography sx={{ color: 'text.secondary' }}>
                                      <ScoreCard cvss3Score={advisoryDetail.cvss3Score} />
                                      {advisoryDetail.advisoryKey.id}
                                    </Typography>
                                  </Grid>
                                  <Grid item sx={{ flexShrink: 0, mt: { xs: 1 } }}>
                                    <Link to={`/advisory/${advisoryDetail.advisoryKey.id}`}>
                                      <Button variant="outlined" sx={{ width: { xs: '100%', sm: '140px' } }}>
                                        MORE DETAIL
                                      </Button>
                                    </Link>
                                  </Grid>
                                </Grid>
                              </Stack>
                            ))}
                          </Box>
                        ))}
                    </Box>
                  }
                  {dependencyData && dependencyData.dependency.some((dependency) => dependency.relation !== 'SELF' && dependency.advisoryDetail.length > 0) &&
                    <Box>
                      <Typography variant="h5" sx={{ mb: 1, mt: 1, fontWeight: '500'}}>
                        In the dependencies
                      </Typography>
                      {dependencyData.dependency
                        .filter((dependency) => dependency.relation !== 'SELF')
                        .map((dependency, index) => (
                          <Box sx={{ mt: 1 }} key={index}>
                            {dependency.advisoryDetail.map((advisoryDetail, index) => (
                              <Stack key={index} sx={{ mb: 2 }}>
                                <Box>
                                  <Typography variant="subtitle1">
                                    Related Dependency: {dependency.versionKey.name} version: {dependency.versionKey.version}
                                  </Typography>
                                  <Typography variant="subtitle2" sx={{mb:1}}>
                                    Relation: {dependency?.relation}
                                  </Typography>
                                </Box>
                                <Typography variant="subtitle3">
                                  {advisoryDetail.title}
                                </Typography>
                                <Grid container alignItems="center" justifyContent="space-between" textAlign="right">
                                  <Grid item>
                                    <Typography sx={{ color: 'text.secondary' }}>
                                      <ScoreCard cvss3Score={advisoryDetail.cvss3Score} />
                                      {advisoryDetail.advisoryKey.id}
                                    </Typography>
                                  </Grid>
                                  <Grid item sx={{ flexShrink: 0, mt: { xs: 1 } }}>
                                    <Link to={`/advisory/${advisoryDetail.advisoryKey.id}`}>
                                      <Button variant="outlined" sx={{ width: { xs: '100%', sm: '140px' } }}>
                                        MORE DETAIL
                                      </Button>
                                    </Link>
                                  </Grid>
                                </Grid>
                              </Stack>
                            ))}
                          </Box>
                        ))
                      }
                    </Box>
                  }
                  {dependencyData.dependency.every((dependency) => dependency.advisoryDetail.length === 0) && (
                    <Typography variant="body2" sx={{ mt: 2, fontSize: '20px' }}>
                      None Detected!
                    </Typography>
                  )}
                </Container>
              </Paper>
            </Grid>
          </Grid>
        </Grid>
        <Grid item xs={12} sm={0.5}/>
        <Grid item xs={12} sm={4}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              {dependencyData && 
                <PublishedCard dependencyData={dependencyData.dependency[0]}/> 
              }
            </Grid>
            <Grid item xs={12}>
              {dependencyData && 
                <LinkCard dependencyData={dependencyData.dependency[0]} />
              }
            </Grid>
          </Grid>
        </Grid>
        <Grid mt={2} mb={2} item xs={12} sm={11.5}>
          <Box>
            {dependencyData && dependencyData.dependency[0].links.some((link) => link.label === "SOURCE_REPO") &&
              <SSFScoreCard dependencyData={dependencyData} />
            }
          </Box>
        </Grid>
        <Grid item mt={2} mb={2} xs={12} sm={11.5}>
          <Box>
            {dependencyData &&
              <LicensesChart dependencyData={dependencyData}/>
            }
          </Box>
        </Grid>
        <Grid item mt={2} mb={2} xs={12} sm={11.5}>
          <Box>
            {dependencyData &&
              <Paper variant="outlined" sx={{ flex: 1, minHeight: '100px', position: 'relative' }}>
                <Typography variant="h4" sx={{ml:2, mr:2, mt:2}}>
                  Dependencies
                </Typography>
                <Divider sx={{ml:2,mr:2}}/>
                <RelatedDependenciesChart dependencyData={dependencyData} setSelectedTab={setSelectedTab} />
              </Paper>
            }
          </Box>
        </Grid>
      </Grid>
    </Box>
  )
}

export default DependencyOverview