import { motion } from 'framer-motion'
import { Car, LogOut, User, Settings } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useAuth } from '@/contexts/AuthContext'
import * as DropdownMenu from '@radix-ui/react-dropdown-menu'

export function Header() {
  const { user, logout } = useAuth()

  return (
    <motion.header
      initial={{ opacity: 0, y: -10 }}
      animate={{ opacity: 1, y: 0 }}
      className="border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 sticky top-0 z-50"
    >
      <div className="container mx-auto px-6 py-4 flex items-center justify-between">
        <motion.div 
          className="flex items-center gap-2"
          whileHover={{ scale: 1.05 }}
        >
          <div className="p-2 rounded-full bg-primary">
            <Car className="h-6 w-6 text-primary-foreground" />
          </div>
          <span className="text-xl font-bold">Virtual Garage</span>
        </motion.div>

        <nav className="hidden md:flex items-center gap-6">
          <Button variant="ghost" className="text-muted-foreground hover:text-foreground">
            Dashboard
          </Button>
          <Button variant="ghost" className="text-muted-foreground hover:text-foreground">
            Garages
          </Button>
          <Button variant="ghost" className="text-muted-foreground hover:text-foreground">
            Vehicles
          </Button>
          <Button variant="ghost" className="text-muted-foreground hover:text-foreground">
            Builds
          </Button>
        </nav>

        <DropdownMenu.Root>
          <DropdownMenu.Trigger asChild>
            <Button variant="ghost" className="flex items-center gap-2 px-2">
              <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center">
                {user?.avatar ? (
                  <img
                    src={user.avatar}
                    alt={user.name}
                    className="w-full h-full rounded-full object-cover"
                  />
                ) : (
                  <User className="h-4 w-4" />
                )}
              </div>
              <span className="hidden sm:block">{user?.name}</span>
            </Button>
          </DropdownMenu.Trigger>

          <DropdownMenu.Portal>
            <DropdownMenu.Content
              className="min-w-[200px] bg-popover border rounded-md p-1 shadow-lg animate-in fade-in-0 zoom-in-95"
              sideOffset={5}
            >
              <DropdownMenu.Item className="flex items-center gap-2 px-3 py-2 text-sm cursor-pointer hover:bg-accent rounded-sm">
                <User className="h-4 w-4" />
                Profile
              </DropdownMenu.Item>
              <DropdownMenu.Item className="flex items-center gap-2 px-3 py-2 text-sm cursor-pointer hover:bg-accent rounded-sm">
                <Settings className="h-4 w-4" />
                Settings
              </DropdownMenu.Item>
              <DropdownMenu.Separator className="h-px bg-border my-1" />
              <DropdownMenu.Item
                className="flex items-center gap-2 px-3 py-2 text-sm cursor-pointer hover:bg-destructive hover:text-destructive-foreground rounded-sm"
                onClick={logout}
              >
                <LogOut className="h-4 w-4" />
                Logout
              </DropdownMenu.Item>
            </DropdownMenu.Content>
          </DropdownMenu.Portal>
        </DropdownMenu.Root>
      </div>
    </motion.header>
  )
}