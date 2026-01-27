import { Play, Star, Eye, ExternalLink } from 'lucide-react';

export default function VideoCard({ video, compact = false }) {
  const handleClick = () => {
    window.open(video.videoUrl, '_blank');
  };

  if (compact) {
    return (
      <div 
        onClick={handleClick}
        className="flex items-center gap-3 p-2 bg-gray-50 rounded-lg hover:bg-gray-100 cursor-pointer transition-colors"
      >
        <div className="relative w-20 h-14 flex-shrink-0">
          <img 
            src={video.thumbnailUrl || '/video-placeholder.png'} 
            alt={video.title}
            className="w-full h-full object-cover rounded"
          />
          <div className="absolute inset-0 flex items-center justify-center bg-black/30 rounded">
            <Play size={16} className="text-white" fill="white" />
          </div>
        </div>
        <div className="flex-1 min-w-0">
          <p className="text-sm font-medium text-gray-900 truncate">{video.title}</p>
          <div className="flex items-center gap-2 text-xs text-gray-500">
            {video.duration && <span>{video.duration}</span>}
            {video.isExactMatch && (
              <span className="text-green-600 font-medium">Exact match</span>
            )}
          </div>
        </div>
        <ExternalLink size={14} className="text-gray-400" />
      </div>
    );
  }

  return (
    <div 
      onClick={handleClick}
      className="bg-white rounded-lg shadow hover:shadow-md transition-shadow cursor-pointer overflow-hidden"
    >
      <div className="relative aspect-video">
        <img 
          src={video.thumbnailUrl || '/video-placeholder.png'} 
          alt={video.title}
          className="w-full h-full object-cover"
        />
        <div className="absolute inset-0 flex items-center justify-center bg-black/30 opacity-0 hover:opacity-100 transition-opacity">
          <Play size={48} className="text-white" fill="white" />
        </div>
        {video.duration && (
          <span className="absolute bottom-2 right-2 bg-black/80 text-white text-xs px-1.5 py-0.5 rounded">
            {video.duration}
          </span>
        )}
        {video.difficultyLevel && (
          <span className="absolute top-2 left-2 bg-blue-600 text-white text-xs px-2 py-0.5 rounded">
            {video.difficultyLevel}
          </span>
        )}
      </div>
      <div className="p-3">
        <h3 className="font-medium text-gray-900 line-clamp-2">{video.title}</h3>
        <div className="flex items-center gap-3 mt-2 text-sm text-gray-500">
          {video.rating > 0 && (
            <span className="flex items-center gap-1">
              <Star size={14} className="text-yellow-500" fill="currentColor" />
              {video.rating.toFixed(1)}
            </span>
          )}
          {video.viewCount > 0 && (
            <span className="flex items-center gap-1">
              <Eye size={14} />
              {video.viewCount.toLocaleString()}
            </span>
          )}
        </div>
      </div>
    </div>
  );
}
