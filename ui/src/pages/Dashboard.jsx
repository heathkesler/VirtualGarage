import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { 
  Car, 
  Home, 
  BarChart3, 
  Settings, 
  Plus, 
  Search, 
  DollarSign,
  TrendingUp,
  ChevronRight,
  AlertCircle,
  Loader,
  Eye,
  Edit2,
  Trash2,
  MoreVertical
} from 'lucide-react';
import apiService from '../services/api';
import { transformPagedResponse, transformDashboardStatsFromAPI, transformVehicleToAPI } from '../utils/dataTransform';
import VehicleModal from '../components/VehicleModal';
import ConfirmModal from '../components/ConfirmModal';

const Dashboard = () => {
  const navigate = useNavigate();
  const [selectedFilter, setSelectedFilter] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [vehicles, setVehicles] = useState([]);
  const [garageStats, setGarageStats] = useState({ totalVehicles: 0, totalValue: 0, avgValue: () => 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showVehicleModal, setShowVehicleModal] = useState(false);
  const [editingVehicle, setEditingVehicle] = useState(null);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [vehicleToDelete, setVehicleToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);

  // Load vehicles and stats on component mount
  useEffect(() => {
    loadData();
  }, []);

  // Load data when search term or filter changes
  useEffect(() => {
    loadVehicles();
  }, [searchTerm, selectedFilter]);

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Load both vehicles and stats in parallel
      const [, statsResponse] = await Promise.all([
        loadVehicles(false),
        apiService.getDashboardStats()
      ]);
      
      if (statsResponse) {
        setGarageStats(transformDashboardStatsFromAPI(statsResponse));
      }
    } catch (err) {
      console.error('Error loading dashboard data:', err);
      setError('Failed to load dashboard data. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const loadVehicles = async (showLoading = true) => {
    try {
      if (showLoading) {
        setLoading(true);
        setError(null);
      }
      
      let response;
      
      if (searchTerm && selectedFilter !== 'all') {
        const filterMap = {
          'classic': 'Classic',
          'performance': 'Performance', 
          'electric': 'Electric',
          'exotic': 'Exotic'
        };
        
        response = await apiService.filterVehicles({
          search: searchTerm,
          type: filterMap[selectedFilter]
        });
      } else if (searchTerm) {
        response = await apiService.searchVehicles(searchTerm);
      } else if (selectedFilter !== 'all') {
        const filterMap = {
          'classic': 'Classic',
          'performance': 'Performance',
          'electric': 'Electric', 
          'exotic': 'Exotic'
        };
        
        response = await apiService.getVehiclesByTag(filterMap[selectedFilter]);
      } else {
        response = await apiService.getAllVehicles();
      }
      
      const transformedResponse = transformPagedResponse(response);
      setVehicles(transformedResponse.content);
      
      return response;
    } catch (err) {
      console.error('Error loading vehicles:', err);
      if (showLoading) {
        setError('Failed to load vehicles. Please try again.');
      }
      return null;
    } finally {
      if (showLoading) {
        setLoading(false);
      }
    }
  };

  const handleAddVehicle = () => {
    setEditingVehicle(null);
    setShowVehicleModal(true);
  };

  const handleViewVehicleDetails = (vehicle) => {
    setEditingVehicle(vehicle);
    setShowVehicleModal(true);
  };

  const handleSaveVehicle = async (vehicleData) => {
    try {
      const apiData = transformVehicleToAPI(vehicleData);
      
      if (editingVehicle) {
        await apiService.updateVehicle(editingVehicle.id, apiData);
      } else {
        await apiService.createVehicle(apiData);
      }
      
      await loadData();
      setShowVehicleModal(false);
      setEditingVehicle(null);
    } catch (error) {
      console.error('Error saving vehicle:', error);
      throw error;
    }
  };

  const handleDeleteVehicle = (vehicle) => {
    setVehicleToDelete(vehicle);
    setShowConfirmModal(true);
  };

  const confirmDeleteVehicle = async () => {
    if (!vehicleToDelete) return;
    
    try {
      setDeleting(true);
      await apiService.deleteVehicle(vehicleToDelete.id);
      await loadData();
      setShowConfirmModal(false);
      setVehicleToDelete(null);
    } catch (error) {
      console.error('Error deleting vehicle:', error);
      setError('Failed to delete vehicle. Please try again.');
    } finally {
      setDeleting(false);
    }
  };

  const handleEditVehicle = (vehicleId) => {
    navigate(`/vehicle/edit/${vehicleId}`);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center">
        <div className="text-center">
          <Loader className="w-8 h-8 text-primary-500 animate-spin mx-auto mb-4" />
          <p className="text-slate-400">Loading your garage...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center">
        <div className="text-center max-w-md">
          <AlertCircle className="w-12 h-12 text-red-400 mx-auto mb-4" />
          <h2 className="text-xl font-semibold text-white mb-2">Something went wrong</h2>
          <p className="text-slate-400 mb-4">{error}</p>
          <button 
            onClick={loadData}
            className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
          >
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950">
      {/* Sidebar */}
      <div className="fixed inset-y-0 left-0 w-64 bg-slate-900 border-r border-slate-800">
        <div className="flex flex-col h-full">
          {/* Logo */}
          <div className="flex items-center space-x-2 p-6 border-b border-slate-800">
            <div className="p-2 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl">
              <Car className="w-6 h-6 text-white" />
            </div>
            <span className="text-lg font-bold bg-gradient-to-r from-white to-slate-300 bg-clip-text text-transparent">
              Virtual Garage
            </span>
          </div>

          {/* Navigation */}
          <nav className="flex-1 px-4 py-6 space-y-2">
            <NavItem icon={<Home />} label="Dashboard" active />
            <NavItem icon={<Car />} label="My Vehicles" />
            <NavItem icon={<BarChart3 />} label="Analytics" />
            <NavItem icon={<Settings />} label="Settings" />
          </nav>

          {/* User Section */}
          <div className="p-4 border-t border-slate-800">
            <Link 
              to="/"
              className="flex items-center space-x-3 p-3 rounded-lg hover:bg-slate-800 transition-colors"
            >
              <div className="w-8 h-8 bg-gradient-to-br from-primary-500 to-primary-600 rounded-full flex items-center justify-center">
                <span className="text-sm font-semibold text-white">JD</span>
              </div>
              <div className="flex-1">
                <p className="text-sm font-medium text-white">John Doe</p>
                <p className="text-xs text-slate-400">Premium Member</p>
              </div>
            </Link>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="ml-64">
        {/* Header */}
        <header className="bg-slate-900/50 backdrop-blur-sm border-b border-slate-800 px-6 py-4">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-bold text-white mb-1">My Garage</h1>
              <p className="text-slate-400">Manage and showcase your vehicle collection</p>
            </div>
            <button 
              onClick={handleAddVehicle}
              className="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-primary-600 to-primary-700 text-white rounded-lg font-medium hover:from-primary-700 hover:to-primary-800 transition-all duration-200 shadow-lg shadow-primary-500/25"
            >
              <Plus className="w-4 h-4" />
              Add Vehicle
            </button>
          </div>
        </header>

        {/* Stats Cards */}
        <div className="p-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <StatsCard
              icon={<Car className="w-6 h-6 text-blue-400" />}
              title="Total Vehicles"
              value={garageStats.totalVehicles}
              change="+2 this month"
              positive
            />
            <StatsCard
              icon={<DollarSign className="w-6 h-6 text-green-400" />}
              title="Total Value"
              value={`$${(garageStats.totalValue / 1000000).toFixed(1)}M`}
              change="+5.2% this year"
              positive
            />
            <StatsCard
              icon={<TrendingUp className="w-6 h-6 text-purple-400" />}
              title="Average Value"
              value={`$${(garageStats.avgValue() / 1000).toFixed(0)}K`}
              change="Market trending"
              positive
            />
          </div>

          {/* Search and Filters */}
          <div className="flex flex-col md:flex-row gap-4 mb-8">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-slate-400" />
              <input
                type="text"
                placeholder="Search vehicles..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-3 bg-slate-800/50 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-all"
              />
            </div>
            <div className="flex gap-2">
              <FilterButton 
                label="All" 
                active={selectedFilter === 'all'} 
                onClick={() => setSelectedFilter('all')}
              />
              <FilterButton 
                label="Classic" 
                active={selectedFilter === 'classic'} 
                onClick={() => setSelectedFilter('classic')}
              />
              <FilterButton 
                label="Performance" 
                active={selectedFilter === 'performance'} 
                onClick={() => setSelectedFilter('performance')}
              />
              <FilterButton 
                label="Electric" 
                active={selectedFilter === 'electric'} 
                onClick={() => setSelectedFilter('electric')}
              />
            </div>
          </div>

          {/* Vehicle Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {vehicles.map((vehicle) => (
              <VehicleCard 
                key={vehicle.id} 
                vehicle={vehicle} 
                onViewDetails={() => handleViewVehicleDetails(vehicle)}
                onEdit={handleEditVehicle}
                onDelete={() => handleDeleteVehicle(vehicle)}
              />
            ))}
          </div>

          {vehicles.length === 0 && (
            <div className="text-center py-12">
              <Car className="w-16 h-16 text-slate-600 mx-auto mb-4" />
              <h3 className="text-xl font-semibold text-slate-400 mb-2">No vehicles found</h3>
              <p className="text-slate-500">Try adjusting your search or filter criteria</p>
            </div>
          )}
        </div>
      </div>

      {/* Vehicle Modal */}
      <VehicleModal
        isOpen={showVehicleModal}
        onClose={() => {
          setShowVehicleModal(false);
          setEditingVehicle(null);
        }}
        onSave={handleSaveVehicle}
        onDelete={handleDeleteVehicle}
        vehicle={editingVehicle}
      />

      {/* Delete Confirmation Modal */}
      <ConfirmModal
        isOpen={showConfirmModal}
        onClose={() => {
          setShowConfirmModal(false);
          setVehicleToDelete(null);
        }}
        onConfirm={confirmDeleteVehicle}
        title="Delete Vehicle"
        message={vehicleToDelete ? `Are you sure you want to delete "${vehicleToDelete.name}"? This action cannot be undone.` : ''}
        confirmText="Delete Vehicle"
        cancelText="Cancel"
        confirmVariant="danger"
        loading={deleting}
      />
    </div>
  );
};

const NavItem = ({ icon, label, active = false }) => (
  <div className={`flex items-center space-x-3 px-4 py-3 rounded-lg cursor-pointer transition-colors ${
    active ? 'bg-primary-600/20 text-primary-400 border-r-2 border-primary-500' : 'text-slate-400 hover:text-white hover:bg-slate-800'
  }`}>
    {React.cloneElement(icon, { className: 'w-5 h-5' })}
    <span className="font-medium">{label}</span>
  </div>
);

const StatsCard = ({ icon, title, value, change, positive = true }) => (
  <div className="bg-gradient-to-br from-slate-800/50 to-slate-700/30 backdrop-blur-sm border border-slate-600 rounded-xl p-6 hover:border-slate-500 transition-all duration-300">
    <div className="flex items-center justify-between mb-4">
      <div className="p-3 bg-slate-700/50 rounded-lg">
        {icon}
      </div>
      <ChevronRight className="w-5 h-5 text-slate-400" />
    </div>
    <h3 className="text-2xl font-bold text-white mb-1">{value}</h3>
    <p className="text-sm text-slate-400 mb-2">{title}</p>
    <div className={`flex items-center text-sm ${positive ? 'text-green-400' : 'text-red-400'}`}>
      <TrendingUp className="w-4 h-4 mr-1" />
      {change}
    </div>
  </div>
);

const FilterButton = ({ label, active, onClick }) => (
  <button
    onClick={onClick}
    className={`px-4 py-2 rounded-lg font-medium transition-all ${
      active 
        ? 'bg-primary-600 text-white' 
        : 'bg-slate-800/50 text-slate-400 hover:text-white hover:bg-slate-700/50'
    }`}
  >
    {label}
  </button>
);

const VehicleCard = ({ vehicle, onViewDetails, onEdit, onDelete }) => {
  
  return (
    <div className="group bg-gradient-to-br from-slate-800/50 to-slate-700/30 backdrop-blur-sm border border-slate-600 rounded-xl overflow-hidden hover:border-slate-500 transition-all duration-300 hover:shadow-xl hover:shadow-slate-900/50">
      <div className="relative overflow-hidden">
        <img 
          src={vehicle.image} 
          alt={vehicle.name}
          className="w-full h-48 object-cover group-hover:scale-105 transition-transform duration-500"
          onError={(e) => {
            e.target.src = 'https://images.unsplash.com/photo-1494905998402-395d579af36f?w=800&h=600&fit=crop&crop=center';
          }}
        />
        <div className="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
        
        {/* Status Badge */}
        <div className="absolute top-3 left-3">
          <div className={`px-2 py-1 rounded-full text-xs font-medium ${
            vehicle.status === 'Pristine' ? 'bg-green-500/20 text-green-400' :
            vehicle.status === 'Excellent' ? 'bg-blue-500/20 text-blue-400' :
            vehicle.status === 'Concours' ? 'bg-purple-500/20 text-purple-400' :
            'bg-yellow-500/20 text-yellow-400'
          }`}>
            {vehicle.status}
          </div>
        </div>
        
        {/* Action Buttons - Always Visible on Hover */}
        <div className="absolute top-3 right-3 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
          <div className="flex gap-2">
            <button
              onClick={(e) => {
                e.stopPropagation();
                onViewDetails?.();
              }}
              className="p-2 bg-blue-600/80 backdrop-blur-sm rounded-full text-white hover:bg-blue-600 transition-all shadow-lg"
              title="View Details"
            >
              <Eye className="w-4 h-4" />
            </button>
            <button
              onClick={(e) => {
                e.stopPropagation();
                onEdit?.(vehicle.id);
              }}
              className="p-2 bg-green-600/80 backdrop-blur-sm rounded-full text-white hover:bg-green-600 transition-all shadow-lg"
              title="Edit Vehicle"
            >
              <Edit2 className="w-4 h-4" />
            </button>
            <button
              onClick={(e) => {
                e.stopPropagation();
                onDelete?.(vehicle);
              }}
              className="p-2 bg-red-600/80 backdrop-blur-sm rounded-full text-white hover:bg-red-600 transition-all shadow-lg"
              title="Delete Vehicle"
            >
              <Trash2 className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
      
      <div className="p-6">
        <h3 className="text-lg font-semibold text-white mb-2 group-hover:text-primary-400 transition-colors">
          {vehicle.name}
        </h3>
        <p className="text-slate-400 text-sm mb-4">{vehicle.type} • {vehicle.year}</p>
        
        <div className="flex items-center justify-between mb-4">
          <div>
            <p className="text-slate-400 text-xs">Mileage</p>
            <p className="text-white text-sm font-medium">{vehicle.mileage}</p>
          </div>
          <div className="text-right">
            <p className="text-slate-400 text-xs">Value</p>
            <p className="text-primary-400 text-lg font-bold">{vehicle.value}</p>
          </div>
        </div>
        
        <div className="flex flex-wrap gap-1 mb-4">
          {vehicle.tags && vehicle.tags.map((tag) => (
            <span 
              key={tag}
              className="px-2 py-1 bg-slate-700/50 text-slate-300 text-xs rounded-md"
            >
              {tag}
            </span>
          ))}
        </div>
        
        {/* Action Buttons Bar */}
        <div className="flex gap-2 pt-4 border-t border-slate-600/50">
          <button
            onClick={(e) => {
              e.stopPropagation();
              onViewDetails?.();
            }}
            className="flex items-center gap-2 flex-1 px-3 py-2 bg-slate-600/50 text-slate-300 rounded-lg hover:bg-slate-600 hover:text-white transition-all text-sm font-medium"
          >
            <Eye className="w-4 h-4" />
            Details
          </button>
          <button
            onClick={(e) => {
              e.stopPropagation();
              onEdit?.(vehicle.id);
            }}
            className="flex items-center gap-2 flex-1 px-3 py-2 bg-green-600/20 text-green-400 rounded-lg hover:bg-green-600/30 hover:text-green-300 transition-all text-sm font-medium"
          >
            <Edit2 className="w-4 h-4" />
            Edit
          </button>
          <button
            onClick={(e) => {
              e.stopPropagation();
              onDelete?.(vehicle);
            }}
            className="flex items-center gap-2 flex-1 px-3 py-2 bg-red-600/20 text-red-400 rounded-lg hover:bg-red-600/30 hover:text-red-300 transition-all text-sm font-medium"
          >
            <Trash2 className="w-4 h-4" />
            Remove
          </button>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;