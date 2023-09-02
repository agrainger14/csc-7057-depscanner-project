import React from 'react'
import { Box } from '@mui/material'
import Header from '../components/Header/Header'
import Tabs from '../containers/Dashboard/DashboardTabs'
import Footer from '../components/Footer/Footer'
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