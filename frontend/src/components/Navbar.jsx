import { AppBar, Toolbar, Typography, Button, Box, IconButton } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Home, ExitToApp, Brightness4, Brightness7 } from '@mui/icons-material';
import SearchBar from './SearchBar';

export default function Navbar({ darkMode, toggleDarkMode }) {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!user) return null;

  return (
    <AppBar position="sticky">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ cursor: 'pointer', mr: 3 }} onClick={() => navigate('/')}>
          Ananta
        </Typography>
        <Box sx={{ flexGrow: 1 }}>
          <SearchBar />
        </Box>
        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
          <IconButton color="inherit" onClick={toggleDarkMode} title={darkMode ? 'Light Mode' : 'Dark Mode'}>
            {darkMode ? <Brightness7 /> : <Brightness4 />}
          </IconButton>
          <Button color="inherit" startIcon={<Home />} onClick={() => navigate('/')}>
            Home
          </Button>
          <Button color="inherit" startIcon={<ExitToApp />} onClick={handleLogout}>
            Logout
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  );
}
