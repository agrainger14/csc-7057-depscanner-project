import React from 'react'
import { Box, TableContainer, Table, TableHead, TableRow, TableCell, TableBody, Typography, Select, FormControl, InputLabel, MenuItem, Button, Tooltip } from '@mui/material'
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
            const res = await axiosDefault.get(`/vuln/versions?name=${dependencyName}&system=${dependencySystem}`, {
                signal: controller.signal
            })
            const sortedVersions = sortVersionsByVersionKey(res.data.versions);
            setDependencyVersions(sortedVersions);
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

    function sortVersionsByVersionKey(versions) {
        return versions.sort((a, b) => {
            const versionA = a.versionKey.version.split('.').map(Number);
            const versionB = b.versionKey.version.split('.').map(Number);
    
            for (let i = 0; i < Math.max(versionA.length, versionB.length); i++) {
                const partA = versionA[i] || 0;
                const partB = versionB[i] || 0;
    
                if (partA !== partB) {
                    return partB - partA;
                }
            }
            return 0;
        });
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
                        {projectDependencies && projectDependencies[0].system === "NPM" &&
                        <TableCell>
                            <Typography variant="body1">
                                devDependency
                            </Typography>
                        </TableCell>
                        }
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
                                {dependency.system === "NPM" &&
                                <TableCell>
                                    {dependency.isDevDependency ? "Yes" : dependency.isDevDependency ===  null ? "N/A" : "No"}
                                </TableCell>
                                }
                                <TableCell>
                                    <Tooltip
                                        title="Project must have at least one dependency"
                                        placement="top"
                                        disableHoverListener={projectDependencies.length !== 1}
                                    >
                                        <span>
                                            <Button
                                                variant="contained"
                                                color="primary"
                                                onClick={() => handleDeleteDependency(index)}
                                                disabled={projectDependencies.length === 1}
                                            >
                                                Delete
                                            </Button>
                                        </span>
                                    </Tooltip>
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