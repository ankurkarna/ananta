import { useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Box,
  CircularProgress,
} from '@mui/material';
import { postAPI } from '../services/api';

export default function CreatePostDialog({ open, onClose, onPostCreated }) {
  const [formData, setFormData] = useState({ imageUrl: '', caption: '' });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    if (!formData.imageUrl) return;
    setLoading(true);
    try {
      await postAPI.createPost(formData);
      setFormData({ imageUrl: '', caption: '' });
      onClose();
      if (onPostCreated) onPostCreated();
    } catch (error) {
      console.error('Error creating post:', error);
      alert(error.response?.data?.message || 'Failed to create post');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Create New Post</DialogTitle>
      <DialogContent>
        <Box sx={{ pt: 2 }}>
          <TextField
            fullWidth
            label="Image URL"
            value={formData.imageUrl}
            onChange={(e) => setFormData({ ...formData, imageUrl: e.target.value })}
            margin="normal"
            required
          />
          <TextField
            fullWidth
            label="Caption"
            value={formData.caption}
            onChange={(e) => setFormData({ ...formData, caption: e.target.value })}
            margin="normal"
            multiline
            rows={3}
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button onClick={handleSubmit} variant="contained" disabled={loading || !formData.imageUrl}>
          {loading ? <CircularProgress size={24} /> : 'Post'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
