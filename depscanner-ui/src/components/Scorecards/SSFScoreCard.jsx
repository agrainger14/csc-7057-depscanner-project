import React from 'react'
import { Box, Paper, Typography, Divider } from '@mui/material'
import ScoreAccordion from './ScoreAccordion'
import axios from 'axios';

const SSFScoreCard = ({ dependencyData }) => {
    const [scoreData, setScoreData] = React.useState(null);

    React.useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();

        const links = dependencyData.dependency[0].links;

        const SOURCE_REPO = links.find(link => link.label === 'SOURCE_REPO')?.url;

        const parts = SOURCE_REPO.split('/');
        const owner = parts[parts.length - 2];
        const repo_name = parts[parts.length - 1].split('.')[0];
        console.log(parts);

        const getScorecard = async () => {
        try {
            const res = await axios.get(`https://api.securityscorecards.dev/projects/github.com/${owner}/${repo_name}`, {
              signal: controller.signal
            });
                isMounted && setScoreData(res.data);
                console.log(res.data);
            } catch (err) {
                console.log(err);
            }
        }

        getScorecard();

        return () => {
            isMounted = false;
            controller.abort();
        } 
    }, [])

    return (
        <Box>
            {scoreData && 
                <Paper variant="outlined" sx={{ flex: 1, minHeight: '100px', position: 'relative' }}>
                    <Typography variant="h4" sx={{ ml: 2, mr: 2, mt: 2 }}>
                        Open SSF Score
                    </Typography>
                    <Divider sx={{ ml:2, mr:2 }} />
                    <Typography variant="body2" sx={{ ml: 2, mr: 2, mt:1 }}>
                        The Open Source Security Foundation (OpenSSF) is a collaborative effort spanning various industries, aimed at enhancing the security of open source software (OSS).
                        The Scorecard is a tool provided by OpenSSF that offers security health metrics for open source projects.
                    </Typography>
                    <ScoreAccordion scoreData={scoreData} />
                    <Divider sx={{ ml: 2, mr: 2 }} />
                </Paper>
            }
        </Box>
    )
}

export default SSFScoreCard