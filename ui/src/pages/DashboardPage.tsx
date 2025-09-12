import { useState, useEffect } from 'react'
import { motion } from 'framer-motion'
import { Plus, Car, Wrench, DollarSign, TrendingUp, Warehouse } from 'lucide-react'
import { Card, CardHeader, CardContent, CardDescription, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { useAuth } from '@/contexts/AuthContext'
import type { Garage } from '@/types'
import { generateMockData } from '@/services/mockData'

export function DashboardPage() {
  const { user } = useAuth()
  const [garages, setGarages] = useState<Garage[]>([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    // Simulate loading data
    const loadData = async () => {
      if (!user) return
      
      await new Promise(resolve => setTimeout(resolve, 500)) // Simulate API call
      const mockGarages = generateMockData(user.id)
      setGarages(mockGarages)
      setIsLoading(false)
    }

    loadData()
  }, [user])

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
          className="h-8 w-8 border-2 border-primary border-t-transparent rounded-full"
        />
      </div>
    )
  }

  const totalVehicles = garages.reduce((sum, garage) => sum + garage.vehicles.length, 0)
  const totalBuilds = garages.reduce((sum, garage) => 
    sum + garage.vehicles.reduce((vehicleSum, vehicle) => vehicleSum + vehicle.builds.length, 0), 0
  )
  const totalInvestment = garages.reduce((sum, garage) => 
    sum + garage.vehicles.reduce((vehicleSum, vehicle) => 
      vehicleSum + vehicle.builds.reduce((buildSum, build) => buildSum + build.totalCost, 0), 0
    ), 0
  )
  const activeProjects = garages.reduce((sum, garage) => 
    sum + garage.vehicles.reduce((vehicleSum, vehicle) => 
      vehicleSum + vehicle.builds.filter(build => build.status === 'in-progress').length, 0
    ), 0
  )

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1
      }
    }
  }

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: {
      opacity: 1,
      y: 0,
      transition: {
        duration: 0.5
      }
    }
  }

  return (
    <div className="container mx-auto p-6 space-y-6">
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="flex justify-between items-center"
      >
        <div>
          <h1 className="text-3xl font-bold">Welcome back, {user?.name}!</h1>
          <p className="text-muted-foreground">Manage your virtual garage and track your builds</p>
        </div>
        <Button className="flex items-center gap-2">
          <Plus className="h-4 w-4" />
          New Garage
        </Button>
      </motion.div>

      {/* Stats Cards */}
      <motion.div
        variants={containerVariants}
        initial="hidden"
        animate="visible"
        className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6"
      >
        <motion.div variants={itemVariants}>
          <Card className="hover:shadow-lg transition-all duration-300 hover:-translate-y-1">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Total Vehicles</CardTitle>
              <Car className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{totalVehicles}</div>
              <p className="text-xs text-muted-foreground">
                Across {garages.length} garage{garages.length !== 1 ? 's' : ''}
              </p>
            </CardContent>
          </Card>
        </motion.div>

        <motion.div variants={itemVariants}>
          <Card className="hover:shadow-lg transition-all duration-300 hover:-translate-y-1">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Active Builds</CardTitle>
              <Wrench className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{totalBuilds}</div>
              <p className="text-xs text-muted-foreground">
                {activeProjects} in progress
              </p>
            </CardContent>
          </Card>
        </motion.div>

        <motion.div variants={itemVariants}>
          <Card className="hover:shadow-lg transition-all duration-300 hover:-translate-y-1">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Total Investment</CardTitle>
              <DollarSign className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">${totalInvestment.toLocaleString()}</div>
              <p className="text-xs text-muted-foreground">
                Planned modifications
              </p>
            </CardContent>
          </Card>
        </motion.div>

        <motion.div variants={itemVariants}>
          <Card className="hover:shadow-lg transition-all duration-300 hover:-translate-y-1">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Completion Rate</CardTitle>
              <TrendingUp className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">67%</div>
              <p className="text-xs text-muted-foreground">
                Project completion average
              </p>
            </CardContent>
          </Card>
        </motion.div>
      </motion.div>

      {/* Garages */}
      <motion.div
        variants={containerVariants}
        initial="hidden"
        animate="visible"
        className="space-y-6"
      >
        <motion.h2 variants={itemVariants} className="text-2xl font-semibold">
          Your Garages
        </motion.h2>
        
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {garages.map((garage) => (
            <motion.div
              key={garage.id}
              variants={itemVariants}
              whileHover={{ scale: 1.02 }}
              transition={{ duration: 0.2 }}
            >
              <Card className="hover:shadow-xl transition-all duration-300 cursor-pointer border-l-4 border-l-primary">
                <CardHeader>
                  <div className="flex items-center gap-3">
                    <div className="p-2 rounded-full bg-primary/10">
                      <Warehouse className="h-6 w-6 text-primary" />
                    </div>
                    <div className="flex-1">
                      <CardTitle className="text-xl">{garage.name}</CardTitle>
                      <CardDescription>{garage.description}</CardDescription>
                    </div>
                    <Button variant="ghost" size="sm">
                      View
                    </Button>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    <div className="flex justify-between text-sm">
                      <span className="text-muted-foreground">Vehicles:</span>
                      <span className="font-medium">{garage.vehicles.length}</span>
                    </div>
                    
                    {garage.vehicles.length > 0 && (
                      <div className="space-y-2">
                        <p className="text-sm font-medium text-muted-foreground">Recent Vehicles:</p>
                        <div className="flex gap-2 overflow-x-auto">
                          {garage.vehicles.slice(0, 3).map((vehicle) => (
                            <div
                              key={vehicle.id}
                              className="flex-shrink-0 bg-muted rounded-lg p-3 min-w-[200px]"
                            >
                              <div className="aspect-[16/9] bg-background rounded mb-2 overflow-hidden">
                                <img
                                  src={vehicle.photo || vehicle.stockPhotoUrl}
                                  alt={`${vehicle.year} ${vehicle.make} ${vehicle.model}`}
                                  className="w-full h-full object-cover"
                                />
                              </div>
                              <h4 className="font-medium text-sm truncate">
                                {vehicle.year} {vehicle.make} {vehicle.model}
                              </h4>
                              <p className="text-xs text-muted-foreground">
                                {vehicle.builds.length} build{vehicle.builds.length !== 1 ? 's' : ''}
                              </p>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
                    
                    {garage.vehicles.length === 0 && (
                      <div className="text-center py-8">
                        <Car className="h-12 w-12 text-muted-foreground/50 mx-auto mb-2" />
                        <p className="text-muted-foreground">No vehicles yet</p>
                        <Button variant="link" size="sm" className="mt-2">
                          Add your first vehicle
                        </Button>
                      </div>
                    )}
                  </div>
                </CardContent>
              </Card>
            </motion.div>
          ))}
        </div>
      </motion.div>
    </div>
  )
}