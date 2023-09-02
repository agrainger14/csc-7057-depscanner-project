import React from 'react'
import { Box, Paper, Typography, Container, Divider, Link} from '@mui/material'

const LinkCard = ({ dependencyData }) => {
  return (
    <Box>
        <Paper variant="outlined" sx={{ flex: 1, minHeight: '100px', position: 'relative' }}>
        <Container sx={{pt:2}}>
        <Typography variant="h4">
          Links
        </Typography>
        <Divider/>
        {dependencyData &&
          dependencyData.links.map((link) => (
            <Box key={link.label} sx={{ mt: 1, mb: 2 }}>
              <Typography variant="body2" sx={{fontWeight:700}}>{link.label}</Typography>
              <Link href={link.url.startsWith('git') ? link.url.substr(4) : link.url} target="_blank" rel="noopener noreferrer"
                  sx={{
                    wordWrap: 'break-word',
                    display: 'inline-block',
                    maxWidth: '100%',
                  }}
              >
                {link.url.startsWith('git') ? (
                  <Typography variant="body2">{link.url.substr(4)}</Typography>
                ) : (
                  <Typography variant="body2">{link.url}</Typography>
                )}
              </Link>
            </Box>
          ))
        }
        </Container>
        </Paper>
    </Box>
  )
}

export default LinkCard