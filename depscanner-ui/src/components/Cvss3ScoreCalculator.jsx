import React from 'react'
import { Box } from '@mui/material';
import ScoreCard from './Scorecard';

const Cvss3ScoreCalculator = ({ score }) => {
    const [cvssScore, setCvssScore] = React.useState(null);

    React.useEffect(() => {
        const calculateScore = () => {
            const [_, AV, AC, PR, UI, S, C, I, A] = score.split('/');

            const confidentiality = C.split(':')[1] === 'H' ? 0.56 : C.split(':')[1] === 'L' ? 0.22 : 0;
            const integrity = I.split(':')[1] === 'H' ? 0.56 : I.split(':')[1] === 'L' ? 0.22 : 0;
            const availability = A.split(':')[1] === 'H' ? 0.56 : A.split(':')[1] === 'L' ? 0.22 : 0;
      
            const ISS = 1 - (1 - confidentiality) * (1 - integrity) * (1 - availability);

            const scopeUnchanged = 6.42 * ISS;
            const scopeChanged = 7.52 * (ISS - 0.029) - 3.25 * (ISS - 0.02);
            const impact = S.split(':')[1] === 'C' ? scopeChanged : scopeUnchanged;

            const AVValue = AV.split(':')[1] === 'N' ? 0.85 : 0.62;
            const ACValue = AC.split(':')[1] === 'L' ? 0.77 : AC.split(':')[1] === 'H' ? 0.44 : 0.56;
            const PRValue = PR.split(':')[1] === 'N' ? 0.85 : PR.split(':')[1] === 'H' ? 0.27 : 0.62;
            const UIValue = UI.split(':')[1] === 'N' ? 0.85 : 0.62;

            const exploitability = 8.22 * AVValue * ACValue * PRValue * UIValue;

            const baseScore =
              impact <= 0
                ? 0
                : S === 'C'
                ? Math.ceil(Math.min(1.08 * (impact + exploitability), 10) * 10) / 10
                : Math.ceil(Math.min(impact + exploitability, 10) * 10) / 10;

            setCvssScore(baseScore);
        };
    
        calculateScore();
      }, [score]);

    return (
        <Box>
            {cvssScore && 
            <ScoreCard cvss3Score={cvssScore}/>
            }
        </Box>
    );
}

export default Cvss3ScoreCalculator