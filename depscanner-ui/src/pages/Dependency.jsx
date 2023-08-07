import React from 'react'
import { Box } from '@mui/material'
import Header from '../components/Header'
import Footer from '../components/Footer'
import DependencyDetail from '../containers/DependencyDetail'
import { DependencyDataProvider } from '../context/DependencyDataContext'

const Dependency = () => {
  return (
    <Box>
      <Box sx={{minHeight: '100vh'}}>
        <Header/>
          <DependencyDataProvider>
            <DependencyDetail/>
          </DependencyDataProvider>
      </Box>
      <Footer/>
    </Box>
  )
}

export default Dependency
