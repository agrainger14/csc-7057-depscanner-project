import React from 'react'
import { Box } from '@mui/material'
import Header from '../components/Header'
import Tabs from '../containers/DashboardTabs'
import Footer from '../components/Footer'
import { OidcSecure } from "@axa-fr/react-oidc";

const Dashboard = () => {
  return (
    <OidcSecure>
      <Box sx={{minHeight: '100vh'}}>
        <Header/>
          <Tabs/>
      </Box>
      <Footer />
    </OidcSecure>
  )
}

export default Dashboard