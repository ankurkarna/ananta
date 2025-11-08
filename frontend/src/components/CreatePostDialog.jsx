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
  Tabs,
  Tab,
  Typography,
  LinearProgress,
} from '@mui/material';
import { CloudUpload, Link as LinkIcon } from '@mui/icons-material';
import { postAPI } from '../services/api';
import axios from 'axios';

export default function CreatePostDialog({ open, onClose, onPostCreated }) {
  const [tabValue, setTabValue] = useState(0);
  const [formData, setFormData] = useState({ imageUrl: '', caption: '' });
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      // Validate file type
      if (!selectedFile.type.startsWith('image/') && !selectedFile.type.startsWith('video/')) {
        alert('Please select an image or video file');
        return;
      }
      // Validate file size (100MB max)
      if (selectedFile.size > 100 * 1024 * 1024) {
        alert('File size must be less than 100MB');
        return;
      }
      setFile(selectedFile);
    }
  };

  const handleSubmit = async () => {
    setLoading(true);
    setUploadProgress(0);

    try {
      let imageUrl = '';

      // Tab 0: File upload
      if (tabValue === 0 && file) {
        const formData = new FormData();
        formData.append('file', file);

        const token = localStorage.getItem('token');
        const uploadResponse = await axios.post('/ananta/dev/api/upload/image', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': `Bearer ${token}`
          },
          onUploadProgress: (progressEvent) => {
            const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
            setUploadProgress(percentCompleted);
          }
        });

        imageUrl = uploadResponse.data.url;
      }
      // Tab 1: URL/Google Drive link
      else if (tabValue === 1 && formData.imageUrl) {
        imageUrl = formData.imageUrl;
      } else {
        alert('Please provide an image or enter a URL');
        setLoading(false);
        return;
      }

      // Create post
      await postAPI.createPost({
        imageUrl: imageUrl,
        caption: formData.caption
      });

      // Reset form
      setFormData({ imageUrl: '', caption: '' });
      setFile(null);
      setUploadProgress(0);
      onClose();
      if (onPostCreated) onPostCreated();

    } catch (error) {
      console.error('Error creating post:', error);
      alert(error.response?.data?.message || error.response?.data?.error || 'Failed to create post');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Create New Post</DialogTitle>
      <DialogContent>
        <Tabs value={tabValue} onChange={(e, newValue) => setTabValue(newValue)} sx={{ mb: 2 }}>
          <Tab icon={<CloudUpload />} label="Upload File" />
          <Tab icon={<LinkIcon />} label="URL / Google Drive" />
        </Tabs>

        <Box sx={{ pt: 1 }}>
          {tabValue === 0 ? (
            // File Upload Tab
            <Box>
              <Button
                variant="outlined"
                component="label"
                fullWidth
                startIcon={<CloudUpload />}
                sx={{ mb: 2, py: 2 }}
              >
                {file ? file.name : 'Choose Image or Video'}
                <input
                  type="file"
                  hidden
                  accept="image/*,video/*"
                  onChange={handleFileChange}
                />
              </Button>
              {file && (
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                  Size: {(file.size / (1024 * 1024)).toFixed(2)} MB
                </Typography>
              )}
              {uploadProgress > 0 && uploadProgress < 100 && (
                <Box sx={{ mb: 2 }}>
                  <LinearProgress variant="determinate" value={uploadProgress} />
                  <Typography variant="caption" color="text.secondary">
                    Uploading: {uploadProgress}%
                  </Typography>
                </Box>
              )}
            </Box>
          ) : (
            // URL Tab
            <Box>
              <TextField
                fullWidth
                label="Image URL or Google Drive Link"
                value={formData.imageUrl}
                onChange={(e) => setFormData({ ...formData, imageUrl: e.target.value })}
                margin="normal"
                placeholder="https://drive.google.com/file/d/FILE_ID/view..."
                helperText="Paste any image URL or Google Drive sharing link"
              />
            </Box>
          )}

          <TextField
            fullWidth
            label="Caption"
            value={formData.caption}
            onChange={(e) => setFormData({ ...formData, caption: e.target.value })}
            margin="normal"
            multiline
            rows={3}
            placeholder="Write a caption..."
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={loading}>Cancel</Button>
        <Button
          onClick={handleSubmit}
          variant="contained"
          disabled={loading || (tabValue === 0 && !file) || (tabValue === 1 && !formData.imageUrl)}
        >
          {loading ? <CircularProgress size={24} /> : 'Post'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
