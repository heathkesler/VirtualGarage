# Virtual Garage - Troubleshooting Guide

If you're having trouble accessing the application at `localhost:5173`, here are several solutions to try:

## Quick Fix - Start the Server Correctly

The development server needs to stay running. Try one of these methods:

### Method 1: Start in Background
```bash
# Start the server in background
nohup npm run dev > server.log 2>&1 &

# Check if it's running
ps aux | grep vite

# View logs
tail -f server.log
```

### Method 2: Use a New Terminal Window
```bash
# In Terminal Window 1
npm run dev

# Leave this terminal open and use a new terminal for other commands
```

### Method 3: Use Screen or Tmux
```bash
# Using screen
screen -S vite-server
npm run dev
# Press Ctrl+A then D to detach

# Using tmux (if installed)
tmux new-session -d -s vite-server 'npm run dev'
```

## Alternative Ports

If port 5173 is blocked, try different ports:

```bash
# Try port 3000
npm run dev -- --port 3000

# Try port 8080
npm run dev -- --port 8080

# Try with host flag for network access
npm run dev -- --host 0.0.0.0
```

## Complete Reset

If you're still having issues, try a complete reset:

```bash
# 1. Kill any existing processes
pkill -f vite
pkill -f node

# 2. Clean install
rm -rf node_modules package-lock.json
npm install

# 3. Start fresh
npm run dev
```

## Check System Requirements

Ensure you have:
- Node.js 18+ (`node --version`)
- npm 9+ (`npm --version`)
- No firewall blocking localhost ports

## Verify Installation

Test the basic setup:

```bash
# Check if build works
npm run build

# Check if preview works
npm run preview
```

## Access the Application

Once the server is running, you should see:
```
VITE v7.1.5  ready in 86 ms

➜  Local:   http://localhost:5173/
➜  Network: use --host to expose
```

Open your browser and navigate to: **http://localhost:5173**

## Application Features

When running successfully, you'll see:

1. **Login Screen**: Use any email/password to login
2. **Dashboard**: Overview of garages and vehicles
3. **Modern UI**: Smooth animations and responsive design
4. **Interactive Elements**: Hover effects and micro-interactions

## Common Issues

### Port Already in Use
```bash
# Find what's using the port
lsof -i :5173

# Kill the process if needed
kill -9 <PID>
```

### Permission Issues
```bash
# Fix npm permissions
sudo chown -R $(whoami) ~/.npm
```

### Tailwind CSS Issues
If styling looks broken, try:
```bash
# Rebuild Tailwind
npx tailwindcss build -o dist/output.css
```

## Browser Testing

Test in multiple browsers:
- Chrome/Chromium
- Firefox
- Safari

## Network Issues

If localhost doesn't work:
```bash
# Try 127.0.0.1 instead
http://127.0.0.1:5173

# Or use the network option
npm run dev -- --host
# Then use your IP address shown in terminal
```

## Development Mode Features

The application includes:
- Hot module replacement (HMR)
- TypeScript compilation
- Tailwind CSS processing
- Modern ES modules

## Production Build

To test the production version:
```bash
npm run build
npm run preview
```

This will start a production server typically on port 4173.

---

**If you continue to have issues, please provide:**
1. Your operating system version
2. Node.js version (`node --version`)
3. The exact error message you're seeing
4. Browser developer console errors (F12)