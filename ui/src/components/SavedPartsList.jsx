import React, { useState, useEffect } from 'react';
import { Package, Trash2, ExternalLink, AlertCircle, Loader } from 'lucide-react';

const SavedPartsList = ({ vehicleId }) => {
  const [savedParts, setSavedParts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [removingPartId, setRemovingPartId] = useState(null);

  useEffect(() => {
    loadSavedParts();
  }, [vehicleId]);

  const loadSavedParts = async () => {
    try {
      setLoading(true);
      setError('');
      
      // In a real implementation, you'd call your API here
      // const parts = await apiService.getVehicleParts(vehicleId);
      
      // Mock saved parts for demo
      const mockSavedParts = [
        {
          id: 'saved-part-1',
          title: 'Premium Brake Pads Set',
          description: 'High-performance ceramic brake pads for superior stopping power.',
          price: '$89.99',
          source: 'AutoZone',
          url: 'https://autozone.com/brake-pads-example',
          image: 'https://images.unsplash.com/photo-1486262715619-67b85e0b08d3?w=300&h=200&fit=crop',
          dateAdded: new Date('2024-01-15').toISOString(),
          category: 'Brakes',
          partNumber: 'BP-2024-001'
        },
        {
          id: 'saved-part-2',
          title: 'OEM Oil Filter',
          description: 'Original Equipment Manufacturer oil filter for optimal engine protection.',
          price: '$24.99',
          source: 'Parts Geek',
          url: 'https://partsgeek.com/oil-filter-example',
          image: 'https://images.unsplash.com/photo-1581833971358-2c8b550f87b3?w=300&h=200&fit=crop',
          dateAdded: new Date('2024-01-10').toISOString(),
          category: 'Engine',
          partNumber: 'OF-2024-002'
        },
        {
          id: 'saved-part-3',
          title: 'LED Headlight Bulbs',
          description: 'Ultra-bright LED headlight bulbs with 6000K color temperature.',
          price: '$129.99',
          source: 'Amazon',
          url: 'https://amazon.com/led-headlights-example',
          image: 'https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=300&h=200&fit=crop',
          dateAdded: new Date('2024-01-05').toISOString(),
          category: 'Lighting',
          partNumber: 'HL-2024-003'
        }
      ];

      // Simulate API delay
      await new Promise(resolve => setTimeout(resolve, 500));
      setSavedParts(mockSavedParts);
    } catch (err) {
      console.error('Error loading saved parts:', err);
      setError('Failed to load saved parts');
    } finally {
      setLoading(false);
    }
  };

  const handleRemovePart = async (partId) => {
    if (!confirm('Are you sure you want to remove this part from your vehicle?')) {
      return;
    }

    try {
      setRemovingPartId(partId);
      
      // In a real implementation, you'd call your API here
      // await apiService.removePartFromVehicle(vehicleId, partId);
      
      // Simulate API delay
      await new Promise(resolve => setTimeout(resolve, 500));
      
      // Remove from local state
      setSavedParts(prev => prev.filter(part => part.id !== partId));
    } catch (err) {
      console.error('Error removing part:', err);
      setError('Failed to remove part');
    } finally {
      setRemovingPartId(null);
    }
  };

  const handleExternalClick = (url) => {
    window.open(url, '_blank', 'noopener,noreferrer');
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  const getCategoryColor = (category) => {
    const colors = {
      'Brakes': 'bg-red-500/20 text-red-400',
      'Engine': 'bg-blue-500/20 text-blue-400',
      'Lighting': 'bg-yellow-500/20 text-yellow-400',
      'Suspension': 'bg-purple-500/20 text-purple-400',
      'Electrical': 'bg-green-500/20 text-green-400',
      'Interior': 'bg-pink-500/20 text-pink-400',
      'Exterior': 'bg-indigo-500/20 text-indigo-400'
    };
    return colors[category] || 'bg-slate-500/20 text-slate-400';
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-8">
        <div className="text-center">
          <Loader className="w-6 h-6 text-primary-500 animate-spin mx-auto mb-2" />
          <p className="text-slate-400 text-sm">Loading saved parts...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center gap-2 p-4 bg-red-500/10 border border-red-500/20 rounded-lg text-red-400">
        <AlertCircle className="w-5 h-5 flex-shrink-0" />
        <div>
          <p className="font-medium">Error loading parts</p>
          <p className="text-sm opacity-80">{error}</p>
        </div>
      </div>
    );
  }

  if (savedParts.length === 0) {
    return (
      <div className="text-center py-8">
        <Package className="w-12 h-12 text-slate-600 mx-auto mb-3" />
        <p className="text-slate-400 mb-2">No parts saved yet</p>
        <p className="text-slate-500 text-sm">Use the search above to find and add parts to this vehicle.</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <p className="text-sm text-slate-400">
          {savedParts.length} part{savedParts.length !== 1 ? 's' : ''} saved
        </p>
      </div>

      <div className="space-y-3 max-h-96 overflow-y-auto">
        {savedParts.map((part) => (
          <div
            key={part.id}
            className="bg-slate-700/50 border border-slate-600 rounded-lg p-4 hover:border-slate-500 transition-all duration-200"
          >
            <div className="flex gap-4">
              {/* Part Image */}
              <div className="flex-shrink-0">
                <div className="w-12 h-12 rounded-lg overflow-hidden bg-slate-600">
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
                  <div>
                    <h4 className="text-white font-medium text-sm leading-tight line-clamp-1">
                      {part.title}
                    </h4>
                    {part.partNumber && (
                      <p className="text-slate-500 text-xs">
                        Part #: {part.partNumber}
                      </p>
                    )}
                  </div>

                  {/* Category Badge */}
                  <span className={`inline-flex px-2 py-1 text-xs rounded-md ${getCategoryColor(part.category)}`}>
                    {part.category}
                  </span>
                </div>

                <p className="text-slate-400 text-xs mb-2 line-clamp-1">
                  {part.description}
                </p>

                {/* Metadata */}
                <div className="flex items-center justify-between text-xs text-slate-500 mb-3">
                  <span>Added {formatDate(part.dateAdded)}</span>
                  <span>from {part.source}</span>
                </div>

                {/* Price and Actions */}
                <div className="flex items-center justify-between">
                  <div className="text-primary-400 font-semibold text-sm">
                    {part.price}
                  </div>
                  
                  <div className="flex items-center gap-2">
                    {/* External Link Button */}
                    <button
                      onClick={() => handleExternalClick(part.url)}
                      className="flex items-center gap-1 px-2 py-1 bg-slate-600 text-slate-300 rounded-md hover:bg-slate-500 hover:text-white transition-colors text-xs"
                      title="View on retailer website"
                    >
                      <ExternalLink className="w-3 h-3" />
                      View
                    </button>

                    {/* Remove Button */}
                    <button
                      onClick={() => handleRemovePart(part.id)}
                      disabled={removingPartId === part.id}
                      className="flex items-center gap-1 px-2 py-1 bg-red-600/20 text-red-400 rounded-md hover:bg-red-600/30 hover:text-red-300 transition-colors text-xs disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      {removingPartId === part.id ? (
                        <Loader className="w-3 h-3 animate-spin" />
                      ) : (
                        <Trash2 className="w-3 h-3" />
                      )}
                      Remove
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default SavedPartsList;