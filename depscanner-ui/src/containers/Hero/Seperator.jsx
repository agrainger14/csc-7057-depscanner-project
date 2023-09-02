import React from 'react'
import styled from '@emotion/styled';
import { Box } from '@mui/material';
import Gradient from '../assets/slanted-gradient.svg'

const SeperatorStyle = styled(Box)({ 
  width: '100%',
  height: 'auto',
  display: 'flex',
  alignItems: 'center',
  paddingBottom: '50%',
  backgroundSize: 'cover',
  backgroundRepeat: 'no-repeat',
  backgroundImage: `url(${Gradient})`,
});

const Seperator = () => {
  return (
    <SeperatorStyle/>
  )
}

export default Seperator