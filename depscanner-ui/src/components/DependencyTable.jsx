import React from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Typography, Container, TextField, Grid, Hidden, Link, TableSortLabel } from '@mui/material';

const DependencyTable = ({ dependencyData }) => {
  const [searchTerm, setSearchTerm] = React.useState('');
  const [orderBy, setOrderBy] = React.useState('');
  const [order, setOrder] = React.useState('asc'); 
  
  const handleSort= (column) => {
    const isAsc = orderBy === column && order === 'asc';
    setOrder(isAsc ? 'desc' : 'asc');
    setOrderBy(column);
  };

  function stableSort(array, comparator) {
    const stabilizedThis = array.map((el, index) => [el, index]);
    stabilizedThis.sort((a, b) => {
      const order = comparator(a[0], b[0]);
      if (order !== 0) {
        return order;
      }
      return a[1] - b[1];
    });
    return stabilizedThis.map((el) => el[0]);
  }

  function getComparator(order, orderBy) {
    return order === 'desc'
      ? (a, b) => descendingComparator(a, b, orderBy)
      : (a, b) => -descendingComparator(a, b, orderBy);
  }

  function descendingComparator(a, b, orderBy) {
    const aValue = getValueByProperty(a, orderBy);
    const bValue = getValueByProperty(b, orderBy);

    if (aValue < bValue) {
      return -1;
    }
    if (aValue > bValue) {
      return 1;
    }
    return 0;
  }

  function getValueByProperty(object, property) {
    const properties = property.split('.');
    let value = object;
    
    for (const prop of properties) {
      value = value[prop];
    }
    
    return value;
  }

  const handleSearch = event => {
    setSearchTerm(event.target.value);
  };

  const filteredDependencies = dependencyData && stableSort(dependencyData.dependency, getComparator(order, orderBy)).slice().filter(dependency =>
    dependency.versionKey.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    dependency.versionKey.version.toLowerCase().includes(searchTerm.toLowerCase()) ||
    dependency.relation.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
      <Container maxWidth={'xl'} sx={{mt:2, mb:2}}>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <TextField
              sx={{
                width: '100%',
                textAlign: 'center',
              }}
              label="Filter dependencies by name, license, relation, security advisory etc."
              variant="outlined"
              value={searchTerm}
              onChange={handleSearch}
            />
          </Grid>
          <Grid item xs={12}>
          <TableContainer variant="outlined" component={Paper}>
          <Table aria-label="dependency">
              <TableHead>
                <TableRow>
                  <TableCell>
                  <TableSortLabel
                    active={orderBy === 'versionKey.name'}
                     direction={orderBy === 'versionKey.name' ? order : 'asc'}
                      onClick={() => handleSort('versionKey.name')}
                  >
                      Dependency
                    </TableSortLabel>
                  </TableCell>
                  <TableCell>
                  <TableSortLabel
                    active={orderBy === 'advisoryDetail'}
                     direction={orderBy === 'advisoryDetail' ? order : 'asc'}
                      onClick={() => handleSort('advisoryDetail')}
                  >
                      Notes
                    </TableSortLabel>
                  </TableCell>
                  <Hidden smDown>
                  <TableCell>
                  <TableSortLabel
                    active={orderBy === 'relation'}
                     direction={orderBy === 'relation' ? order : 'asc'}
                      onClick={() => handleSort('relation')}
                  >
                      Relation
                    </TableSortLabel>
                  </TableCell>
                  <TableCell>
                  <TableSortLabel
                    active={orderBy === 'licenses'}
                     direction={orderBy === 'licenses' ? order : 'asc'}
                      onClick={() => handleSort('licenses')}
                  >
                      Licenses
                    </TableSortLabel>
                  </TableCell>
                  </Hidden>
                </TableRow>
              </TableHead>
              <TableBody>
              {filteredDependencies && 
                filteredDependencies.map((dependency, index) => (
                <TableRow key={index}>
                    <TableCell>
                    <Link href={`/dependency/${encodeURIComponent(dependency.versionKey.name)}/${dependency.versionKey.system}/${dependency.versionKey.version}`}>
                      <Typography variant="subtitle1">
                    {dependency.versionKey.name}
                    </Typography>
                    </Link>
                    <Typography variant="subtitle2" color="text.secondary">
                    {dependency.versionKey.version}
                    </Typography>
                    </TableCell>
                    <Hidden smDown>
                    <TableCell>{dependency.advisoryDetail.length > 0 && dependency.advisoryDetail.length}</TableCell>
                    <TableCell>{dependency.relation}</TableCell>
                    <TableCell>{dependency.licenses}</TableCell>
                    </Hidden>
                  </TableRow>
                ))
              }
                </TableBody>
              </Table>
            </TableContainer>
          </Grid>
        </Grid>
      </Container>
      );
      
}

export default DependencyTable