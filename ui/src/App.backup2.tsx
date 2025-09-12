import { AuthProvider } from '@/contexts/AuthContext'
import { ProtectedRoute } from '@/components/ProtectedRoute'
import { Header } from '@/components/Header'
import { DashboardPage } from '@/pages/DashboardPage'

function App() {
  return (
    <AuthProvider>
      <div className="min-h-screen bg-background">
        <ProtectedRoute>
          <Header />
          <main>
            <DashboardPage />
          </main>
        </ProtectedRoute>
      </div>
    </AuthProvider>
  )
}

export default App
