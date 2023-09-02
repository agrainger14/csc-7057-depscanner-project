import React from 'react'
import AboutCards from '../../containers/About/AboutCards';
import Title from '../../utils/title';
import { Box, Container, Typography, Divider} from '@mui/material'

const AboutContainer = () => {
  return (
    <Container maxWidth={'lg'} 
        sx={{
            backgroundColor:'background.default', 
            mb:2, 
            mt:2, 
            border: '1px solid white',
            boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
            borderRadius: '8px' 
        }}
    >
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
        <Box sx={{mb:3}}>
            <Title variant={{ xs: 'h5' }} color='text.secondary'>
            WHAT INFORMATION DOES DEPSCANNER PROVIDE?
            </Title>
            <Typography>
            DepScanner provides advisory information relating to dependencies. Not only does it provide information relating to a vulnerable dependency, it can also show if any dependencies within a dependency are also vulnerable. A breakdown of the vulnerable dependency is provided as well as remedial actions if available.
            Aswell as this, DepScanner also provides information such as licensing directly from the dependency metadata. The license data provided is not intended to be legal advice, and should independently be verified for your own needs.
            </Typography>
        </Box>
        <AboutCards/>
    </Container>
  )
}

export default AboutContainer