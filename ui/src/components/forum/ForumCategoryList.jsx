import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { MessageSquare, Users, ChevronRight } from 'lucide-react';
import { forumApi } from '../../services/forumApi';

export default function ForumCategoryList() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    forumApi.getCategories()
      .then(setCategories)
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="animate-pulse space-y-4">
        {[1, 2, 3].map(i => (
          <div key={i} className="h-20 bg-gray-200 rounded-lg" />
        ))}
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 text-red-700 p-4 rounded-lg">
        Error loading categories: {error}
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {categories.map(category => (
        <Link
          key={category.id}
          to={`/forum/category/${category.slug}`}
          className="block bg-white rounded-lg shadow hover:shadow-md transition-shadow p-4"
        >
          <div className="flex items-center justify-between">
            <div className="flex-1">
              <h3 className="font-semibold text-lg text-gray-900">{category.name}</h3>
              <p className="text-gray-600 text-sm mt-1">{category.description}</p>
              <div className="flex items-center gap-4 mt-2 text-sm text-gray-500">
                <span className="flex items-center gap-1">
                  <MessageSquare size={14} />
                  {category.threadCount} threads
                </span>
                <span className="flex items-center gap-1">
                  <Users size={14} />
                  {category.postCount} posts
                </span>
              </div>
            </div>
            <ChevronRight className="text-gray-400" />
          </div>
        </Link>
      ))}
    </div>
  );
}
