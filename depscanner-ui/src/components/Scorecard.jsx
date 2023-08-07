import React from 'react';
import { Box, Typography } from '@mui/material';

const ScoreCard = ({ cvss3Score }) => {
  let backgroundColour = '';
  let borderColour = '';
  let textColour = '';
  let text = '';

    switch (true) {
      case cvss3Score >= 9.0:
        backgroundColour = 'red';
        textColour = 'white';
        borderColour = 'black';
        text = `${cvss3Score} CRITICAL`;
        break;
      case cvss3Score >= 7.0:
        backgroundColour = 'transparent';
        textColour = 'red';
        borderColour = 'red';
        text = `${cvss3Score} High`;
        break;
      case cvss3Score >= 4.0:
        backgroundColour = 'transparent';
        textColour = 'orange';
        borderColour = 'orange';
        text = `${cvss3Score} Moderate`;
        break;
      case cvss3Score >= 0.1:
        backgroundColour = 'transparent';
        textColour = 'yellow';
        borderColour = 'yellow';
        text = `${cvss3Score} Low`;
        break;
      default:
        backgroundColour = 'transparent';
        textColour = 'green';
        borderColour = 'green';
        text = `${cvss3Score} None`;
        break;
      }
  
  return (
    <Box
      component="button"
      sx={{
        variant: "h6",
        display: 'inline-block',
        mr: 2,
        fontWeight: 700,
        backgroundColor: backgroundColour,
        color: textColour,
        border: `2px solid ${borderColour}`,
        borderRadius: '4px',
      }}
      disabled
    >
      <Typography variant="body1">{text}</Typography>
    </Box>
  );
};

export default ScoreCard;