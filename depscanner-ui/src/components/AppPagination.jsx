import React from 'react'
import { Box, Pagination } from '@mui/material'

const AppPagination = ({ totalItems, pageSize, currentPage, onPageChange }) => {
    const totalPages = Math.ceil(totalItems / pageSize);

    return (
      <Box>
      {totalItems > 5 && 
      <Pagination sx={{display: 'flex', justifyContent: 'center', alignItems: 'center' }}
        count={totalPages}
        page={currentPage}
        onChange={(event, newPage) => onPageChange(newPage)}
      />
     }
     </Box>
    );
  };
export default AppPagination