import { Box, Typography, Grid } from '@mui/material'
import React from 'react'
import styled from '@emotion/styled'
import Pointer from '../../assets/background/dots-bullet.svg'
import CheckMark from '../../assets/background/check-mark-regular.svg'
import Symbol from '../../assets/background/no-symbol-regular.svg'

const InformationStyle = styled(Box)({
    width: '100%',
    maxWidth: '1414px',
    display: 'flex',
    margin: 'auto',
    marginBottom: '80px',
    paddingLeft: '8px',
    alignItems: 'center',
    flexDirection: 'column',
})

const InformationBox = styled(Box)({
    width: '50%',
    display: 'flex',
    alignItems: 'center',
    marginBottom: '60px',
    flexDirection: 'column',
})

const FactsCard = styled(Box)({
    display: 'flex',
    alignItems: 'flex-start',
    paddingX: '16px',
    marginRight: '30px',
})

const IconBox = styled(Box)({
    display: 'flex',
    padding: '8px',
    alignItems: 'center',
    marginRight: '24px',
    borderRadius: '50%',
    backgroundColor: 'white',
    flexDirection: 'column',
})

const Information = () => {
  return (
    <InformationStyle id='about'>
        <InformationBox>
            <Typography variant='h3' sx={{textAlign: 'center', color: 'text.secondary', fontWeight:'400'}}>
                Why is it important that I know about vulnerabilities?
            </Typography>
        </InformationBox>
        <Grid container spacing={8}>
            <Grid item sm={12} md={6} lg={4}>
                <FactsCard>
                    <IconBox>
                        <img src={Pointer} height={55} width={55} alt="Pointer" />
                    </IconBox>
                    <Box>
                        <Typography variant="h5" sx={{ fontWeight: '700', lineHeight: '1.55', textTransform: 'none', textDecoration: 'none', mt: 2 }}>
                        REPUTATION
                        </Typography>
                        <Typography variant="subtitle2" sx={{mt:2}}>
                        Security breaches can have a severe impact on the trust users have in your software or service. 
                        By actively monitoring and mitigating vulnerabilities in dependencies, you demonstrate a commitment to security, which enhances trust and protects your reputation.
                        </Typography>
                    </Box>
                </FactsCard>
            </Grid>
            <Grid item sm={12} md={6} lg={4}>
                <FactsCard>
                    <IconBox>
                        <img src={Symbol} height={55} width={55} alt="Pointer" />
                    </IconBox>
                    <Box>
                        <Typography variant="h5" sx={{ fontWeight: '700', lineHeight: '1.55', textTransform: 'none', textDecoration: 'none', mt:2 }}>
                        PROTECTION
                        </Typography>
                        <Typography variant="subtitle2" sx={{mt:2}}>
                        Security vulnerabilities in dependencies can serve as entry points for attackers to compromise the overall security of your software. 
                        By staying informed about these vulnerabilities, you can proactively address them and minimise the risk of an attack.
                        </Typography>
                    </Box>
                </FactsCard>
            </Grid>
            <Grid item sm={12} md={6} lg={4}>
                <FactsCard>
                    <IconBox>
                        <img src={CheckMark} height={55} width={55} alt="Pointer" />
                    </IconBox>
                    <Box>
                        <Typography variant="h5" sx={{ fontWeight: '700', lineHeight: '1.55', textTransform: 'none', textDecoration: 'none', mt:2 }}>
                            COMPLIANCE
                        </Typography>
                        <Typography variant="subtitle2" sx={{mt:2}}>
                            Industry and regulatory frameworks have specific security standards that must be adhered to. Being aware of vulnerabilities in your dependencies helps you meet these compliance requirements and demonstrate a commitment to security.
                        </Typography>
                    </Box>
                </FactsCard>
            </Grid>
        </Grid>
    </InformationStyle>
  )
}

export default Information