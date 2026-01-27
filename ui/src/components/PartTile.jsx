import React, { useState } from 'react';
import { Plus, ExternalLink, Star, CheckCircle, XCircle, Loader, Sparkles, Wrench, ShoppingBag } from 'lucide-react';

const PartTile = ({ part, onAdd, isAiGenerated = false }) => {
  const [adding, setAdding] = useState(false);
  const [added, setAdded] = useState(false);

  const handleAdd = async () => {
    if (adding || added) return;
    
    try {
      setAdding(true);
      await onAdd(part);
      setAdded(true);
      
      // Reset added state after 2 seconds for feedback
      setTimeout(() => setAdded(false), 2000);
    } catch (err) {
      console.error('Error adding part:', err);
    } finally {
      setAdding(false);
    }
  };

  const handleExternalClick = (url) => {
    window.open(url, '_blank', 'noopener,noreferrer');
  };

  const renderStars = (rating) => {
    if (!rating) return null;
    
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 !== 0;
    const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);

    return (
      <div className="flex items-center gap-1">
        {[...Array(fullStars)].map((_, i) => (
          <Star key={i} className="w-4 h-4 fill-yellow-400 text-yellow-400" />
        ))}
        {hasHalfStar && (
          <Star className="w-4 h-4 fill-yellow-400/50 text-yellow-400" />
        )}
        {[...Array(emptyStars)].map((_, i) => (
          <Star key={i} className="w-4 h-4 text-slate-600" />
        ))}
        <span className="text-xs text-slate-400 ml-1">({rating})</span>
      </div>
    );
  };

  const getDifficultyColor = (difficulty) => {
    const d = difficulty?.toLowerCase() || '';
    if (d.includes('easy')) return 'text-green-400 bg-green-500/20';
    if (d.includes('medium') || d.includes('moderate')) return 'text-yellow-400 bg-yellow-500/20';
    if (d.includes('hard') || d.includes('difficult')) return 'text-orange-400 bg-orange-500/20';
    if (d.includes('professional') || d.includes('expert')) return 'text-red-400 bg-red-500/20';
    return 'text-slate-400 bg-slate-500/20';
  };

  const getAddButtonContent = () => {
    if (adding) {
      return (
        <>
          <Loader className="w-4 h-4 animate-spin" />
          Adding...
        </>
      );
    }
    
    if (added) {
      return (
        <>
          <CheckCircle className="w-4 h-4" />
          Added!
        </>
      );
    }

    return (
      <>
        <Plus className="w-4 h-4" />
        Add Part
      </>
    );
  };

  const getAddButtonStyle = () => {
    if (adding) {
      return "bg-blue-600/50 cursor-not-allowed";
    }
    
    if (added) {
      return "bg-green-600 hover:bg-green-700";
    }

    return "bg-primary-600 hover:bg-primary-700";
  };

  // Render AI-generated part tile
  if (isAiGenerated || part.isAiGenerated) {
    return (
      <div className="bg-slate-700/50 border border-primary-500/30 rounded-lg p-4 hover:border-primary-500/50 transition-all duration-200">
        <div className="flex flex-col gap-3">
          {/* Header with AI badge */}
          <div className="flex items-start justify-between gap-2">
            <div className="flex-1">
              <div className="flex items-center gap-2 mb-1">
                <Sparkles className="w-4 h-4 text-primary-400" />
                <h4 className="text-white font-medium text-sm leading-tight">
                  {part.title}
                </h4>
              </div>
              {part.partNumber && part.partNumber !== 'N/A' && (
                <span className="text-xs text-slate-500">Part #: {part.partNumber}</span>
              )}
            </div>
            
            {/* Installation Difficulty */}
            {part.installationDifficulty && (
              <span className={`inline-flex items-center gap-1 px-2 py-1 text-xs rounded-md ${getDifficultyColor(part.installationDifficulty)}`}>
                <Wrench className="w-3 h-3" />
                {part.installationDifficulty}
              </span>
            )}
          </div>

          {/* Description */}
          <p className="text-slate-400 text-xs">
            {part.description}
          </p>

          {/* Notes */}
          {part.notes && (
            <p className="text-slate-500 text-xs italic border-l-2 border-slate-600 pl-2">
              💡 {part.notes}
            </p>
          )}

          {/* Brand and Price */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              {part.source && (
                <span className="text-slate-400 text-xs">
                  Brand: <span className="text-slate-300">{part.source}</span>
                </span>
              )}
            </div>
            <div className="text-primary-400 font-semibold">
              {part.price}
            </div>
          </div>

          {/* Where to Buy */}
          {part.whereToBuy && part.whereToBuy.length > 0 && (
            <div className="flex flex-wrap items-center gap-2">
              <ShoppingBag className="w-4 h-4 text-slate-500" />
              {part.whereToBuy.map((store, index) => (
                <span 
                  key={index}
                  className="px-2 py-1 bg-slate-600/50 text-slate-300 text-xs rounded-md"
                >
                  {store}
                </span>
              ))}
            </div>
          )}

          {/* Action Button */}
          <div className="flex justify-end pt-2 border-t border-slate-600/50">
            <button
              onClick={handleAdd}
              disabled={adding || added}
              className={`flex items-center gap-2 px-3 py-1 text-white rounded-md transition-colors text-xs disabled:opacity-50 disabled:cursor-not-allowed ${getAddButtonStyle()}`}
            >
              {getAddButtonContent()}
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Render traditional part tile (from search results with URLs)
  return (
    <div className="bg-slate-700/50 border border-slate-600 rounded-lg p-4 hover:border-slate-500 transition-all duration-200">
      <div className="flex gap-4">
        {/* Part Image */}
        <div className="flex-shrink-0">
          <div className="w-16 h-16 rounded-lg overflow-hidden bg-slate-600">
            {part.image ? (
              <img
                src={part.image}
                alt={part.title}
                className="w-full h-full object-cover"
                onError={(e) => {
                  e.target.style.display = 'none';
                  e.target.nextSibling.style.display = 'flex';
                }}
              />
            ) : null}
            <div 
              className="w-full h-full flex items-center justify-center text-slate-400 text-xs"
              style={{ display: part.image ? 'none' : 'flex' }}
            >
              No Image
            </div>
          </div>
        </div>

        {/* Part Details */}
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between gap-2 mb-2">
            <h4 className="text-white font-medium text-sm leading-tight line-clamp-2">
              {part.title}
            </h4>
            
            {/* Stock Status */}
            {part.inStock !== null && part.inStock !== undefined && (
              <div className="flex-shrink-0">
                {part.inStock ? (
                  <span className="inline-flex items-center gap-1 px-2 py-1 bg-green-500/20 text-green-400 text-xs rounded-md">
                    <CheckCircle className="w-3 h-3" />
                    In Stock
                  </span>
                ) : (
                  <span className="inline-flex items-center gap-1 px-2 py-1 bg-red-500/20 text-red-400 text-xs rounded-md">
                    <XCircle className="w-3 h-3" />
                    Out of Stock
                  </span>
                )}
              </div>
            )}
          </div>

          <p className="text-slate-400 text-xs mb-3 line-clamp-2">
            {part.description}
          </p>

          {/* Rating and Source */}
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center gap-2">
              {renderStars(part.rating)}
            </div>
            <span className="text-slate-500 text-xs">
              from {part.source}
            </span>
          </div>

          {/* Price and Actions */}
          <div className="flex items-center justify-between">
            <div className="text-primary-400 font-semibold">
              {part.price}
            </div>
            
            <div className="flex items-center gap-2">
              {/* External Link Button */}
              {part.url && (
                <button
                  onClick={() => handleExternalClick(part.url)}
                  className="flex items-center gap-1 px-3 py-1 bg-slate-600 text-slate-300 rounded-md hover:bg-slate-500 hover:text-white transition-colors text-xs"
                  title="View on retailer website"
                >
                  <ExternalLink className="w-3 h-3" />
                  View
                </button>
              )}

              {/* Add Button */}
              <button
                onClick={handleAdd}
                disabled={adding || added || (part.inStock === false)}
                className={`flex items-center gap-2 px-3 py-1 text-white rounded-md transition-colors text-xs disabled:opacity-50 disabled:cursor-not-allowed ${getAddButtonStyle()}`}
              >
                {getAddButtonContent()}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PartTile;
