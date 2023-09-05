import React from 'react'
import { Box, Typography } from '@mui/material'
import Header from '../components/Header/Header'
import Footer from '../components/Footer/Footer'
import { Link } from 'react-router-dom'

const NotFound = () => {
  return (
    <Box>
      <Box sx={{minHeight: '100vh'}}>
        <Header/>
        <Box sx={{ display: 'flex', flexDirection:'column', justifyContent:'center', alignItems:'center', mt:10, textAlign:'center' }}>
          <Typography variant="h2">
            404 Error
          </Typography>
          <Typography variant="h3" sx={{mt:2}}>
           Oops! It looks like no information has been found!
          </Typography>
          <Typography variant="h4">
            The information you requested cannot be found. The information you requested is either missing data, or does not exist.
          </Typography>
          <Box sx={{display: 'flex', flexDirection:'column', alignItems:'center'}}>
            <Typography variant="h4" sx={{mt:5,mb:2}}>
              Here are some useful links...
            </Typography>
            <Link to='/'>Home</Link>
            <Link to='/about'>About</Link>
            <Link to='/dashboard'>Dashboard</Link>
          </Box>
        </Box>
      </Box>
      <Footer/>
    </Box>
  )
}

export default NotFound