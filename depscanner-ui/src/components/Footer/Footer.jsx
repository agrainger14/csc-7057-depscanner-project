import React from 'react'
import { Box, Typography, Link } from '@mui/material'
import styled from '@emotion/styled'
import GitHubIcon from '../../assets/background/iconmonstr-github-1.svg';
import DepsDevIcon from '../../assets/background/svgviewer-output.svg';

const PageContainer = styled(Box)({
  display: 'flex',
  flexDirection: 'column',
  marginTop: 'auto',
  
})

const HomeFooterBox = styled(Box)({
    width: '100%',
    display: 'flex',
    alignItems: 'center',
    flexDirection: 'column',
    justifyContent: 'center',
    backgroundColor: '#003375',
})

const HomeFooter = styled(Box)({
    width: '100%',
    display: 'flex',
    maxWidth: '1414px',
    alignItems: 'center',
    paddingTop: '12px',
    paddingLeft: '24px',
    paddingRight: '24px',
    paddingBottom: '12px',
    flexDirection: 'row',
    justifyContent: 'space-between',
    color: 'black',
})

const Links = styled(Box)({
    display: 'flex',
    alignItems: 'flex-start',
    flexDirection: 'row',
})

const Footer = () => {
  return (
    <PageContainer>
      <HomeFooterBox>
        <HomeFooter>
          <Links>
            <Link href='https://github.com/advisories'>
              <img src={GitHubIcon} height={24} width={24} style={{marginRight: '16px'}} alt="GitHubIcon" />
            </Link>
            <Link href='https://deps.dev'>
              <img src={DepsDevIcon} height={24} width={24} style={{marginRight: '16px'}} alt="DepsDevIcon" />
            </Link>
          </Links>
          <Box>
            <Box style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end'}}>
              <Typography variant="overline">
                DEPSCANNER, {`${new Date().getFullYear()}`}.
              </Typography>
              <Box>
                <Typography style={{fontSize:'14px'}} variant='body1'>
                All data obtained from the "Open Source Insights Project" by Google.
                </Typography>
              </Box>
            </Box>
          </Box>
        </HomeFooter>
      </HomeFooterBox>
    </PageContainer>
  )
}

export default Footer