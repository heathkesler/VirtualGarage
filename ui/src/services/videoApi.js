const API_BASE = '/api/videos';

export const videoApi = {
  getRecentVideos: async (page = 0, size = 20) => {
    const res = await fetch(`${API_BASE}?page=${page}&size=${size}`);
    if (!res.ok) throw new Error('Failed to fetch videos');
    return res.json();
  },

  getVideo: async (id) => {
    const res = await fetch(`${API_BASE}/${id}`);
    if (!res.ok) throw new Error('Failed to fetch video');
    return res.json();
  },

  searchVideos: async (query, page = 0, size = 20) => {
    const res = await fetch(`${API_BASE}/search?query=${encodeURIComponent(query)}&page=${page}&size=${size}`);
    if (!res.ok) throw new Error('Failed to search videos');
    return res.json();
  },

  getInstallationVideos: async (partType, make, model, year, limit = 5) => {
    const params = new URLSearchParams({ partType, make, model, year, limit });
    const res = await fetch(`${API_BASE}/installation?${params}`);
    if (!res.ok) throw new Error('Failed to fetch installation videos');
    return res.json();
  },

  getPopularVideos: async () => {
    const res = await fetch(`${API_BASE}/popular`);
    if (!res.ok) throw new Error('Failed to fetch popular videos');
    return res.json();
  },

  getCategories: async () => {
    const res = await fetch(`${API_BASE}/categories`);
    if (!res.ok) throw new Error('Failed to fetch categories');
    return res.json();
  },

  submitVideo: async (data) => {
    const res = await fetch(API_BASE, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Failed to submit video');
    return res.json();
  },

  rateVideo: async (videoId, userId, rating) => {
    const res = await fetch(`${API_BASE}/${videoId}/rate?userId=${userId}&rating=${rating}`, { method: 'POST' });
    if (!res.ok) throw new Error('Failed to rate video');
    return res.json();
  },
};

export default videoApi;
