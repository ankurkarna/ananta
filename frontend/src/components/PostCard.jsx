import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Card,
  CardHeader,
  CardMedia,
  CardContent,
  CardActions,
  Avatar,
  IconButton,
  Typography,
  TextField,
  Button,
  Box,
  Collapse,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Menu,
  MenuItem,
} from '@mui/material';
import {
  Favorite,
  FavoriteBorder,
  ChatBubbleOutline,
  Send,
  MoreVert,
  Edit,
  Delete,
} from '@mui/icons-material';
import { likeAPI, commentAPI, postAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import EditPostDialog from './EditPostDialog';

export default function PostCard({ post, onUpdate }) {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [liked, setLiked] = useState(post.likedByCurrentUser);
  const [likeCount, setLikeCount] = useState(post.likeCount);
  const [showComments, setShowComments] = useState(false);
  const [comments, setComments] = useState([]);
  const [commentText, setCommentText] = useState('');
  const [loadingComments, setLoadingComments] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const [editDialogOpen, setEditDialogOpen] = useState(false);

  const isOwnPost = user && post.user && user.username === post.user.username;

  const handleLike = async () => {
    try {
      if (liked) {
        await likeAPI.unlikePost(post.postId);
        setLiked(false);
        setLikeCount(prev => prev - 1);
      } else {
        await likeAPI.likePost(post.postId);
        setLiked(true);
        setLikeCount(prev => prev + 1);
      }
    } catch (error) {
      console.error('Error toggling like:', error);
    }
  };

  const loadComments = async () => {
    if (!showComments && comments.length === 0) {
      setLoadingComments(true);
      try {
        const response = await commentAPI.getCommentsByPost(post.postId);
        setComments(response.data);
      } catch (error) {
        console.error('Error loading comments:', error);
      } finally {
        setLoadingComments(false);
      }
    }
    setShowComments(!showComments);
  };

  const handleAddComment = async () => {
    if (!commentText.trim()) return;
    try {
      const response = await commentAPI.addComment(post.postId, { content: commentText });
      setComments([response.data, ...comments]);
      setCommentText('');
      if (onUpdate) onUpdate();
    } catch (error) {
      console.error('Error adding comment:', error);
    }
  };

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleEdit = () => {
    setEditDialogOpen(true);
    handleMenuClose();
  };

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this post?')) return;
    try {
      await postAPI.deletePost(post.postId);
      if (onUpdate) onUpdate();
      handleMenuClose();
    } catch (error) {
      console.error('Error deleting post:', error);
      alert('Failed to delete post');
    }
  };

  const handleUsernameClick = () => {
    navigate(`/profile/${post.user.username}`);
  };

  return (
    <>
      <Card sx={{ mb: 3 }}>
        <CardHeader
          avatar={
            <Avatar
              sx={{ bgcolor: 'secondary.main', cursor: 'pointer' }}
              onClick={handleUsernameClick}
            >
              {post.user.username[0].toUpperCase()}
            </Avatar>
          }
          title={
            <Typography
              variant="subtitle1"
              sx={{ cursor: 'pointer', '&:hover': { textDecoration: 'underline' } }}
              onClick={handleUsernameClick}
            >
              {post.user.username}
            </Typography>
          }
          subheader={new Date(post.createdAt).toLocaleDateString()}
          action={
            isOwnPost && (
              <IconButton onClick={handleMenuOpen}>
                <MoreVert />
              </IconButton>
            )
          }
        />
      <CardMedia
        component="img"
        image={post.imageUrl}
        alt={post.caption}
        sx={{ maxHeight: 500, objectFit: 'cover' }}
      />
      <CardContent>
        <Typography variant="body2" color="text.secondary">
          {post.caption}
        </Typography>
      </CardContent>
      <CardActions disableSpacing>
        <IconButton onClick={handleLike} color={liked ? 'error' : 'default'}>
          {liked ? <Favorite /> : <FavoriteBorder />}
        </IconButton>
        <Typography variant="body2" sx={{ mr: 2 }}>
          {likeCount}
        </Typography>
        <IconButton onClick={loadComments}>
          <ChatBubbleOutline />
        </IconButton>
        <Typography variant="body2">
          {post.commentCount}
        </Typography>
      </CardActions>

      <Collapse in={showComments} timeout="auto" unmountOnExit>
        <CardContent>
          <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
            <TextField
              fullWidth
              size="small"
              placeholder="Add a comment..."
              value={commentText}
              onChange={(e) => setCommentText(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleAddComment()}
            />
            <IconButton color="primary" onClick={handleAddComment}>
              <Send />
            </IconButton>
          </Box>

          {loadingComments ? (
            <Typography>Loading comments...</Typography>
          ) : (
            <List>
              {comments.map((comment) => (
                <ListItem key={comment.commentId} alignItems="flex-start">
                  <ListItemAvatar>
                    <Avatar sx={{ bgcolor: 'secondary.main' }}>
                      {comment.user.username[0].toUpperCase()}
                    </Avatar>
                  </ListItemAvatar>
                  <ListItemText
                    primary={comment.user.username}
                    secondary={comment.content}
                  />
                </ListItem>
              ))}
            </List>
          )}
        </CardContent>
      </Collapse>
    </Card>

    <Menu
      anchorEl={anchorEl}
      open={Boolean(anchorEl)}
      onClose={handleMenuClose}
    >
      <MenuItem onClick={handleEdit}>
        <Edit sx={{ mr: 1 }} fontSize="small" />
        Edit
      </MenuItem>
      <MenuItem onClick={handleDelete} sx={{ color: 'error.main' }}>
        <Delete sx={{ mr: 1 }} fontSize="small" />
        Delete
      </MenuItem>
    </Menu>

    <EditPostDialog
      open={editDialogOpen}
      onClose={() => setEditDialogOpen(false)}
      post={post}
      onPostUpdated={onUpdate}
    />
  </>
  );
}
