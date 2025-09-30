const API_BASE_URL = 'http://localhost:8080/api';

class ApiService {
  constructor() {
    this.baseURL = API_BASE_URL;
  }

  async request(endpoint, options = {}) {
    const url = `${this.baseURL}${endpoint}`;
    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    };

    if (config.body && typeof config.body === 'object') {
      config.body = JSON.stringify(config.body);
    }

    try {
      const response = await fetch(url, config);
      
      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.error || `HTTP ${response.status}: ${response.statusText}`);
      }

      // Handle 204 No Content responses
      if (response.status === 204) {
        return null;
      }

      return await response.json();
    } catch (error) {
      console.error(`API request failed: ${endpoint}`, error);
      throw error;
    }
  }

  // Vehicle API methods
  async getAllVehicles(params = {}) {
    const queryString = new URLSearchParams(params).toString();
    const endpoint = queryString ? `/vehicles?${queryString}` : '/vehicles';
    return await this.request(endpoint);
  }

  async getVehicleById(id) {
    return await this.request(`/vehicles/${id}`);
  }

  async createVehicle(vehicleData) {
    return await this.request('/vehicles', {
      method: 'POST',
      body: vehicleData,
    });
  }

  async updateVehicle(id, vehicleData) {
    return await this.request(`/vehicles/${id}`, {
      method: 'PUT',
      body: vehicleData,
    });
  }

  async deleteVehicle(id) {
    return await this.request(`/vehicles/${id}`, {
      method: 'DELETE',
    });
  }

  async searchVehicles(searchQuery, params = {}) {
    const allParams = { q: searchQuery, ...params };
    const queryString = new URLSearchParams(allParams).toString();
    return await this.request(`/vehicles/search?${queryString}`);
  }

  async filterVehicles(filters = {}) {
    const queryString = new URLSearchParams(filters).toString();
    const endpoint = queryString ? `/vehicles/filter?${queryString}` : '/vehicles/filter';
    return await this.request(endpoint);
  }

  async getVehiclesByType(type, params = {}) {
    const queryString = new URLSearchParams(params).toString();
    const endpoint = queryString 
      ? `/vehicles/type/${encodeURIComponent(type)}?${queryString}`
      : `/vehicles/type/${encodeURIComponent(type)}`;
    return await this.request(endpoint);
  }

  async getVehiclesByTag(tag, params = {}) {
    const queryString = new URLSearchParams(params).toString();
    const endpoint = queryString 
      ? `/vehicles/tag/${encodeURIComponent(tag)}?${queryString}`
      : `/vehicles/tag/${encodeURIComponent(tag)}`;
    return await this.request(endpoint);
  }

  async getDashboardStats() {
    return await this.request('/vehicles/stats/dashboard');
  }

  async getVehicleStatsByType() {
    return await this.request('/vehicles/stats/by-type');
  }

  async getVehicleStatsByMake() {
    return await this.request('/vehicles/stats/by-make');
  }
}

// Create and export a singleton instance
const apiService = new ApiService();
export default apiService;

// Named exports for convenience
export const {
  getAllVehicles,
  getVehicleById,
  createVehicle,
  updateVehicle,
  deleteVehicle,
  searchVehicles,
  filterVehicles,
  getVehiclesByType,
  getVehiclesByTag,
  getDashboardStats,
  getVehicleStatsByType,
  getVehicleStatsByMake,
} = apiService;