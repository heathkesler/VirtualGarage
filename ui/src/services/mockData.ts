import type { Garage, Vehicle, Build, Component, StockVehicle, ComponentCategory } from '@/types'

export const mockStockVehicles: StockVehicle[] = [
  {
    id: 'honda-civic-2023',
    make: 'Honda',
    model: 'Civic',
    year: 2023,
    imageUrl: 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800',
    category: 'Compact'
  },
  {
    id: 'toyota-supra-2023',
    make: 'Toyota',
    model: 'Supra',
    year: 2023,
    imageUrl: 'https://images.unsplash.com/photo-1544829099-b9a0c5303bea?w=800',
    category: 'Sports'
  },
  {
    id: 'ford-mustang-2023',
    make: 'Ford',
    model: 'Mustang GT',
    year: 2023,
    imageUrl: 'https://images.unsplash.com/photo-1584345604476-8ec5e12e42dd?w=800',
    category: 'Muscle'
  },
  {
    id: 'bmw-m3-2023',
    make: 'BMW',
    model: 'M3',
    year: 2023,
    imageUrl: 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800',
    category: 'Luxury Sports'
  },
  {
    id: 'subaru-wrx-2023',
    make: 'Subaru',
    model: 'WRX STI',
    year: 2023,
    imageUrl: 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800',
    category: 'Sport Compact'
  },
  {
    id: 'nissan-370z-2023',
    make: 'Nissan',
    model: '370Z',
    year: 2023,
    imageUrl: 'https://images.unsplash.com/photo-1503736334956-4c8f8e92946d?w=800',
    category: 'Sports'
  }
]

export const mockComponents: Omit<Component, 'id' | 'buildId' | 'createdAt' | 'updatedAt'>[] = [
  {
    name: 'K&N Cold Air Intake',
    brand: 'K&N',
    partNumber: '57-3510',
    description: 'High-flow cold air intake system',
    category: 'intake' as ComponentCategory,
    price: 299.99,
    quantity: 1,
    status: 'pending',
    imageUrl: 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400',
    url: 'https://example.com/kn-intake'
  },
  {
    name: 'Bilstein B12 Pro-Kit',
    brand: 'Bilstein',
    partNumber: '46-000753',
    description: 'Complete suspension lowering kit',
    category: 'suspension' as ComponentCategory,
    price: 849.99,
    quantity: 1,
    status: 'pending',
    imageUrl: 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400'
  },
  {
    name: 'Brembo GT Big Brake Kit',
    brand: 'Brembo',
    partNumber: '1N1.9001A',
    description: '4-piston front brake upgrade',
    category: 'brakes' as ComponentCategory,
    price: 2399.99,
    quantity: 1,
    status: 'pending',
    imageUrl: 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400'
  },
  {
    name: 'Borla ATAK Exhaust System',
    brand: 'Borla',
    partNumber: '140679',
    description: 'Cat-back exhaust system',
    category: 'exhaust' as ComponentCategory,
    price: 1299.99,
    quantity: 1,
    status: 'ordered',
    imageUrl: 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=400'
  }
]

export const generateMockData = (userId: string): Garage[] => {
  const now = new Date().toISOString()
  
  const builds: Build[] = [
    {
      id: 'build-1',
      vehicleId: 'vehicle-1',
      name: 'Stage 2 Performance Build',
      description: 'Bolt-on modifications for increased power and handling',
      components: mockComponents.map((comp, index) => ({
        ...comp,
        id: `comp-${index + 1}`,
        buildId: 'build-1',
        createdAt: now,
        updatedAt: now
      })),
      totalCost: mockComponents.reduce((sum, comp) => sum + (comp.price * comp.quantity), 0),
      status: 'in-progress',
      createdAt: now,
      updatedAt: now
    },
    {
      id: 'build-2',
      vehicleId: 'vehicle-2',
      name: 'Daily Driver Plus',
      description: 'Practical upgrades for daily driving comfort',
      components: mockComponents.slice(0, 2).map((comp, index) => ({
        ...comp,
        id: `comp-build2-${index + 1}`,
        buildId: 'build-2',
        createdAt: now,
        updatedAt: now
      })),
      totalCost: mockComponents.slice(0, 2).reduce((sum, comp) => sum + (comp.price * comp.quantity), 0),
      status: 'planning',
      createdAt: now,
      updatedAt: now
    }
  ]

  const vehicles: Vehicle[] = [
    {
      id: 'vehicle-1',
      garageId: 'garage-1',
      make: 'Honda',
      model: 'Civic Type R',
      year: 2023,
      color: 'Championship White',
      vin: '2HGFK5C60NH123456',
      mileage: 8500,
      stockPhotoUrl: 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800',
      builds: [builds[0]],
      createdAt: now,
      updatedAt: now
    },
    {
      id: 'vehicle-2',
      garageId: 'garage-1',
      make: 'Toyota',
      model: 'Supra 3.0',
      year: 2023,
      color: 'Nitro Yellow',
      mileage: 12000,
      stockPhotoUrl: 'https://images.unsplash.com/photo-1544829099-b9a0c5303bea?w=800',
      builds: [builds[1]],
      createdAt: now,
      updatedAt: now
    },
    {
      id: 'vehicle-3',
      garageId: 'garage-2',
      make: 'Ford',
      model: 'Mustang GT',
      year: 2022,
      color: 'Grabber Blue',
      mileage: 15000,
      stockPhotoUrl: 'https://images.unsplash.com/photo-1584345604476-8ec5e12e42dd?w=800',
      builds: [],
      createdAt: now,
      updatedAt: now
    }
  ]

  return [
    {
      id: 'garage-1',
      name: 'My Daily Drivers',
      description: 'Cars I drive every day',
      userId,
      vehicles: vehicles.filter(v => v.garageId === 'garage-1'),
      createdAt: now,
      updatedAt: now
    },
    {
      id: 'garage-2',
      name: 'Weekend Warriors',
      description: 'Fun cars for the weekend',
      userId,
      vehicles: vehicles.filter(v => v.garageId === 'garage-2'),
      createdAt: now,
      updatedAt: now
    }
  ]
}