import React from 'react'
import { Box } from '@mui/material'
import styled from '@emotion/styled'
import Background from '../assets/Wave.svg'

const LowerHeroBackground = styled(Box)({
    display: 'flex',
    alignItems: 'center',
    paddingTop: '160px',
    flexDirection: 'row',
    paddingBottom: '60px',
    backgroundSize: 'cover',
    justifyContent: 'center',
    backgroundRepeat: 'no-repeat',
    backgroundImage: `url(${Background})`,
    backgroundPosition: 'center',
})

const LowerHero = () => {
  return (
    <LowerHeroBackground />
  )
}

export default LowerHero