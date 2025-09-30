# WARP 2.0 UI Services Killer

## Overview

The WARP 2.0 UI Services Killer is a comprehensive bash workflow script designed to forcefully terminate all UI runtime processes and development servers across different frameworks and build tools.

**Location**: `/scripts/kill-ui-services.sh`

## Features

- **🔍 Process Detection**: Automatically identifies Node.js, npm, yarn, pnpm, Vite, Webpack, Parcel, Next.js, and other UI-related processes
- **🌐 Port Management**: Scans and reports on common development ports (3000, 4000, 5173, etc.)
- **⚡ Force Termination**: Uses `kill -9` to ensure processes are terminated
- **✅ Verification**: Confirms cleanup success and port availability
- **🎨 Visual Feedback**: Color-coded output with clear status messages
- **🔒 Safety Prompts**: Interactive confirmation before executing kills (unless force mode)

## Usage

### Interactive Mode (Recommended)
```bash
./scripts/kill-ui-services.sh
```
Prompts for confirmation before terminating processes.

### Force Mode (Advanced)
```bash
./scripts/kill-ui-services.sh -f
# or
./scripts/kill-ui-services.sh --force
```
Skips confirmation and immediately terminates all detected UI processes.

### Help
```bash
./scripts/kill-ui-services.sh --help
```

## Supported Technologies

The script detects and terminates processes for:

- **Node.js Runtimes**: `node`, `npm`, `yarn`, `pnpm`
- **Build Tools**: `vite`, `webpack`, `parcel`, `rollup`, `esbuild`, `snowpack`
- **Framework CLI**: `next`, `gatsby`, `nuxt`, `react-scripts`, `angular`, `vue-cli-service`
- **Modern Tools**: `turbo`, and other development servers

## Common Development Ports

The script monitors these commonly used ports:
- `3000` - Create React App, Next.js
- `3001`, `3002` - Alternative React ports  
- `4000`, `4001` - Node.js applications
- `4200` - Angular CLI
- `5000` - Flask, some Node apps
- `5173` - Vite dev server
- `8000`, `8001`, `8080` - Various development servers
- `9000` - Gatsby, other tools

## When to Use

### ✅ Recommended Use Cases:
- **Project Switching**: Before working on different projects to prevent port conflicts
- **Stuck Processes**: When UI servers become unresponsive or won't shut down gracefully
- **System Cleanup**: Before restarting your development environment
- **Configuration Changes**: When switching between different frontend setups

### ⚠️ Use with Caution:
- **Force Mode**: Only use `-f` when you're certain no important work will be lost
- **Production Systems**: Never run this on production servers
- **Shared Development**: Be mindful of other developers' processes on shared systems

## Safety Features

1. **Process Validation**: Verifies processes exist before attempting termination
2. **Confirmation Prompts**: Interactive mode requires explicit user confirmation
3. **Error Handling**: Graceful handling of permission errors and missing processes
4. **Status Verification**: Post-execution verification of cleanup success

## Integration with Virtual Garage

This script is specifically designed for the Virtual Garage WARP 2.0 project workflow:

- Compatible with the React 19 + TypeScript + Vite frontend
- Safely terminates development servers before backend startup
- Ensures clean port availability for Docker services
- Integrates with existing WARP.md development guidelines

## Technical Details

### Process Detection Algorithm
```bash
# UI process patterns
patterns=("node" "npm" "yarn" "pnpm" "vite" "webpack" "parcel" "rollup" "next" "gatsby" "nuxt" "react-scripts" "angular" "vue-cli-service" "esbuild" "snowpack" "turbo")

# Process discovery
ps aux | grep -E "(pattern1|pattern2|...)" | grep -v grep
```

### Termination Strategy
1. **Discovery**: Scan for all UI-related processes
2. **Validation**: Verify processes are still running
3. **Termination**: Execute `kill -9 <PID>` for each process
4. **Verification**: Confirm successful termination

### Port Monitoring
```bash
# Common development ports
common_ports=(3000 3001 3002 4000 4001 4200 5000 5173 8080 8000 8001 9000)

# Port status check
lsof -i ":$port" | grep LISTEN
```

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | 2024-09-30 | Initial WARP 2.0 workflow implementation |

## Contributing

This script follows WARP 2.0 development standards. When contributing:

1. Maintain color-coded output consistency
2. Add new UI framework patterns to the detection arrays
3. Update port monitoring for new development servers
4. Test thoroughly with various UI development scenarios
5. Follow the existing error handling patterns

## License

Part of the Virtual Garage WARP 2.0 project ecosystem.