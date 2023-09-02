import React from 'react'
import { Hidden, Card, CardContent, Grid, Typography, Container, Stack, CardMedia, Divider } from '@mui/material'
import ArrowCircleRightIcon from '@mui/icons-material/ArrowCircleRight';
import UploadImage from '../../assets/img/upload.webp';
import DependencyGraph from '../../assets/img/dependencies.webp';
import EmailNotification from '../../assets/img/email-notification.webp';
import LooksOneIcon from '@mui/icons-material/LooksOne';
import LooksTwoIcon from '@mui/icons-material/LooksTwo';
import Looks3Icon from '@mui/icons-material/Looks3';
import Title from '../../utils/title';

export default function AboutCards() {
  return (
      <Container disableGutters sx={{mt:4, textAlign: 'center'}}>
      <Divider sx={{ mb: 2 }} />
        <Title variant={{ xs: 'h4' }} sx={{ textAlign: 'center'}}>
          HOW DOES DEPSCANNER WORK?
        </Title>
        <Grid container direction="row" alignItems="stretch" justifyContent="center">
          <Card sx={{ maxWidth: 300, mt: 4, ml: 4, mb: 4 }}>
            <CardMedia
              sx={{ height: 150 }}
              image={UploadImage}
              title="upload"
            />
            <CardContent sx={{textAlign: 'center'}}>
              <Typography gutterBottom variant="h5" alignContent="center">
                <LooksOneIcon/>
              </Typography>
              <Typography variant="body2" color="text.secondary">
              Upload your projects build tool file. 
              By uploading this file, DepScanner will extract and track your projects dependencies.
              The dependencies associated with your project will be stored within the database.
              </Typography>
            </CardContent>
          </Card>
          <Hidden lgDown>  
            <Stack direction="row" alignItems="center" sx={{ml: 4}}>
              <ArrowCircleRightIcon fontSize="large"/>
            </Stack>
          </Hidden>
          <Card sx={{ maxWidth: 300, mt: 4, ml: 4, mb: 4 }}>
            <CardMedia
              sx={{ height: 150 }}
              image={DependencyGraph}
              title="DependencyGraph"
            />
            <CardContent sx={{textAlign: 'center'}}>
              <Typography gutterBottom variant="h5" alignContent="center">
                <LooksTwoIcon/>
              </Typography>
              <Typography variant="body2" color="text.secondary">
              Information will be retrieved from the uploaded dependencies which your project rely on.
              A full visualisation is provided to give a breakdown of all the dependency information within your codebase.
              </Typography>
            </CardContent>
          </Card>
          <Hidden lgDown>  
          <Stack direction="row" alignItems="center" gap={1} sx={{ml: 4}}>
            <ArrowCircleRightIcon fontSize="large"/>
          </Stack>
          </Hidden>
          <Card sx={{ maxWidth: 300, mt: 4, ml: 4, mb: 4 }}>
            <CardMedia
              sx={{ height: 150 }}
              image={EmailNotification}
              title="EmailNotification"
            />
            <CardContent sx={{textAlign: 'center'}}>
              <Typography gutterBottom variant="h5" alignContent="center">
                <Looks3Icon/>
              </Typography>
              <Typography variant="body2" color="text.secondary">
              Daily or weekly scanning can be chosen when adding a project to obtain the latest security information.
              If a vulnerability is detected, an email notification will be sent
              highlighting the vulnerablility details.
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Divider sx={{ mb: 5 }} />
      </Container>
  );
}