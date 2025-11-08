import { useState } from 'react';
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
} from '@mui/material';
import {
  Favorite,
  FavoriteBorder,
  ChatBubbleOutline,
  Send,
} from '@mui/icons-material';
import { likeAPI, commentAPI } from '../services/api';

export default function PostCard({ post, onUpdate }) {
  const [liked, setLiked] = useState(post.likedByCurrentUser);
  const [likeCount, setLikeCount] = useState(post.likeCount);
  const [showComments, setShowComments] = useState(false);
  const [comments, setComments] = useState([]);
  const [commentText, setCommentText] = useState('');
  const [loadingComments, setLoadingComments] = useState(false);

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

  return (
    <Card sx={{ mb: 3 }}>
      <CardHeader
        avatar={<Avatar sx={{ bgcolor: 'secondary.main' }}>{post.user.username[0].toUpperCase()}</Avatar>}
        title={post.user.username}
        subheader={new Date(post.createdAt).toLocaleDateString()}
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
  );
}
