import React, { useState } from 'react';
import { Plus, ExternalLink, Star, CheckCircle, XCircle, Loader } from 'lucide-react';

const PartTile = ({ part, onAdd }) => {
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

  const handleExternalClick = () => {
    window.open(part.url, '_blank', 'noopener,noreferrer');
  };

  const renderStars = (rating) => {
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
              <button
                onClick={handleExternalClick}
                className="flex items-center gap-1 px-3 py-1 bg-slate-600 text-slate-300 rounded-md hover:bg-slate-500 hover:text-white transition-colors text-xs"
                title="View on retailer website"
              >
                <ExternalLink className="w-3 h-3" />
                View
              </button>

              {/* Add Button */}
              <button
                onClick={handleAdd}
                disabled={adding || added || !part.inStock}
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