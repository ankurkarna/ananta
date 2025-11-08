import axios from 'axios';

const API_BASE_URL = '/ananta/dev/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Auth APIs
export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
};

// Post APIs
export const postAPI = {
  createPost: (data) => api.post('/posts', data),
  getAllPosts: () => api.get('/posts'),
  getFollowingFeed: () => api.get('/posts/following'),
  getPostById: (postId) => api.get(`/posts/${postId}`),
  getPostsByUser: (userId) => api.get(`/posts/user/${userId}`),
  deletePost: (postId) => api.delete(`/posts/${postId}`),
};

// Comment APIs
export const commentAPI = {
  addComment: (postId, data) => api.post(`/comments/post/${postId}`, data),
  getCommentsByPost: (postId) => api.get(`/comments/post/${postId}`),
  deleteComment: (commentId) => api.delete(`/comments/${commentId}`),
};

// Like APIs
export const likeAPI = {
  likePost: (postId) => api.post(`/likes/post/${postId}`),
  unlikePost: (postId) => api.delete(`/likes/post/${postId}`),
  getLikeCount: (postId) => api.get(`/likes/post/${postId}/count`),
};

// Follow APIs
export const followAPI = {
  followUser: (userId) => api.post(`/follows/user/${userId}`),
  unfollowUser: (userId) => api.delete(`/follows/user/${userId}`),
  getFollowers: (userId) => api.get(`/follows/user/${userId}/followers`),
  getFollowing: (userId) => api.get(`/follows/user/${userId}/following`),
  getFollowerCount: (userId) => api.get(`/follows/user/${userId}/followers/count`),
  getFollowingCount: (userId) => api.get(`/follows/user/${userId}/following/count`),
};

export default api;
