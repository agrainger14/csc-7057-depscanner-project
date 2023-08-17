import React from 'react'
import { Box, Container, List, Typography, ListItem, ListItemText, Chip, Divider, Tooltip } from '@mui/material'
import { useParams, Link } from 'react-router-dom'
import Header from '../components/Header'
import Footer from '../components/Footer'
import { OidcSecure } from "@axa-fr/react-oidc";
import { useOidcIdToken } from '@axa-fr/react-oidc/dist/ReactOidc'
import { axiosDefault } from '../utils/axios'
import Gradient from '../assets/slanted-gradient.svg';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';
import HelpOutlinedIcon from '@mui/icons-material/HelpOutlined';
import AppPagination from '../components/AppPagination'
import LoadingSpinner from '../components/LoadingSpinner'

const Project = () => {
  const [project, setProject] = React.useState(null);
  const [currentPage, setCurrentPage] = React.useState(1);
  const [isLoading, setIsLoading] = React.useState(true);
  const { id } = useParams();
  const { idToken } = useOidcIdToken();

  const dependenciesPerPage = 10;
  const startIndex = (currentPage - 1) * dependenciesPerPage;
  const endIndex = startIndex + dependenciesPerPage;
  
    const handlePageChange = (newPage) => {
      setCurrentPage(newPage);
    }

    React.useEffect(() => {
      let isMounted = true;
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
          isMounted && setProject(res.data);
        } catch (err) {
          console.error(err)
        } finally {
          setIsLoading(false);
        }
      }

      fetchProjectData();
    
      return () => {
        isMounted = false;
        controller.abort();
      }
    }, [id])

  return (
    <OidcSecure>
      <Box>
      <Header/>
      <Box sx={{background: `url(${Gradient})`, backgroundSize: 'cover', backgroundPosition: 'center', minHeight: '100vh'}}>
      {isLoading ? (<LoadingSpinner/>) : (
        project && 
        <Container maxWidth={'md'} sx={{backgroundColor:'background.default', 
          display: 'flex',
          mt:2,
          flexDirection: 'column',
          border: '1px solid white',
          boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
          borderRadius: '8px', 
          minHeight: '100%',
        }}
        >
          {console.log(project)}
          <Typography variant="h4" gutterBottom sx={{mt:1}}>
            {project.name}
          </Typography>
          <Divider/>
          <Typography variant="body1" gutterBottom>
            {project.description}
          </Typography>
          <Typography variant="body2" gutterBottom>
            Project Uploaded at: {new Date(project.createdAt).toLocaleDateString()}
          </Typography>
          <Typography variant="body2" gutterBottom sx={{fontSize:'18px'}}>
            Project Dependencies: {project.projectDependenciesCount}
          </Typography>
          <Typography variant="body2" gutterBottom sx={{fontSize:'18px'}}>
            Vulnerable Dependencies: {project.vulnerableDependenciesCount}
          </Typography>
          <Divider/>
          <Typography variant="subtitle2" gutterBottom sx={{mt:1}}>
            Please find below your project dependencies and status, if a dependency is vulnerable click onto the affected dependency for more detail.
            Security vulnerabilities are always being reported, just because a dependency is marked as safe now does not mean it will always be safe.
          </Typography>
          <Typography variant="h6" gutterBottom sx={{mt:1}}>
            Dependencies:
          </Typography>
          <List>
            {project.dependencies.slice(startIndex,endIndex).map((dependency) => (
            <Link key={dependency.id} to={`/dependency/${encodeURIComponent(dependency.name)}/${dependency.system}/${dependency.version}`} style={{textDecoration:'none', color:'inherit'}}>
              <ListItem key={dependency.id}>
                <ListItemText
                  primary={dependency.name}
                  secondary={
                    <Typography
                      sx={{
                        color: dependency.isVulnerable ? 'error.main' : dependency.isVulnerable === null ? 'warning.main' : 'success.main',
                      }}
                    >
                      Version: {dependency.version}
                  </Typography>
                  }
                />
                {dependency.isVulnerable !== null ? (
                <Tooltip
                  key={dependency.id}
                  title={`${dependency.isVulnerable ? "This dependency may potentially be unsafe" : "There are no vulnerabilities currently detected for this dependency version"}`}
                  placement="top"
                >
                <Chip
                  label={dependency.isVulnerable ? 'Vulnerable' : 'Safe'}
                  color={dependency.isVulnerable ? 'error' : 'success'}
                  icon={dependency.isVulnerable ? <ErrorIcon /> : <CheckCircleIcon />}
                  sx={{ width: '140px'}}
                />
                </Tooltip>
                ) : 
                <Tooltip
                key={dependency.id}
                title='There is no data currently available for this dependency, it may be a private dependency or there is currently no data available.'
                placement="top"
                >
                <Chip
                  icon={<HelpOutlinedIcon/>}
                  label={'No data'}
                  color={'warning'}
                  sx={{ width: '140px'}}
                />
                </Tooltip>
                }
              </ListItem>
              </Link>
            ))}
            <AppPagination
              totalItems={project.dependencies.length}
                pageSize={dependenciesPerPage}
                currentPage={currentPage}
                onPageChange={handlePageChange}
            />
          </List>
        </Container>
        )}
        </Box>
        <Footer/>
      </Box>
    </OidcSecure>
  )
}

export default Project