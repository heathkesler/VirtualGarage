import React, { useState, useEffect } from 'react';
import { X, Save, Loader, AlertCircle } from 'lucide-react';

const VehicleModal = ({ isOpen, onClose, onSave, vehicle = null }) => {
  const [formData, setFormData] = useState({
    name: '',
    make: '',
    model: '',
    year: new Date().getFullYear(),
    type: '',
    color: '',
    mileage: '',
    value: '',
    status: 'Excellent',
    image: '',
    description: '',
    tags: []
  });
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (vehicle) {
      const valueNumber = typeof vehicle.value === 'string' 
        ? vehicle.value.replace(/[$,]/g, '')
        : vehicle.value || '';
      
      const mileageNumber = typeof vehicle.mileage === 'string'
        ? vehicle.mileage.replace(/[^0-9]/g, '')
        : vehicle.mileage || '';

      setFormData({
        name: vehicle.name || '',
        make: vehicle.make || '',
        model: vehicle.model || '',
        year: vehicle.year || new Date().getFullYear(),
        type: vehicle.type || '',
        color: vehicle.color || '',
        mileage: mileageNumber,
        value: valueNumber,
        status: vehicle.status || 'Excellent',
        image: vehicle.image || '',
        description: vehicle.description || '',
        tags: vehicle.tags || []
      });
    } else {
      setFormData({
        name: '',
        make: '',
        model: '',
        year: new Date().getFullYear(),
        type: '',
        color: '',
        mileage: '',
        value: '',
        status: 'Excellent',
        image: '',
        description: '',
        tags: []
      });
    }
    setError('');
  }, [vehicle, isOpen]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.name || !formData.make || !formData.model || !formData.year || !formData.type) {
      setError('Please fill in all required fields (Name, Make, Model, Year, Type)');
      return;
    }
    
    try {
      setLoading(true);
      setError('');
      
      const vehicleData = {
        ...formData,
        name: formData.name || `${formData.year} ${formData.make} ${formData.model}`,
        year: parseInt(formData.year),
        mileage: formData.mileage ? parseInt(formData.mileage) : 0,
        value: formData.value ? parseFloat(formData.value) : 0
      };
      
      await onSave(vehicleData);
      onClose();
    } catch (err) {
      console.error('Error saving vehicle:', err);
      setError(err.message || 'Failed to save vehicle. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div className="bg-slate-800 rounded-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between p-6 border-b border-slate-700">
          <h2 className="text-xl font-semibold text-white">
            {vehicle ? 'Edit Vehicle' : 'Add New Vehicle'}
          </h2>
          <button
            onClick={onClose}
            className="p-2 text-slate-400 hover:text-white hover:bg-slate-700 rounded-lg transition-colors"
            disabled={loading}
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-6">
          {error && (
            <div className="flex items-center gap-2 p-3 bg-red-500/10 border border-red-500/20 rounded-lg text-red-400">
              <AlertCircle className="w-5 h-5 flex-shrink-0" />
              <span className="text-sm">{error}</span>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Vehicle Name <span className="text-red-400">*</span>
              </label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="e.g., 2024 BMW M3 Competition"
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
                onChange={handleInputChange}
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
                <option value="Other">Other</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Make <span className="text-red-400">*</span>
              </label>
              <input
                type="text"
                name="make"
                value={formData.make}
                onChange={handleInputChange}
                className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="e.g., BMW"
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
                onChange={handleInputChange}
                className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="e.g., M3 Competition"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Year <span className="text-red-400">*</span>
              </label>
              <input
                type="number"
                name="year"
                value={formData.year}
                onChange={handleInputChange}
                className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                min="1900"
                max={new Date().getFullYear() + 1}
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Color
              </label>
              <input
                type="text"
                name="color"
                value={formData.color}
                onChange={handleInputChange}
                className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="e.g., Alpine White"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Mileage
              </label>
              <input
                type="number"
                name="mileage"
                value={formData.mileage}
                onChange={handleInputChange}
                className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="e.g., 15000"
                min="0"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Value ($)
              </label>
              <input
                type="number"
                name="value"
                value={formData.value}
                onChange={handleInputChange}
                className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="e.g., 89500"
                min="0"
                step="0.01"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Status
              </label>
              <select
                name="status"
                value={formData.status}
                onChange={handleInputChange}
                className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
                <option value="Excellent">Excellent</option>
                <option value="Good">Good</option>
                <option value="Fair">Fair</option>
                <option value="Poor">Poor</option>
                <option value="Pristine">Pristine</option>
                <option value="Concours">Concours</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-2">
                Image URL
              </label>
              <input
                type="url"
                name="image"
                value={formData.image}
                onChange={handleInputChange}
                className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="https://example.com/image.jpg"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-300 mb-2">
              Description
            </label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              rows={3}
              className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              placeholder="Add any additional details about this vehicle..."
            />
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-700">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-slate-400 hover:text-white transition-colors"
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex items-center gap-2 px-6 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? (
                <Loader className="w-4 h-4 animate-spin" />
              ) : (
                <Save className="w-4 h-4" />
              )}
              {vehicle ? 'Update Vehicle' : 'Add Vehicle'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default VehicleModal;