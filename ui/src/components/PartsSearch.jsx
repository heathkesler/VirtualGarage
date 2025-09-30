import React, { useState } from 'react';
import { Search, Loader, AlertCircle, Plus, ExternalLink, ShoppingCart } from 'lucide-react';
import PartTile from './PartTile';

const PartsSearch = ({ year, make, model, vehicleId }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Generate default search query
  const defaultSearchQuery = `${year} ${make} ${model} parts`.trim();

  const handleSearch = async (customQuery = '') => {
    const query = customQuery || searchTerm || defaultSearchQuery;
    
    if (!query.trim()) {
      setError('Please enter a search term');
      return;
    }

    try {
      setLoading(true);
      setError('');
      
      // Simulate Google search results - In a real implementation, you'd use:
      // - Google Custom Search API
      // - SerpAPI
      // - Or another search service
      const mockResults = await simulateGoogleSearch(query);
      setSearchResults(mockResults);
    } catch (err) {
      console.error('Search error:', err);
      setError('Failed to search for parts. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handlePartAdd = async (part) => {
    try {
      // Here you would call your API to save the part to the vehicle
      console.log('Adding part to vehicle:', vehicleId, part);
      // Example: await apiService.addPartToVehicle(vehicleId, part);
      
      // For now, just show a success message
      // In a real implementation, you'd update the SavedPartsList
    } catch (err) {
      console.error('Error adding part:', err);
    }
  };

  return (
    <div className="space-y-4">
      {/* Search Input */}
      <div className="space-y-3">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-slate-400" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
            className="w-full pl-10 pr-4 py-3 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            placeholder={`Search parts for ${year} ${make} ${model}...`}
          />
        </div>
        
        <div className="flex gap-2">
          <button
            onClick={() => handleSearch()}
            disabled={loading}
            className="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? (
              <Loader className="w-4 h-4 animate-spin" />
            ) : (
              <Search className="w-4 h-4" />
            )}
            Search Parts
          </button>
          
          <button
            onClick={() => handleSearch(defaultSearchQuery)}
            disabled={loading}
            className="px-4 py-2 bg-slate-600 text-slate-300 rounded-lg hover:bg-slate-500 hover:text-white transition-colors"
          >
            Search Default
          </button>
        </div>
      </div>

      {/* Quick Search Suggestions */}
      <div className="space-y-2">
        <p className="text-sm text-slate-400">Quick searches:</p>
        <div className="flex flex-wrap gap-2">
          {[
            `${year} ${make} ${model} brake pads`,
            `${year} ${make} ${model} oil filter`,
            `${year} ${make} ${model} air filter`,
            `${year} ${make} ${model} spark plugs`,
            `${year} ${make} ${model} headlight bulbs`
          ].map((suggestion, index) => (
            <button
              key={index}
              onClick={() => handleSearch(suggestion)}
              disabled={loading}
              className="px-3 py-1 bg-slate-700/50 text-slate-300 rounded-md hover:bg-slate-600 hover:text-white transition-colors text-sm disabled:opacity-50"
            >
              {suggestion.split(' ').slice(-2).join(' ')}
            </button>
          ))}
        </div>
      </div>

      {/* Error Message */}
      {error && (
        <div className="flex items-center gap-2 p-3 bg-red-500/10 border border-red-500/20 rounded-lg text-red-400">
          <AlertCircle className="w-5 h-5 flex-shrink-0" />
          <span className="text-sm">{error}</span>
        </div>
      )}

      {/* Loading State */}
      {loading && (
        <div className="flex items-center justify-center py-8">
          <div className="text-center">
            <Loader className="w-6 h-6 text-primary-500 animate-spin mx-auto mb-2" />
            <p className="text-slate-400 text-sm">Searching for parts...</p>
          </div>
        </div>
      )}

      {/* Search Results */}
      {!loading && searchResults.length > 0 && (
        <div className="space-y-4">
          <h3 className="text-lg font-medium text-white flex items-center gap-2">
            <ShoppingCart className="w-5 h-5" />
            Search Results ({searchResults.length})
          </h3>
          
          <div className="grid grid-cols-1 gap-3 max-h-96 overflow-y-auto">
            {searchResults.map((part, index) => (
              <PartTile
                key={index}
                part={part}
                onAdd={() => handlePartAdd(part)}
              />
            ))}
          </div>
        </div>
      )}

      {/* No Results */}
      {!loading && searchResults.length === 0 && searchTerm && (
        <div className="text-center py-8">
          <ShoppingCart className="w-12 h-12 text-slate-600 mx-auto mb-3" />
          <p className="text-slate-400">No parts found. Try different search terms.</p>
        </div>
      )}
    </div>
  );
};

// Simulate Google search results
const simulateGoogleSearch = async (query) => {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 1000));
  
  // Mock search results - In real implementation, replace with actual search API
  const mockParts = [
    {
      id: `part-${Date.now()}-1`,
      title: "Premium Brake Pads Set",
      description: "High-performance ceramic brake pads for superior stopping power and reduced dust.",
      price: "$89.99",
      source: "AutoZone",
      url: "https://autozone.com/brake-pads-example",
      image: "https://images.unsplash.com/photo-1486262715619-67b85e0b08d3?w=300&h=200&fit=crop",
      rating: 4.5,
      inStock: true
    },
    {
      id: `part-${Date.now()}-2`,
      title: "OEM Oil Filter",
      description: "Original Equipment Manufacturer oil filter for optimal engine protection.",
      price: "$24.99",
      source: "Parts Geek",
      url: "https://partsgeek.com/oil-filter-example",
      image: "https://images.unsplash.com/photo-1581833971358-2c8b550f87b3?w=300&h=200&fit=crop",
      rating: 4.8,
      inStock: true
    },
    {
      id: `part-${Date.now()}-3`,
      title: "Air Filter Assembly",
      description: "High-flow air filter for improved engine performance and fuel efficiency.",
      price: "$45.50",
      source: "RockAuto",
      url: "https://rockauto.com/air-filter-example",
      image: "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=300&h=200&fit=crop",
      rating: 4.3,
      inStock: false
    },
    {
      id: `part-${Date.now()}-4`,
      title: "LED Headlight Bulbs",
      description: "Ultra-bright LED headlight bulbs with 6000K color temperature.",
      price: "$129.99",
      source: "Amazon",
      url: "https://amazon.com/led-headlights-example",
      image: "https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=300&h=200&fit=crop",
      rating: 4.6,
      inStock: true
    },
    {
      id: `part-${Date.now()}-5`,
      title: "Spark Plug Set (4pcs)",
      description: "Iridium spark plugs for enhanced ignition and extended service life.",
      price: "$67.99",
      source: "O'Reilly Auto Parts",
      url: "https://oreillyauto.com/spark-plugs-example",
      image: "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=300&h=200&fit=crop",
      rating: 4.7,
      inStock: true
    }
  ];
  
  // Filter results based on query for more realistic simulation
  return mockParts.filter(part => 
    part.title.toLowerCase().includes(query.split(' ').slice(-1)[0].toLowerCase()) ||
    part.description.toLowerCase().includes(query.split(' ').slice(-1)[0].toLowerCase())
  );
};

export default PartsSearch;