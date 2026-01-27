import { Link } from 'react-router-dom';
import { MessageSquare, Eye, Pin, Lock, CheckCircle } from 'lucide-react';

export default function ThreadList({ threads, loading }) {
  if (loading) {
    return (
      <div className="animate-pulse space-y-3">
        {[1, 2, 3, 4, 5].map(i => (
          <div key={i} className="h-24 bg-gray-200 rounded-lg" />
        ))}
      </div>
    );
  }

  if (!threads?.length) {
    return (
      <div className="text-center py-8 text-gray-500">
        No threads yet. Be the first to start a discussion!
      </div>
    );
  }

  const getThreadTypeColor = (type) => {
    const colors = {
      QUESTION: 'bg-blue-100 text-blue-800',
      DISCUSSION: 'bg-gray-100 text-gray-800',
      SHOWCASE: 'bg-purple-100 text-purple-800',
      TUTORIAL: 'bg-green-100 text-green-800',
      PARTS_REQUEST: 'bg-orange-100 text-orange-800',
    };
    return colors[type] || colors.DISCUSSION;
  };

  return (
    <div className="space-y-3">
      {threads.map(thread => (
        <Link
          key={thread.id}
          to={`/forum/thread/${thread.id}`}
          className="block bg-white rounded-lg shadow hover:shadow-md transition-shadow p-4"
        >
          <div className="flex items-start gap-3">
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2 flex-wrap">
                {thread.isPinned && <Pin size={14} className="text-yellow-500" />}
                {thread.isLocked && <Lock size={14} className="text-red-500" />}
                {thread.isSolved && <CheckCircle size={14} className="text-green-500" />}
                <span className={`text-xs px-2 py-0.5 rounded ${getThreadTypeColor(thread.threadType)}`}>
                  {thread.threadType}
                </span>
              </div>
              <h3 className="font-medium text-gray-900 mt-1 truncate">{thread.title}</h3>
              <p className="text-sm text-gray-500 mt-1 line-clamp-2">{thread.content}</p>
              <div className="flex items-center gap-4 mt-2 text-xs text-gray-500">
                <span>by {thread.author?.displayName || 'Anonymous'}</span>
                <span className="flex items-center gap-1">
                  <MessageSquare size={12} />
                  {thread.replyCount}
                </span>
                <span className="flex items-center gap-1">
                  <Eye size={12} />
                  {thread.viewCount}
                </span>
              </div>
            </div>
          </div>
        </Link>
      ))}
    </div>
  );
}
