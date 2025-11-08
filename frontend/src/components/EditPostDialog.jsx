import { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  CircularProgress,
} from '@mui/material';
import { postAPI } from '../services/api';

export default function EditPostDialog({ open, onClose, post, onPostUpdated }) {
  const [caption, setCaption] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (post) {
      setCaption(post.caption || '');
    }
  }, [post]);

  const handleSubmit = async () => {
    if (!post) return;

    setLoading(true);
    try {
      await postAPI.updatePost(post.postId, { caption });
      if (onPostUpdated) {
        onPostUpdated();
      }
      onClose();
    } catch (error) {
      console.error('Error updating post:', error);
      alert(error.response?.data?.message || 'Failed to update post');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Edit Post</DialogTitle>
      <DialogContent>
        <TextField
          fullWidth
          label="Caption"
          value={caption}
          onChange={(e) => setCaption(e.target.value)}
          margin="normal"
          multiline
          rows={3}
          placeholder="Write a caption..."
          autoFocus
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={loading}>
          Cancel
        </Button>
        <Button
          onClick={handleSubmit}
          variant="contained"
          disabled={loading}
        >
          {loading ? <CircularProgress size={24} /> : 'Save'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
