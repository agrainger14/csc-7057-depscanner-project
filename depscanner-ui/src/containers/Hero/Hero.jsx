import React from 'react'
import { Box, Typography, Button, Container} from '@mui/material'
import { Link } from 'react-router-dom';
import styled from '@emotion/styled';
import HeroBackground from '../../assets/background/AnimatedShape.svg'
import { useOidc } from '@axa-fr/react-oidc';

const HomeHero = styled(Box)({ 
    width: '100%',
    display: 'flex',
    alignItems: 'center',
    paddingTop: '80px',
    flexDirection: 'row',
    paddingBottom: '40px',
    backgroundSize: 'cover',
    justifyContent: 'center',
    backgroundRepeat: 'no-repeat',
    backgroundImage: `url(${HeroBackground})`,
    backgroundPosition: 'center',
});

const HeroContainer = styled(Container)({
    width: '100%',
    display: 'flex',
    maxWidth: '1414px',
    alignItems: 'center',
    paddingLeft: '24px',
    marginBottom: '20px',
    paddingRight: '24px',
    flexDirection: 'row',
    paddingBottom: '20px',
    justifyContent: 'center',
})

const HeroText = styled(Box)({
    width: '65%',
    display: 'flex',
    alignItems: 'center',
    flexDirection: 'column',    
})

const Hero = () => {
  const { login } = useOidc();

  return (
    <HomeHero>
      <HeroContainer>
        <HeroText>
          <Typography variant='h6'
            sx={{
              textAlign: 'center',
              color: 'text.secondary',
              marginBottom: 1,
            }}
          >
            Dependency Insights and Monitoring
          </Typography>
          <Typography variant="h1" sx={{textAlign: 'center', marginBottom: '40px'}}>
            What is the DepScanner Project?
          </Typography>
          <Typography variant='h4' 
            sx={{
              textAlign: 'center',
              marginBottom: '40px',
              color: 'text.secondary',
            }}
          >
          A tool designed to monitor your project dependencies and notify you about the latest security vulnerabilities.
          </Typography>
          <Box
            sx={{
              display: 'flex',
              justifyContent: 'center',
              textAlign: 'center',
            }}
          > 
            <Link to="/about">
              <Button
                variant="contained"
                sx={{
                  paddingTop: '16px',
                  paddingLeft: '80px',
                  paddingRight: '80px',
                  paddingBottom: '16px',
                  borderRadius: '30px',
                  backgroundColor: 'black',
                  '&:hover': {
                    backgroundColor: '#002958',
                    borderColor: 'black',
                  },
                }}
              >
                LEARN MORE
              </Button>
            </Link>
            <Button onClick={() => login('/dashboard')}
              variant="outlined"
              sx={{
                paddingTop: '16px',
                paddingLeft: '80px',
                paddingRight: '80px',
                paddingBottom: '16px',
                borderRadius: '30px',
                borderColor: 'black',
                color: 'white',
                marginLeft: '16px',
                '&:hover': {
                  backgroundColor: 'black',
                },
              }}
            >
              LAUNCH
            </Button>
          </Box>
        </HeroText>
      </HeroContainer>
    </HomeHero>
  )
}

export default Hero