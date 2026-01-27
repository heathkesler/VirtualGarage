import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { MessageSquare, Plus, Search } from 'lucide-react';
import ForumCategoryList from '../../components/forum/ForumCategoryList';
import ThreadList from '../../components/forum/ThreadList';
import { forumApi } from '../../services/forumApi';

export default function ForumHome() {
  const [recentThreads, setRecentThreads] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    forumApi.getRecentThreads(0, 5)
      .then(data => setRecentThreads(data.content || []))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      window.location.href = `/forum/search?q=${encodeURIComponent(searchQuery)}`;
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-4">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-3">
          <MessageSquare size={32} className="text-blue-600" />
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Community Forum</h1>
            <p className="text-gray-600">Ask questions, share knowledge, connect with car enthusiasts</p>
          </div>
        </div>
        <Link
          to="/forum/new"
          className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
        >
          <Plus size={20} />
          New Thread
        </Link>
      </div>

      {/* Search */}
      <form onSubmit={handleSearch} className="mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
          <input
            type="text"
            placeholder="Search discussions..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
      </form>

      <div className="grid md:grid-cols-3 gap-6">
        {/* Categories */}
        <div className="md:col-span-2">
          <h2 className="text-lg font-semibold text-gray-900 mb-3">Categories</h2>
          <ForumCategoryList />
        </div>

        {/* Recent Activity */}
        <div>
          <h2 className="text-lg font-semibold text-gray-900 mb-3">Recent Activity</h2>
          <ThreadList threads={recentThreads} loading={loading} />
          <Link
            to="/forum/recent"
            className="block text-center text-blue-600 hover:underline mt-3 text-sm"
          >
            View all recent threads →
          </Link>
        </div>
      </div>
    </div>
  );
}
