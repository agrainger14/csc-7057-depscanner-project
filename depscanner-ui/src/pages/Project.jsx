import React from 'react'
import { Box } from '@mui/material'
import { useParams, useNavigate } from 'react-router-dom'
import Header from '../components/Header/Header'
import Footer from '../components/Footer/Footer'
import { OidcSecure } from "@axa-fr/react-oidc";
import { useOidcIdToken } from '@axa-fr/react-oidc/dist/ReactOidc'
import { axiosDefault } from '../utils/axios'
import ProjectContainer from '../containers/Project/ProjectContainer'
import LoadingSpinner from '../components/Loading/LoadingSpinner'

const Project = () => {
  const navigate = useNavigate();
  const [project, setProject] = React.useState(null);
  const [isLoading, setIsLoading] = React.useState(true);
  const { id } = useParams();
  const { idToken } = useOidcIdToken();

  React.useEffect(() => {
    setIsLoading(true);
    const controller = new AbortController();

    const fetchProjectData = async () => {
      try {
        const res = await axiosDefault.get(`/project/id/${id}`, { 
          headers: { 'Authorization': 'Bearer ' + idToken }
        }, 
        {
          signal: controller.signal
        });
        setProject(res.data);
      } catch (err) {
        navigate('/404');
        console.error(err)
       } finally {
        setIsLoading(false);
      }
    }

    fetchProjectData();
    
    return () => {
      controller.abort();
    }
  }, [id])

  return (
    <OidcSecure>
      <Box sx={{minHeight: '100vh'}}>
        <Header/>
          {isLoading ? (<LoadingSpinner/>) : (<ProjectContainer project={project}/>)}
      </Box>
      <Footer/>
    </OidcSecure>
  )
}

export default Project