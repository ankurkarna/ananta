# Ananta Frontend

Modern social media frontend built with Vite + React and Material UI.

## Features

- 🔐 Authentication (Login/Register)
- 📝 Create and view posts
- ❤️ Like posts
- 💬 Comment on posts
- 👥 Follow/Unfollow users
- 🏠 Two feed types: Explore (all posts) and Following (followed users)
- 🎨 Material Design with Google Material colors

## Tech Stack

- **Vite** - Fast build tool
- **React 18** - UI library
- **Material UI** - Component library
- **React Router** - Routing
- **Axios** - HTTP client

## Getting Started

### Prerequisites

- Node.js 16+ and npm

### Installation

```bash
# Install dependencies
npm install

# Start development server
npm run dev
```

The app will run on `http://localhost:3000`

### Backend Configuration

The frontend is configured to proxy API requests to the backend running on `http://localhost:8081/ananta/dev/api`.

If your backend runs on a different port or path, update `vite.config.js`:

```javascript
server: {
  port: 3000,
  proxy: {
    '/ananta/dev/api': {
      target: 'http://localhost:YOUR_PORT',
      changeOrigin: true,
    }
  }
}
```

## Project Structure

```
frontend/
├── src/
│   ├── components/       # Reusable components
│   │   ├── Navbar.jsx
│   │   ├── PostCard.jsx
│   │   └── CreatePostDialog.jsx
│   ├── pages/           # Page components
│   │   ├── Login.jsx
│   │   ├── Register.jsx
│   │   └── Home.jsx
│   ├── context/         # React context
│   │   └── AuthContext.jsx
│   ├── services/        # API services
│   │   └── api.js
│   ├── theme.js         # Material UI theme
│   ├── App.jsx          # Main app component
│   └── main.jsx         # Entry point
├── index.html
├── vite.config.js
└── package.json
```

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build

## Features Implemented

### Authentication
- Login with username/password
- Register new account
- JWT token storage
- Protected routes

### Posts
- Create posts with image URL and caption
- View all posts (Explore feed)
- View posts from followed users (Following feed)
- Delete own posts
- Like/Unlike posts
- Real-time like count

### Comments
- Add comments to posts
- View all comments on a post
- Display comment author and timestamp

### UI/UX
- Material Design 3
- Responsive layout
- Loading states
- Error handling
- Smooth animations

## Color Palette

- Primary: Material Blue (#1976d2)
- Secondary: Material Purple (#9c27b0)
- Background: #fafafa
- Paper: #ffffff

## Future Enhancements

- User profiles
- Follow/Unfollow from UI
- Image upload (currently uses URLs)
- Search users
- Notifications
- Dark mode
- Direct messaging
