import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  ArrowLeft, 
  Save, 
  Loader, 
  AlertCircle, 
  Camera, 
  Sparkles,
  Search,
  Plus,
  Trash2,
  ExternalLink,
  Package,
  ShoppingCart
} from 'lucide-react';
import apiService from '../services/api';
import { transformVehicleToAPI } from '../utils/dataTransform';
import PartsSearch from '../components/PartsSearch';
import SavedPartsList from '../components/SavedPartsList';

const VehicleEdit = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [vehicle, setVehicle] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('details');

  const [formData, setFormData] = useState({
    name: '',
    make: '',
    model: '',
    year: new Date().getFullYear(),
    type: '',
    color: '',
    engine: '',
    transmission: '',
    drivetrain: '',
    mileage: '',
    value: '',
    status: 'Excellent',
    image: '',
    description: '',
    notes: '',
    tags: [],
    engineSize: '',
    fuelType: '',
    mileageUnit: 'miles',
    purchasePrice: '',
    purchaseDate: '',
    vinNumber: '',
    licensePlate: ''
  });

  useEffect(() => {
    if (id && id !== 'new') {
      loadVehicle();
    } else {
      setLoading(false);
    }
  }, [id]);

  const loadVehicle = async () => {
    try {
      setLoading(true);
      const vehicleData = await apiService.getVehicleById(id);
      setVehicle(vehicleData);
      
      // Populate form with vehicle data
      setFormData({
        name: vehicleData.name || '',
        make: vehicleData.make || '',
        model: vehicleData.model || '',
        year: vehicleData.year || new Date().getFullYear(),
        type: vehicleData.type || '',
        color: vehicleData.color || '',
        engine: vehicleData.engine || '',
        transmission: vehicleData.transmission || '',
        drivetrain: vehicleData.drivetrain || '',
        mileage: vehicleData.mileage || '',
        value: vehicleData.value || '',
        status: vehicleData.status || 'Excellent',
        image: vehicleData.primary_image_url || vehicleData.image || '',
        description: vehicleData.description || '',
        notes: vehicleData.notes || '',
        tags: vehicleData.tags || [],
        engineSize: vehicleData.engine_size || '',
        fuelType: vehicleData.fuel_type || '',
        mileageUnit: vehicleData.mileage_unit || 'miles',
        purchasePrice: vehicleData.purchase_price || '',
        purchaseDate: vehicleData.purchase_date || '',
        vinNumber: vehicleData.vin_number || '',
        licensePlate: vehicleData.license_plate || ''
      });
    } catch (err) {
      console.error('Error loading vehicle:', err);
      setError('Failed to load vehicle data');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleTagsChange = (e) => {
    const value = e.target.value;
    const tags = value.split(',').map(tag => tag.trim()).filter(tag => tag);
    setFormData(prev => ({
      ...prev,
      tags
    }));
  };

  const handleSave = async () => {
    if (!formData.name || !formData.make || !formData.model || !formData.year || !formData.type) {
      setError('Please fill in all required fields (Name, Make, Model, Year, Type)');
      return;
    }

    try {
      setSaving(true);
      setError('');
      
      const vehicleData = {
        ...formData,
        year: parseInt(formData.year),
        mileage: formData.mileage ? parseInt(formData.mileage) : 0,
        value: formData.value ? parseFloat(formData.value) : 0,
        purchasePrice: formData.purchasePrice ? parseFloat(formData.purchasePrice) : null
      };
      
      const apiData = transformVehicleToAPI(vehicleData);
      
      if (id && id !== 'new') {
        await apiService.updateVehicle(id, apiData);
      } else {
        const newVehicle = await apiService.createVehicle(apiData);
        navigate(`/vehicle/edit/${newVehicle.id}`, { replace: true });
        return;
      }
      
      // Reload vehicle data to get updated info
      await loadVehicle();
    } catch (err) {
      console.error('Error saving vehicle:', err);
      setError(err.message || 'Failed to save vehicle');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center">
        <div className="text-center">
          <Loader className="w-8 h-8 text-primary-500 animate-spin mx-auto mb-4" />
          <p className="text-slate-400">Loading vehicle...</p>
        </div>
      </div>
    );
  }

  const isNewVehicle = !id || id === 'new';

  return (
    <div className="min-h-screen bg-slate-950">
      {/* Header */}
      <div className="bg-slate-900/50 backdrop-blur-sm border-b border-slate-800 px-6 py-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <button
              onClick={() => navigate('/dashboard')}
              className="p-2 text-slate-400 hover:text-white hover:bg-slate-800 rounded-lg transition-colors"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
            <div>
              <h1 className="text-2xl font-bold text-white mb-1">
                {isNewVehicle ? 'Add New Vehicle' : `Edit ${vehicle?.name || 'Vehicle'}`}
              </h1>
              <p className="text-slate-400">
                {isNewVehicle ? 'Create a new vehicle in your garage' : 'Update vehicle details and manage parts'}
              </p>
            </div>
          </div>
          <button
            onClick={handleSave}
            disabled={saving}
            className="flex items-center gap-2 px-6 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {saving ? (
              <Loader className="w-4 h-4 animate-spin" />
            ) : (
              <Save className="w-4 h-4" />
            )}
            {saving ? 'Saving...' : 'Save Changes'}
          </button>
        </div>
      </div>

      <div className="flex">
        {/* Sidebar with tabs */}
        <div className="w-64 bg-slate-900 border-r border-slate-800 min-h-screen">
          <div className="p-4">
            <nav className="space-y-2">
              <button
                onClick={() => setActiveTab('details')}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg text-left transition-colors ${
                  activeTab === 'details'
                    ? 'bg-primary-600/20 text-primary-400 border-r-2 border-primary-500'
                    : 'text-slate-400 hover:text-white hover:bg-slate-800'
                }`}
              >
                <Camera className="w-5 h-5" />
                Vehicle Details
              </button>
              {!isNewVehicle && (
                <button
                  onClick={() => setActiveTab('parts')}
                  className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg text-left transition-colors ${
                    activeTab === 'parts'
                      ? 'bg-primary-600/20 text-primary-400 border-r-2 border-primary-500'
                      : 'text-slate-400 hover:text-white hover:bg-slate-800'
                  }`}
                >
                  <Package className="w-5 h-5" />
                  Parts & Components
                </button>
              )}
            </nav>
          </div>
        </div>

        {/* Main content */}
        <div className="flex-1 p-6">
          {error && (
            <div className="mb-6 flex items-center gap-2 p-4 bg-red-500/10 border border-red-500/20 rounded-lg text-red-400">
              <AlertCircle className="w-5 h-5 flex-shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {activeTab === 'details' && (
            <VehicleDetailsForm 
              formData={formData}
              onChange={handleInputChange}
              onTagsChange={handleTagsChange}
            />
          )}

          {activeTab === 'parts' && !isNewVehicle && (
            <PartsManagement 
              vehicle={vehicle}
              vehicleId={id}
            />
          )}
        </div>
      </div>
    </div>
  );
};

