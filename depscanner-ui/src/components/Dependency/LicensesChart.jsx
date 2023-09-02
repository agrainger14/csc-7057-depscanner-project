import React from 'react'
import { Box, Paper, Typography, Divider, IconButton, Tooltip } from '@mui/material'
import InfoIcon from '@mui/icons-material/InfoSharp';

const LicensesChart = ({ dependencyData }) => {
    const [selfLicenses, setSelfLicenses] = React.useState(null);
    const [relatedLicenses, setRelatedLicenses] = React.useState(null);
    const [licensesCount, setLicensesCount] = React.useState(null);

    React.useEffect(() => {
        const selfLicensesMap = {};
        const relatedLicenseMap = {};

        dependencyData.dependency.forEach(dependency => {
          dependency.licenses.forEach(license => {
            if (dependency.relation === 'SELF') {
                selfLicensesMap[license] = 1;
            } else {
                relatedLicenseMap.hasOwnProperty(license) ? relatedLicenseMap[license]++ : relatedLicenseMap[license] = 1;
            }
          });
        });

        const sortedRelatedLicenseArray = Object.entries(relatedLicenseMap).sort((a, b) => b[1] - a[1]);
        const sortedRelatedLicenseMap = Object.fromEntries(sortedRelatedLicenseArray);
        setSelfLicenses(selfLicensesMap)
        setRelatedLicenses(sortedRelatedLicenseMap);
        setLicensesCount(Object.values(sortedRelatedLicenseMap).reduce((total, count) => total + count, 0));
    }, [])

  return (
    <Paper variant="outlined" sx={{ flex: 1, minHeight: '100px', position: 'relative' }}>
        <Box sx={{
            position: 'absolute',
            top: 10,
            right: 20,
            width: '40px',
            height: '40px',
            backgroundColor: 'transparent',
            color: 'white',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            borderRadius: '20%',
            borderColor: 'white',
            borderWidth: 1,
            borderStyle: 'solid'
            }}
        >
        {relatedLicenses && 
            <Typography variant='subtitle2'>
                {Object.keys(relatedLicenses).length}
            </Typography>
        }
        </Box>
        <Box sx={{display:'flex', flexDirection:'row'}}>
        <Typography variant='h4' sx={{ ml:2, mt:2 }}>
            Licenses
        </Typography>
        <Tooltip
            title={"Open source software licenses govern how others - besides the originator can use, modify, or distribute software code. They grant other users the permission and rights to use or repurpose the code for new applications or to include the code in other projects."}
        >
        <IconButton sx={{mt:1}}>
            <InfoIcon/>
        </IconButton>
        </Tooltip>
        </Box>
        <Divider sx={{ ml:2, mr:2 }} />
        <Box sx={{ display: 'flex', alignItems: 'center', mt:2, ml:2}}>
            <Typography variant='subtitle2'>
                Licenses:
            </Typography>
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center', ml: 2}}>
            {selfLicenses && 
                Object.keys(selfLicenses).map(license => (
                <Typography variant='subtitle1' key={license} sx={{ mr:2 }}>
                    {license}
                </Typography>
            ))}
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center', mt:2, ml:2 }}>
            <Typography variant='subtitle2'>
                Dependency Licenses:
            </Typography>
        </Box>
        <Divider sx={{ ml:2, mr:2 }} />
        <Box sx={{ display: 'flex', flexDirection: 'column', ml: 2, mt: 1, mb: 1 }}>
        {relatedLicenses && Object.keys(relatedLicenses).length === 0  && <Typography>None!</Typography>}
            {relatedLicenses &&
                Object.keys(relatedLicenses).map(license => (
                <Box key={license} sx={{
                    display: 'flex',
                    flexDirection: 'row',
                    alignItems: 'center',
                    ml: 2,
                    mt: 1,
                    }}
                >
                    <Typography variant='subtitle1' sx={{ flex: 1 }}>
                        {license}
                    </Typography>
                    <Typography variant='subtitle2' sx={{ flex: 0.2 }}>
                        {relatedLicenses[license]}
                    </Typography>
                    <Box sx={{
                        flexGrow: 7,
                        mr:2,
                        display: 'flex',
                        alignItems: 'center',
                        height: '5px',
                        backgroundColor: '#CCCCCC',
                        borderRadius: '2.5px',
                        }}
                    >
                        <Box sx={{
                            height: '100%',
                            width: `${(relatedLicenses[license] / licensesCount) * 100}%`, 
                            backgroundColor: '#FFB100',
                            borderRadius: '2.5px',
                        }}
                        />
                    </Box>
                </Box>
            ))}
        </Box>
    </Paper>
  )
}

export default LicensesChart