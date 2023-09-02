import React from 'react'
import { Button, Box } from '@mui/material'
import { useNavigate } from 'react-router-dom'

const GoBackButton = () => {
    const navigate = useNavigate();

    const goBack = () => {
      navigate(-1)
    }

    return (
        <Box>
            <Button onClick={goBack} variant="outlined" sx={{mt:5}}>Go Back</Button>	
        </Box>
    )
}

export default GoBackButton