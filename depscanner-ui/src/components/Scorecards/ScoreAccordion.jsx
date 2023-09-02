import React from 'react';
import { Accordion, AccordionDetails, AccordionSummary, Typography, Box, List, ListItem, Link } from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

const ScoreAccordion = ({ scoreData }) => {
  return (
    <Box>
        {scoreData && (
            <Box sx={{ml:2, mb:2}}>
                <Typography variant="body1" sx={{mt:1, fontWeight:'700'}}>
                    Repository: 
                </Typography>
                <Link href={scoreData.repo.name}>
                    <Typography variant="body2">
                        {scoreData.repo.name}
                    </Typography>
                </Link>
                <Typography variant="h6" sx={{mt:2, fontWeight:'700'}}>Score:</Typography>
                <Typography variant="body2" sx={{fontSize:'28px'}}>{scoreData.score} / 10</Typography>
            </Box>
        )}
      {scoreData &&
        scoreData.checks.map((check, index) => {
          if (check.name === 'Vulnerabilities') {
            return null;
          }

          return (
            <Accordion key={index}>
              <AccordionSummary expandIcon={<ExpandMoreIcon />} id={`accordion-${check.name}`}>
                <Typography variant="subtitle1">
                  {check.name} - Score: {check.score}
                </Typography>
              </AccordionSummary>
              <AccordionDetails>
                <Typography variant="body2" sx={{ mb: 2 }}>
                  {check.documentation.short}
                </Typography>
                <Typography variant="body1" sx={{ fontWeight: 700 }}>
                  Reasoning:
                </Typography>
                <Typography>{check.reason}</Typography>
                {check.details && (
                  <List>
                    {check.details.map((detail, index) => (
                      <ListItem key={index}>
                        <Typography variant="body2">
                            {detail}
                        </Typography>
                    </ListItem>
                    ))}
                  </List>
                )}
                <Typography variant="body2" sx={{ mt: 2 }}>
                  More information:
                </Typography>
                <Link href={check.documentation.url}>
                  <Typography variant="body2">{check.documentation.url}</Typography>
                </Link>
              </AccordionDetails>
            </Accordion>
          );
        })}
    </Box>
  );
};

export default ScoreAccordion;