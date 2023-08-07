import React from 'react'
import { Box } from '@mui/material'
import Header from '../components/Header'
import Hero from '../containers/Hero'
import LowerHero from '../containers/LowerHero'
import Information from '../containers/Information'
import Footer from '../components/Footer'

const Home = () => {

  return (
    <Box sx={{minHeight: '100vh'}}>
      <Header/>
      <Hero/>
      <LowerHero />
      <Information/>
      <Footer/>
    </Box>
  )
}

export default Home