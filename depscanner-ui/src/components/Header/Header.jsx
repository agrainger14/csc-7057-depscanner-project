import React from 'react'
import { Box, Button, Toolbar, Typography, Menu, MenuItem} from '@mui/material'
import { Link } from 'react-router-dom'
import { Security } from '@mui/icons-material'
import { useOidc } from '@axa-fr/react-oidc'
import styled from '@emotion/styled'
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';

const pages = ['About', 'Blog'];

const Appbar = styled(Box)({
    top: 0,
    width: '100%',
    display: 'flex',
    zIndex: 100,
    position: 'sticky',
    boxShadow: '5px 5px 10px 0px #000000',
    alignItems: 'center',
    flexDirection: 'column',
    justifyContent: 'center',
    backgroundColor: 'black',
});

const AppbarLogo = styled(Typography)({
    variant: "h6",
    noWrap: true,
    marginLeft: 1,
    marginRight: 2,
    display: { xs: 'none', md: 'flex' },
    fontWeight: 700,
    letterSpacing: '.3rem',
    color: 'inherit',
    textDecoration: 'none',
});

const HomeLinksBox = styled(Box)({
    display: 'flex',
    alignItems: 'flex-start',
    flexDirection: 'row',
})

const Header = () => {
    const { login, logout, isAuthenticated } = useOidc();
    const [anchorElNav, setAnchorElNav] = React.useState(null);
  
    const handleOpenNavMenu = (event) => {
      setAnchorElNav(event.currentTarget);
    };

    const handleCloseNavMenu = () => {
        setAnchorElNav(null);
    };

  return (
    <Appbar>
        <Toolbar sx={{
            width: '100%',
            display: 'flex',
            maxWidth: '1400px',
            alignItems: 'center',
            paddingX: 4,
            flexDirection: 'row',
            justifyContent: 'space-between',
        }}
        >
            <Box sx={{
                display: 'flex',
                alignItems: 'center',
                flexDirection: 'row',
                justifyContent: 'center',
                }}
            >
                <Security/>
                <Link to="/" style={{ textDecoration: 'none', color: 'inherit'}}>
                    <AppbarLogo>
                        DEPSCANNER
                    </AppbarLogo>
                </Link>
            </Box>
            <HomeLinksBox>
                <Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none' } }}>
                    <IconButton
                        size="large"
                        aria-label="account of current user"
                        aria-controls="menu-appbar"
                        aria-haspopup="true"
                        onClick={handleOpenNavMenu}
                        color="inherit"
                    >
                        <MenuIcon />
                    </IconButton>
                    <Menu
                        id="menu-appbar"
                        anchorEl={anchorElNav}
                        anchorOrigin={{
                            vertical: 'bottom',
                            horizontal: 'left',
                        }}
                        keepMounted
                        transformOrigin={{
                            vertical: 'top',
                            horizontal: 'left',
                        }}
                        open={Boolean(anchorElNav)}
                        onClose={handleCloseNavMenu}
                        sx={{
                            display: { xs: 'block', md: 'none' },
                        }}
                    >
                        <MenuItem onClick={handleCloseNavMenu}>
                            <Link to="/about" style={{ textDecoration: 'none', color: 'inherit'}}>
                                <Typography sx={{fontWeight: 700, ml:1}}>
                                    About
                                </Typography>
                            </Link>
                        </MenuItem>
                        <MenuItem onClick={handleCloseNavMenu}>
                            <Link to="/dashboard" style={{ textDecoration: 'none', color: 'inherit'}}>
                                <Typography sx={{fontWeight: 700, ml:1}}>
                                    Dashboard
                                </Typography>
                            </Link>
                        </MenuItem>
                        {isAuthenticated ? (
                            <Button sx={{borderRadius: '25px', pt:1.5, pb:1.5, pl:3, pr:3 }}>
                            <Typography onClick={() => logout("http://localhost:5173")} sx={{fontWeight: 700}}>
                                Logout
                            </Typography>
                            </Button>
                        ) : (
                            <Button sx={{borderRadius: '25px', pt:1.5, pb:1.5, pl:3, pr:3 }}>
                            <Typography onClick={() => login("/dashboard")} sx={{fontWeight: 700}}>
                                Login
                            </Typography>
                            </Button>
                        )}
                    </Menu>
                </Box>
                <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
                    <MenuItem onClick={handleCloseNavMenu}>
                        <Link to="/about" style={{ textDecoration: 'none', color: 'inherit'}}>
                            <Typography sx={{fontWeight: 700}}>
                                About
                            </Typography>
                        </Link>
                    </MenuItem>
                    <MenuItem onClick={handleCloseNavMenu}>
                        <Link to="/dashboard" style={{ textDecoration: 'none', color: 'inherit'}}>
                            <Typography sx={{fontWeight: 700}}>
                                Dashboard
                            </Typography>
                        </Link>
                    </MenuItem>
                    {isAuthenticated ? (
                        <Button variant="contained" sx={{borderRadius: '25px', pt:1.5, pb:1.5, pl:3, pr:3, backgroundColor:'#003375'}}>
                            <Typography onClick={() => logout("http://localhost:5173")} sx={{fontWeight: 700}}>
                                Logout
                            </Typography>
                        </Button>
                    ) : (
                        <Button variant="contained" sx={{borderRadius: '25px', pt:1.5, pb:1.5, pl:3, pr:3, backgroundColor:'#003375'}}>
                            <Typography onClick={() => login("/dashboard")} sx={{fontWeight: 700}}>
                                Login
                            </Typography>
                        </Button>
                    )}
                </Box>
            </HomeLinksBox>
        </Toolbar>
    </Appbar>
  )
}

export default Header