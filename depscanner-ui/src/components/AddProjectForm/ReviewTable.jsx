import React from 'react'
import { Box, TableContainer, Table, TableHead, TableRow, TableCell, TableBody, Typography, Select, FormControl, InputLabel, MenuItem, Button } from '@mui/material'
import { axiosDefault } from '../../utils/axios';

const ReviewTable = ({ projectDependencies, setProjectDependencies, setEnableNext }) => {
    const dependenciesCount = projectDependencies.length;
    const [dependencyVersions, setDependencyVersions] = React.useState(null);

    React.useEffect(() => {
        setEnableNext(false);
        checkVersionsSet(projectDependencies);
    }, [])

    const fetchVersions = async ({ dependency }) => {
        setDependencyVersions(null);
        const dependencyName = dependency.name;
        const dependencySystem = dependency.system;
        const controller = new AbortController();

        try {
            await axiosDefault.get(`/vuln/versions?name=${dependencyName}&system=${dependencySystem}`, {
                signal: controller.signal
            })
            setDependencyVersions(res.data.versions)
        } catch (err) {
            console.log(err);
        }

        return () => controller.abort();
    }

    const handleDependencyVersionChange = (e, index) => {
        //clone the current dependencies
        const updatedDependencies = [...projectDependencies];
        //update version at index
        updatedDependencies[index].version = e.target.value;
        //set new dependencies
        setProjectDependencies(updatedDependencies);
        checkVersionsSet(projectDependencies);
    }

    const handleDeleteDependency = (index) => {
        //clone the current dependencies
        const updatedDependencies = [...projectDependencies];
        //remove the dependency at the specified index
        updatedDependencies.splice(index, 1);
        //set new dependencies
        setProjectDependencies(updatedDependencies);
        checkVersionsSet(updatedDependencies);
      };

    const checkVersionsSet = (dependencies) => {
        const allVersionsSet = dependencies.every(dependency => dependency.version);
        allVersionsSet ? setEnableNext(true) : setEnableNext(false);
    }

  return (
    <Box>
        <Box sx={{mt:2, display: 'flex', justifyContent:'center' }}>
            <Box sx={{display: 'flex', flexDirection:'column', alignItems: 'center'}}>
            <Typography variant="body1">
                On review of your build tool file {dependenciesCount} dependencies were detected!
            </Typography>
            <Typography variant="body2">
                Please review to ensure the versions have been set.
                You are unable to proceed to the next step unless all dependencies have a version.
            </Typography>
            </Box>
        </Box>
        <TableContainer sx={{ mt: 2, mb:2, minHeight: '70vh'}}>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>
                            <Typography variant="body1">
                                Dependency Name
                            </Typography>
                        </TableCell>
                        <TableCell>
                            <Typography variant="body1">
                                Version Detected
                            </Typography>
                        </TableCell>
                        <TableCell>
                            <Typography variant="body1">Actions</Typography>
                        </TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    { projectDependencies && projectDependencies.map((dependency, index) => {
                        return (
                            <TableRow key={index}>
                                <TableCell>{dependency.name}</TableCell>
                                { dependency?.version ? (
                                    <TableCell>{dependency.version}
                                    </TableCell>) 
                                    : 
                                    (<TableCell>
                                        <FormControl>
                                            <InputLabel>
                                                Select Version
                                            </InputLabel>
                                            <Select 
                                                MenuProps={{
                                                    disableScrollLock: true,
                                                  }}
                                                value={dependency.version || ''}
                                                sx={{ width: 300 }} 
                                                onOpen={() => fetchVersions({dependency})}
                                                onChange={(e) => handleDependencyVersionChange(e, index)}
                                            >
                                                {dependencyVersions && dependencyVersions.map((version, index) => {
                                                    return(
                                                        <MenuItem key={index} value={version.versionKey.version}>
                                                            {version?.isDefault === 'true' ? 
                                                                `${version.versionKey.version} (Default)`
                                                                : `${version.versionKey.version}`
                                                            }
                                                        </MenuItem>
                                                    )
                                                })}
                                            </Select>
                                        </FormControl>
                                    </TableCell>
                                )}
                                <TableCell>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={() => handleDeleteDependency(index)}
                                >
                                    Delete
                                </Button>
                                </TableCell>
                            </TableRow>
                        )
                    })}
                </TableBody>
            </Table>
        </TableContainer>
    </Box>
  )
}

export default ReviewTable