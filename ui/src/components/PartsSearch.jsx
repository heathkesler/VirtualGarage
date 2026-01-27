import React, { useState } from 'react';
import { Search, Loader, AlertCircle, Plus, ExternalLink, ShoppingCart, Sparkles, Bot } from 'lucide-react';
import PartTile from './PartTile';
import apiService from '../services/api';

const PartsSearch = ({ year, make, model, vehicleId }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [summary, setSummary] = useState('');
  const [llmProvider, setLlmProvider] = useState('');

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
      setSearchResults([]);
      setSummary('');
      
      // Call the AI-powered parts search API
      const response = await apiService.searchParts(vehicleId, query, {
        includePricing: true,
        includeInstallationInfo: true,
        maxResults: 10,
      });
      
      // Transform API response to match our PartTile format
      const transformedResults = response.suggestions.map((part, index) => ({
        id: `ai-part-${Date.now()}-${index}`,
        title: part.name,
        description: part.description,
        price: part.priceRange || 'Price varies',
        source: part.brand || 'Multiple brands',
        partNumber: part.partNumber,
        installationDifficulty: part.installationDifficulty,
        notes: part.notes,
        whereToBuy: part.whereToBuy || [],
        rating: null, // AI doesn't provide ratings
        inStock: null, // AI doesn't know real-time stock
        isAiGenerated: true,
      }));
      
      setSearchResults(transformedResults);
      setSummary(response.summary);
      setLlmProvider(response.llmProvider);
      
    } catch (err) {
      console.error('Search error:', err);
      if (err.message.includes('LLM') || err.message.includes('API key')) {
        setError('AI service not configured. Please set up your Claude API key.');
      } else if (err.message.includes('Vehicle not found')) {
        setError('Vehicle not found. Please refresh and try again.');
      } else {
        setError('Failed to search for parts. Please try again.');
      }
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
      {/* AI Badge */}
      <div className="flex items-center gap-2 text-sm text-primary-400">
        <Sparkles className="w-4 h-4" />
        <span>AI-Powered Parts Search</span>
        {llmProvider && (
          <span className="px-2 py-0.5 bg-primary-500/20 rounded-full text-xs capitalize">
            {llmProvider}
          </span>
        )}
      </div>

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
            placeholder={`Ask AI: What parts do you need for your ${year} ${make} ${model}?`}
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
              <Bot className="w-4 h-4" />
            )}
            Ask AI
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
            `brake pads and rotors`,
            `oil change kit`,
            `air filter`,
            `spark plugs`,
            `headlight bulbs`,
            `suspension upgrades`,
            `performance exhaust`
          ].map((suggestion, index) => (
            <button
              key={index}
              onClick={() => handleSearch(suggestion)}
              disabled={loading}
              className="px-3 py-1 bg-slate-700/50 text-slate-300 rounded-md hover:bg-slate-600 hover:text-white transition-colors text-sm disabled:opacity-50"
            >
              {suggestion}
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
            <Bot className="w-8 h-8 text-primary-500 animate-pulse mx-auto mb-2" />
            <Loader className="w-6 h-6 text-primary-500 animate-spin mx-auto mb-2" />
            <p className="text-slate-400 text-sm">AI is analyzing parts for your {year} {make} {model}...</p>
            <p className="text-slate-500 text-xs mt-1">This may take a few seconds</p>
          </div>
        </div>
      )}

      {/* Summary */}
      {!loading && summary && (
        <div className="p-3 bg-primary-500/10 border border-primary-500/20 rounded-lg">
          <div className="flex items-start gap-2">
            <Sparkles className="w-5 h-5 text-primary-400 mt-0.5 flex-shrink-0" />
            <p className="text-primary-300 text-sm">{summary}</p>
          </div>
        </div>
      )}

      {/* Search Results */}
      {!loading && searchResults.length > 0 && (
        <div className="space-y-4">
          <h3 className="text-lg font-medium text-white flex items-center gap-2">
            <ShoppingCart className="w-5 h-5" />
            AI Suggestions ({searchResults.length})
          </h3>
          
          <div className="grid grid-cols-1 gap-3 max-h-[32rem] overflow-y-auto pr-2">
            {searchResults.map((part, index) => (
              <PartTile
                key={part.id || index}
                part={part}
                onAdd={() => handlePartAdd(part)}
                isAiGenerated={true}
              />
            ))}
          </div>
        </div>
      )}

      {/* No Results */}
      {!loading && searchResults.length === 0 && searchTerm && !error && (
        <div className="text-center py-8">
          <ShoppingCart className="w-12 h-12 text-slate-600 mx-auto mb-3" />
          <p className="text-slate-400">No parts found. Try different search terms.</p>
        </div>
      )}
    </div>
  );
};

export default PartsSearch;
