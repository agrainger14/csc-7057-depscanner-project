import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import theme from './utils/theme'
import { BrowserRouter } from 'react-router-dom'
import { ThemeProvider } from '@emotion/react'
import { CssBaseline } from '@mui/material'
import { OidcProvider } from '@axa-fr/react-oidc';

import './index.css'

const configuration = {
  authority: "http://keycloak:8181/realms/depscanner",
  client_id: "depscanner-client",
  redirect_uri: window.location.origin + "/authentication/callback",
  silent_redirect_uri: window.location.origin + "/authentication/silent-callback",
  post_logout_redirect_uri: "http://localhost:5173",
  response_type: 'code',
  scope: "openid profile offline_access",
  service_worker_relative_url:'/OidcServiceWorker.js',
  service_worker_only: true,
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <OidcProvider configuration={configuration}>
      <BrowserRouter>
        <ThemeProvider theme={theme}>
          <CssBaseline/>
            <App />
        </ThemeProvider>
      </BrowserRouter>
    </OidcProvider>
  </React.StrictMode>
)