// Vehicle Details Form Component
const VehicleDetailsForm = ({ formData, onChange, onTagsChange }) => {
  return (
    <div className="max-w-4xl">
      <div className="bg-slate-800/50 backdrop-blur-sm border border-slate-700 rounded-xl p-6">
        <h2 className="text-xl font-semibold text-white mb-6">Vehicle Information</h2>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Basic Information */}
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Vehicle Name <span className="text-red-400">*</span>
              </label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={onChange}
                className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="e.g., 2024 BMW M3 Competition"
                required
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-2">
                  Make <span className="text-red-400">*</span>
                </label>
                <input
                  type="text"
                  name="make"
                  value={formData.make}
                  onChange={onChange}
                  className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  placeholder="BMW"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-2">
                  Model <span className="text-red-400">*</span>
                </label>
                <input
                  type="text"
                  name="model"
                  value={formData.model}
                  onChange={onChange}
                  className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  placeholder="M3 Competition"
                  required
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-2">
                  Year <span className="text-red-400">*</span>
                </label>
                <input
                  type="number"
                  name="year"
                  value={formData.year}
                  onChange={onChange}
                  className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  min="1900"
                  max={new Date().getFullYear() + 1}
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-2">
                  Type <span className="text-red-400">*</span>
                </label>
                <select
                  name="type"
                  value={formData.type}
                  onChange={onChange}
                  className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  required
                >
                  <option value="">Select type...</option>
                  <option value="Sedan">Sedan</option>
                  <option value="SUV">SUV</option>
                  <option value="Sports Car">Sports Car</option>
                  <option value="Coupe">Coupe</option>
                  <option value="Convertible">Convertible</option>
                  <option value="Classic">Classic</option>
                  <option value="Electric">Electric</option>
                  <option value="Luxury">Luxury</option>
                  <option value="Truck">Truck</option>
                  <option value="Motorcycle">Motorcycle</option>
                  <option value="Other">Other</option>
                </select>
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">Color</label>
              <input
                type="text"
                name="color"
                value={formData.color}
                onChange={onChange}
                className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="e.g., Alpine White"
              />
            </div>
          </div>

          {/* Technical Specifications */}
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">Engine</label>
              <input
                type="text"
                name="engine"
                value={formData.engine}
                onChange={onChange}
                className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="e.g., Twin-Turbo 3.0L I6"
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-2">Transmission</label>
                <input
                  type="text"
                  name="transmission"
                  value={formData.transmission}
                  onChange={onChange}
                  className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  placeholder="e.g., Automatic"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-2">Drivetrain</label>
                <input
                  type="text"
                  name="drivetrain"
                  value={formData.drivetrain}
                  onChange={onChange}
                  className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  placeholder="e.g., RWD"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-2">Mileage</label>
                <input
                  type="number"
                  name="mileage"
                  value={formData.mileage}
                  onChange={onChange}
                  className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  placeholder="15000"
                  min="0"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-2">Value ($)</label>
                <input
                  type="number"
                  name="value"
                  value={formData.value}
                  onChange={onChange}
                  className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  placeholder="89500"
                  min="0"
                  step="0.01"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">Status</label>
              <select
                name="status"
                value={formData.status}
                onChange={onChange}
                className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="Excellent">Excellent</option>
                <option value="Good">Good</option>
                <option value="Fair">Fair</option>
                <option value="Poor">Poor</option>
                <option value="Pristine">Pristine</option>
                <option value="Concours">Concours</option>
                <option value="Project">Project</option>
              </select>
            </div>
          </div>
        </div>

        {/* Additional fields */}
        <div className="mt-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-2">
              <div className="flex items-center gap-2">
                <Camera className="w-4 h-4" />
                Image URL
                <span className="text-xs text-slate-400">(Optional)</span>
              </div>
            </label>
            <input
              type="url"
              name="image"
              value={formData.image}
              onChange={onChange}
              className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="https://example.com/image.jpg"
            />
            <div className="mt-2 p-2 bg-slate-700/50 rounded text-xs text-slate-400 flex items-center gap-2">
              <Sparkles className="w-3 h-3 text-primary-400" />
              <span>Leave empty to automatically find a matching vehicle image</span>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-300 mb-2">Description</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={onChange}
              rows={3}
              className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Add any additional details about this vehicle..."
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-300 mb-2">Tags (comma separated)</label>
            <input
              type="text"
              value={formData.tags.join(', ')}
              onChange={onTagsChange}
              className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Performance, Daily Driver, Classic"
            />
          </div>
        </div>
      </div>
    </div>
  );
};

// Parts Management Component
const PartsManagement = ({ vehicle, vehicleId }) => {
  return (
    <div className="max-w-6xl">
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Parts Search */}
        <div className="bg-slate-800/50 backdrop-blur-sm border border-slate-700 rounded-xl p-6">
          <h2 className="text-xl font-semibold text-white mb-6 flex items-center gap-2">
            <Search className="w-5 h-5" />
            Find Parts
          </h2>
          <PartsSearch 
            year={vehicle?.year}
            make={vehicle?.make}
            model={vehicle?.model}
            vehicleId={vehicleId}
          />
        </div>

        {/* Saved Parts */}
        <div className="bg-slate-800/50 backdrop-blur-sm border border-slate-700 rounded-xl p-6">
          <h2 className="text-xl font-semibold text-white mb-6 flex items-center gap-2">
            <Package className="w-5 h-5" />
            Saved Parts
          </h2>
          <SavedPartsList vehicleId={vehicleId} />
        </div>
      </div>
    </div>
  );
};

export default VehicleEdit;