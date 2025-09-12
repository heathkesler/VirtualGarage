export interface User {
  id: string
  email: string
  name: string
  avatar?: string
  createdAt: string
  updatedAt: string
}

export interface Garage {
  id: string
  name: string
  description?: string
  userId: string
  vehicles: Vehicle[]
  createdAt: string
  updatedAt: string
}

export interface Vehicle {
  id: string
  garageId: string
  make: string
  model: string
  year: number
  color?: string
  vin?: string
  mileage?: number
  photo?: string
  stockPhotoUrl?: string
  builds: Build[]
  createdAt: string
  updatedAt: string
}

export interface Build {
  id: string
  vehicleId: string
  name: string
  description?: string
  components: Component[]
  totalCost: number
  status: 'planning' | 'in-progress' | 'completed'
  createdAt: string
  updatedAt: string
}

export interface Component {
  id: string
  buildId: string
  name: string
  brand: string
  partNumber?: string
  description?: string
  category: ComponentCategory
  price: number
  quantity: number
  status: 'pending' | 'ordered' | 'received' | 'installed'
  url?: string
  imageUrl?: string
  notes?: string
  createdAt: string
  updatedAt: string
}

export type ComponentCategory = 
  | 'engine'
  | 'transmission'
  | 'suspension'
  | 'brakes'
  | 'wheels-tires'
  | 'exhaust'
  | 'intake'
  | 'body'
  | 'interior'
  | 'electrical'
  | 'cooling'
  | 'fuel-system'
  | 'drivetrain'
  | 'other'

export interface AuthContextType {
  user: User | null
  login: (email: string, password: string) => Promise<void>
  logout: () => Promise<void>
  register: (email: string, password: string, name: string) => Promise<void>
  isLoading: boolean
}

export interface ApiResponse<T> {
  data?: T
  error?: string
  message?: string
}

// Stock vehicle data for initial selection
export interface StockVehicle {
  id: string
  make: string
  model: string
  year: number
  imageUrl: string
  category: string
}