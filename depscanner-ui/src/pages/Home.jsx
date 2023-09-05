import React from 'react'
import { Box } from '@mui/material'
import Header from '../components/Header/Header'
import Hero from '../containers/Hero/Hero'
import LowerHero from '../containers/Hero/LowerHero'
import Information from '../containers/Hero/Information'
import Footer from '../components/Footer/Footer'

const Home = () => {
  return (
    <Box sx={{minHeight: '100vh'}}>
      <Header />
      <Hero />
      <LowerHero />
      <Information />
      <Footer />
    </Box>
  )
}

export default Home