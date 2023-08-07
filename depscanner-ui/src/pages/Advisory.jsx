import React from 'react'
import { Box } from '@mui/material'
import { useParams } from 'react-router-dom'
import Header from '../components/Header'
import Footer from '../components/Footer'
import axios from "axios";
import OsvData from '../components/OsvData'

const Advisory = () => {
    const { id } = useParams();
    const [osvData, setOsvData] = React.useState(null);

    React.useEffect(() => {
        const controller = new AbortController();

        const fetchOsvData = async () => {
            try {
                const res = await axios.get(`/OSV/${id}`, {
                    signal: controller.signal
                })
                setOsvData(res.data);
              } catch (err) {
                console.log(err);
              }
        }

    fetchOsvData();
  
    return () => {
        controller.abort();
    }   
    }, [])
    
  return (
    <Box>
        <Box sx={{minHeight: '100vh'}}>
            <Header/>
            {osvData && <OsvData osvData={osvData}/>}
        </Box>
        <Footer/>
    </Box>
  )
}

export default Advisory