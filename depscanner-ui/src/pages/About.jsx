import React from 'react'
import { Box, Container, Typography, Divider} from '@mui/material'
import Header from '../components/Header'
import Footer from '../components/Footer'
import AboutBackground from '../assets/cornered-stairs.svg';
import AboutCards from '../containers/AboutCards';
import Title from '../utils/title';

const About = () => {
  return (
    <Box>
      <Box sx={{minHeight: '100vh'}}>
          <Header/>
          <Box sx={{background: `url(${AboutBackground})`, backgroundSize: 'cover', backgroundPosition: 'center', minHeight: '125vh'}}>
          <Container maxWidth={'lg'} sx={{backgroundColor:'background.default', mb:2, mt:2, 
                  border: '1px solid white',
                  boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
                  borderRadius: '8px' }}>
            <Box sx={{mt:2}}>
            <Title variant={{ xs: 'h3', lg: 'h3' }} sx={{ textAlign: 'center'}}>
              ABOUT DEPSCANNER
            </Title>
            </Box>
            <Divider sx={{color: 'white', mb:2, mt:1}}/>
            <Box sx={{mb:2}}>
            <Typography variant="body2" sx={{textAlign:'center', fontSize: '22px' }}>
              The goal of DepScanner is to provide developers and organisations with the information in order to monitor and protect their software against potential security vulnerabilities.
            </Typography>
            </Box>
            <Box sx={{mb:3}}>
            <Title variant={{ xs: 'h5' }} color='text.secondary'>
              HOW IS THIS ACHIEVED?
            </Title>
            <Typography>
              We can track the open source software we use as dependencies within our projects build tool files (e.g., <strong>package.json</strong> for Node.js projects, <strong>pom.xml</strong> for Java projects).
              Once dependencies are parsed and the dependency version is resolved, this allows us to obtain accurate information as different versions of the same dependency may vary in vulnerabilities.
              Resolved dependency versions are then crossed referenced daily against a comphrensive vulnerability database which checks for known security issues.
              When a vulnerability is detected, information is displayed relating to how critical the vulnerability is and if safer patched version of the affected dependency is available.
            </Typography>
            </Box>
            <Box sx={{mb:3}}>
            <Title variant={{ xs: 'h5' }} color='text.secondary'>
              WHY IS THIS IMPORTANT?
            </Title>
            <Typography>
            Software supply chain security has become a critical concern for organisations and developers worldwide due to recent security events. Let's face it, the use of open source software allows for quicker and a smoother development experience. The software supply chain encompasses all the steps involved in the software development lifecycle, from code creation to deployment.
            Any weak link in the chain caused by software vulnerabilities which can be taken advantage of by attackers can lead to serious consequences such as data breaches, financial losses, and reputational damage.
            </Typography>
            </Box>
            <AboutCards/>
          </Container>
          </Box>
      </Box>
      <Footer/>
    </Box>
  )
}

export default About