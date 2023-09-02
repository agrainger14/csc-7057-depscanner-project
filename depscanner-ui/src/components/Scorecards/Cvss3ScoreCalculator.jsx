import React from 'react'
import { Box } from '@mui/material';
import ScoreCard from './Scorecard';

const Cvss3ScoreCalculator = ({ score }) => {
    const [cvssScore, setCvssScore] = React.useState(null);

    const calculateScore = () => {
        if (score === 0) {
            return 0;
        }

        const [_, AV, AC, PR, UI, S, C, I, A] = score.split('/');

        const confidentiality = C.split(':')[1] === 'H' ? 0.56 : C.split(':')[1] === 'L' ? 0.22 : 0;
        const integrity = I.split(':')[1] === 'H' ? 0.56 : I.split(':')[1] === 'L' ? 0.22 : 0;
        const availability = A.split(':')[1] === 'H' ? 0.56 : A.split(':')[1] === 'L' ? 0.22 : 0;
      
        const BaseScore = 1 - (1 - confidentiality) * (1 - integrity) * (1 - availability);

        const scopeUnchanged = 6.42 * BaseScore;
        const scopeChanged = 7.52 * (BaseScore - 0.029) - 3.25 * (BaseScore - 0.02);
        const impact = S.split(':')[1] === 'C' ? scopeChanged : scopeUnchanged;

        //attack vector
        const AVValue = AV.split(':')[1] === 'N' ? 0.85 : AV.split(':')[1] === 'A' ? 0.62 : AV.split(':')[1] === 'L' ? 0.55 : 0.2;

        //attack complexity
        const ACValue = AC.split(':')[1] === 'L' ? 0.77 : 0.44;

        //privilege required
        const PRValue = PR.split(':')[1] === 'N' ? 0.85 : PR.split(':')[1] === 'L' && S.split(':')[1] === 'C' ? 0.68 : PR.split(':')[1] === 'L' ? 0.62 
        : PR.split(':')[1] === 'H' && S.split(':')[1] === 'C' ? 0.50 : 0.27;

        //user interaction
        const UIValue = UI.split(':')[1] === 'N' ? 0.85 : 0.62;

        const exploitability = 8.22 * AVValue * ACValue * PRValue * UIValue;

        return impact <= 0
            ? 0
            : S === 'C'
            ? Math.ceil(Math.min(1.08 * (impact + exploitability), 10) * 10) / 10
            : Math.ceil(Math.min(impact + exploitability, 10) * 10) / 10;
    };

    React.useEffect(() => {
        return () => { 
            setCvssScore(calculateScore());
        };
    }, [score]);

    return (
        <Box>
            <ScoreCard cvss3Score={cvssScore} />
        </Box>
    );
}

export default Cvss3ScoreCalculator