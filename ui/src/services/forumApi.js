const API_BASE = '/api/forum';

export const forumApi = {
  // Categories
  getCategories: async () => {
    const res = await fetch(`${API_BASE}/categories`);
    if (!res.ok) throw new Error('Failed to fetch categories');
    return res.json();
  },

  // Threads
  getRecentThreads: async (page = 0, size = 20) => {
    const res = await fetch(`${API_BASE}/threads?page=${page}&size=${size}`);
    if (!res.ok) throw new Error('Failed to fetch threads');
    return res.json();
  },

  getThreadsByCategory: async (slug, page = 0, size = 20) => {
    const res = await fetch(`${API_BASE}/categories/${slug}/threads?page=${page}&size=${size}`);
    if (!res.ok) throw new Error('Failed to fetch threads');
    return res.json();
  },

  getThread: async (id) => {
    const res = await fetch(`${API_BASE}/threads/${id}`);
    if (!res.ok) throw new Error('Failed to fetch thread');
    return res.json();
  },

  createThread: async (data) => {
    const res = await fetch(`${API_BASE}/threads`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Failed to create thread');
    return res.json();
  },

  searchThreads: async (query, page = 0, size = 20) => {
    const res = await fetch(`${API_BASE}/threads/search?query=${encodeURIComponent(query)}&page=${page}&size=${size}`);
    if (!res.ok) throw new Error('Failed to search threads');
    return res.json();
  },

  // Posts
  getPostsByThread: async (threadId, page = 0, size = 50) => {
    const res = await fetch(`${API_BASE}/threads/${threadId}/posts?page=${page}&size=${size}`);
    if (!res.ok) throw new Error('Failed to fetch posts');
    return res.json();
  },

  createPost: async (data) => {
    const res = await fetch(`${API_BASE}/posts`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Failed to create post');
    return res.json();
  },

  upvotePost: async (postId, userId) => {
    const res = await fetch(`${API_BASE}/posts/${postId}/upvote?userId=${userId}`, { method: 'POST' });
    if (!res.ok) throw new Error('Failed to upvote');
    return res.json();
  },

  downvotePost: async (postId, userId) => {
    const res = await fetch(`${API_BASE}/posts/${postId}/downvote?userId=${userId}`, { method: 'POST' });
    if (!res.ok) throw new Error('Failed to downvote');
    return res.json();
  },
};

export default forumApi;
