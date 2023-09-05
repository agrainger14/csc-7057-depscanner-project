import React from 'react'
import { Box, Paper, Typography, Container, Divider} from '@mui/material'

const PublishedCard = ({ dependencyData }) => {
  return (
    <Box sx={{pt: {xs: 2, sm: 0}}}>
      <Paper variant="outlined" sx={{ flex: 1, minHeight: '100px', position: 'relative' }}>
        <Container sx={{pt:2}}>
          <Typography variant="h4">
            Date Data Published
          </Typography>
          <Divider/>
          {dependencyData &&
            <Typography variant="subtitle2" sx={{mt:1}}>
              {new Date(dependencyData.publishedAt).toLocaleString('en-GB', 
                {
                  day: 'numeric',
                  month: 'long',
                  year: 'numeric',
                  hour: 'numeric',
                  minute: 'numeric'
                }
              )}
            </Typography>
          } 
        </Container>
      </Paper>
    </Box>
  )
}

export default PublishedCard