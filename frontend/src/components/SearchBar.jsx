import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  TextField,
  InputAdornment,
  Popover,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Avatar,
  CircularProgress,
  Typography,
  Box,
} from '@mui/material';
import { Search } from '@mui/icons-material';
import { userAPI } from '../services/api';

export default function SearchBar() {
  const navigate = useNavigate();
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const searchRef = useRef(null);
  const debounceTimer = useRef(null);

  useEffect(() => {
    // Debounce search - wait 300ms after user stops typing
    if (debounceTimer.current) {
      clearTimeout(debounceTimer.current);
    }

    if (query.trim().length === 0) {
      setResults([]);
      setAnchorEl(null);
      return;
    }

    debounceTimer.current = setTimeout(() => {
      performSearch(query);
    }, 300);

    return () => {
      if (debounceTimer.current) {
        clearTimeout(debounceTimer.current);
      }
    };
  }, [query]);

  const performSearch = async (searchQuery) => {
    setLoading(true);
    try {
      const response = await userAPI.searchUsers(searchQuery);
      setResults(response.data);
      if (searchRef.current) {
        setAnchorEl(searchRef.current);
      }
    } catch (error) {
      console.error('Error searching users:', error);
      setResults([]);
    } finally {
      setLoading(false);
    }
  };

  const handleUserClick = (username) => {
    navigate(`/profile/${username}`);
    setQuery('');
    setResults([]);
    setAnchorEl(null);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl) && (results.length > 0 || loading);

  return (
    <Box ref={searchRef}>
      <TextField
        size="small"
        placeholder="Search users..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <Search />
            </InputAdornment>
          ),
        }}
        sx={{
          minWidth: 250,
          backgroundColor: 'background.paper',
          borderRadius: 1,
        }}
      />

      <Popover
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'left',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'left',
        }}
        PaperProps={{
          sx: {
            width: anchorEl ? anchorEl.offsetWidth : 250,
            maxHeight: 400,
          }
        }}
      >
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}>
            <CircularProgress size={24} />
          </Box>
        ) : results.length === 0 ? (
          <Typography sx={{ p: 2 }} color="text.secondary">
            No users found
          </Typography>
        ) : (
          <List>
            {results.map((user) => (
              <ListItem
                key={user.userId}
                button
                onClick={() => handleUserClick(user.username)}
              >
                <ListItemAvatar>
                  <Avatar sx={{ bgcolor: 'secondary.main' }}>
                    {user.username[0].toUpperCase()}
                  </Avatar>
                </ListItemAvatar>
                <ListItemText
                  primary={user.username}
                  secondary={user.bio || 'No bio'}
                />
              </ListItem>
            ))}
          </List>
        )}
      </Popover>
    </Box>
  );
}
