import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Container,
  Box,
  Avatar,
  Typography,
  Button,
  Grid,
  CircularProgress,
  Card,
  CardMedia,
} from '@mui/material';
import { PersonAdd, PersonRemove } from '@mui/icons-material';
import { userAPI, postAPI, followAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function Profile() {
  const { username } = useParams();
  const navigate = useNavigate();
  const { user: currentUser } = useAuth();

  const [user, setUser] = useState(null);
  const [posts, setPosts] = useState([]);
  const [followerCount, setFollowerCount] = useState(0);
  const [followingCount, setFollowingCount] = useState(0);
  const [isFollowing, setIsFollowing] = useState(false);
  const [loading, setLoading] = useState(true);

  const isOwnProfile = currentUser?.username === username;

  useEffect(() => {
    loadProfileData();
  }, [username]);

  const loadProfileData = async () => {
    setLoading(true);
    try {
      // Fetch user data
      const userResponse = await userAPI.getUserByUsername(username);
      const userData = userResponse.data;
      setUser(userData);

      // Fetch posts
      const postsResponse = await postAPI.getPostsByUser(userData.userId);
      setPosts(postsResponse.data);

      // Fetch follower and following counts
      const followerResponse = await followAPI.getFollowerCount(userData.userId);
      setFollowerCount(followerResponse.data.count);

      const followingResponse = await followAPI.getFollowingCount(userData.userId);
      setFollowingCount(followingResponse.data.count);

      // Check if current user is following this user
      if (!isOwnProfile) {
        try {
          const followersResponse = await followAPI.getFollowers(userData.userId);
          const currentUserId = JSON.parse(localStorage.getItem('user'))?.userId;
          const isFollowingUser = followersResponse.data.some(
            follower => follower.userId === currentUserId
          );
          setIsFollowing(isFollowingUser);
        } catch (error) {
          console.error('Error checking follow status:', error);
        }
      }
    } catch (error) {
      console.error('Error loading profile:', error);
      alert('User not found');
      navigate('/');
    } finally {
      setLoading(false);
    }
  };

  const handleFollowToggle = async () => {
    try {
      if (isFollowing) {
        await followAPI.unfollowUser(user.userId);
        setIsFollowing(false);
        setFollowerCount(prev => prev - 1);
      } else {
        await followAPI.followUser(user.userId);
        setIsFollowing(true);
        setFollowerCount(prev => prev + 1);
      }
    } catch (error) {
      console.error('Error toggling follow:', error);
      alert('Failed to update follow status');
    }
  };

  const handlePostClick = (postId) => {
    // Navigate to post detail or open post dialog
    navigate(`/`); // For now, just go to home
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!user) {
    return (
      <Container maxWidth="md">
        <Typography align="center" sx={{ mt: 4 }}>
          User not found
        </Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="md">
      <Box sx={{ mt: 4 }}>
        {/* Profile Header */}
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 4, gap: 4 }}>
          <Avatar
            sx={{
              width: 120,
              height: 120,
              bgcolor: 'secondary.main',
              fontSize: '3rem'
            }}
          >
            {user.username[0].toUpperCase()}
          </Avatar>

          <Box sx={{ flex: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
              <Typography variant="h4">{user.username}</Typography>
              {!isOwnProfile && (
                <Button
                  variant={isFollowing ? 'outlined' : 'contained'}
                  startIcon={isFollowing ? <PersonRemove /> : <PersonAdd />}
                  onClick={handleFollowToggle}
                >
                  {isFollowing ? 'Unfollow' : 'Follow'}
                </Button>
              )}
            </Box>

            <Box sx={{ display: 'flex', gap: 4, mb: 2 }}>
              <Typography>
                <strong>{posts.length}</strong> posts
              </Typography>
              <Typography>
                <strong>{followerCount}</strong> followers
              </Typography>
              <Typography>
                <strong>{followingCount}</strong> following
              </Typography>
            </Box>

            {user.bio && (
              <Typography variant="body1">{user.bio}</Typography>
            )}
          </Box>
        </Box>

        {/* Posts Grid */}
        {posts.length === 0 ? (
          <Typography align="center" color="text.secondary" sx={{ mt: 4 }}>
            No posts yet
          </Typography>
        ) : (
          <Grid container spacing={1}>
            {posts.map((post) => (
              <Grid item xs={4} key={post.postId}>
                <Card
                  sx={{
                    cursor: 'pointer',
                    paddingTop: '100%',
                    position: 'relative',
                    '&:hover': { opacity: 0.8 }
                  }}
                  onClick={() => handlePostClick(post.postId)}
                >
                  <CardMedia
                    component="img"
                    image={post.imageUrl}
                    alt={post.caption}
                    sx={{
                      position: 'absolute',
                      top: 0,
                      left: 0,
                      width: '100%',
                      height: '100%',
                      objectFit: 'cover'
                    }}
                  />
                </Card>
              </Grid>
            ))}
          </Grid>
        )}
      </Box>
    </Container>
  );
}
