import { useState, useEffect } from 'react';
import {
  Container,
  Box,
  Tabs,
  Tab,
  Fab,
  CircularProgress,
  Typography,
} from '@mui/material';
import { Add } from '@mui/icons-material';
import { postAPI } from '../services/api';
import PostCard from '../components/PostCard';
import CreatePostDialog from '../components/CreatePostDialog';

export default function Home() {
  const [tabValue, setTabValue] = useState(0);
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [openCreateDialog, setOpenCreateDialog] = useState(false);

  useEffect(() => {
    loadPosts();
  }, [tabValue]);

  const loadPosts = async () => {
    setLoading(true);
    try {
      const response = tabValue === 0
        ? await postAPI.getAllPosts()
        : await postAPI.getFollowingFeed();
      setPosts(response.data);
    } catch (error) {
      console.error('Error loading posts:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="md">
      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={tabValue} onChange={(e, newValue) => setTabValue(newValue)} centered>
          <Tab label="Explore" />
          <Tab label="Following" />
        </Tabs>
      </Box>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <CircularProgress />
        </Box>
      ) : posts.length === 0 ? (
        <Typography align="center" color="text.secondary" sx={{ mt: 4 }}>
          {tabValue === 0 ? 'No posts yet. Be the first to post!' : 'Follow users to see their posts here'}
        </Typography>
      ) : (
        posts.map((post) => (
          <PostCard key={post.postId} post={post} onUpdate={loadPosts} />
        ))
      )}

      <Fab
        color="primary"
        sx={{ position: 'fixed', bottom: 16, right: 16 }}
        onClick={() => setOpenCreateDialog(true)}
      >
        <Add />
      </Fab>

      <CreatePostDialog
        open={openCreateDialog}
        onClose={() => setOpenCreateDialog(false)}
        onPostCreated={loadPosts}
      />
    </Container>
  );
}
