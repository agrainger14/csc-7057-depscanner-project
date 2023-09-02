import React from 'react'
import { Box } from '@mui/material'
import Header from '../components/Header/Header'
import Footer from '../components/Footer/Footer'
import AboutBackground from '../assets/background/cornered-stairs.svg';
import AboutContainer from '../containers/About/AboutContainer';

const About = () => {
  return (
    <Box>
      <Box sx={{minHeight: '100vh'}}>
          <Header/>
          <Box sx={{background: `url(${AboutBackground})`, backgroundSize: 'cover', backgroundPosition: 'center', minHeight: '125vh'}}>
              <AboutContainer/>
          </Box>
      </Box>
      <Footer/>
    </Box>
  )
}

export default About