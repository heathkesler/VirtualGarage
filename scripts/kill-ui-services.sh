#!/bin/bash

# =============================================================================
# WARP 2.0 UI Services Killer
# =============================================================================
# 
# Purpose: Forcefully terminates all UI runtime processes and development servers
# Compatible with: Node.js, npm, yarn, pnpm, Vite, Webpack, Parcel, Next.js, etc.
# 
# Usage: ./scripts/kill-ui-services.sh [-f|--force]
# 
# Author: Virtual Garage WARP 2.0 Team
# Version: 1.0.0
# Date: $(date +%Y-%m-%d)
# =============================================================================

set -euo pipefail

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Script configuration
SCRIPT_NAME="WARP 2.0 UI Services Killer"
VERSION="1.0.0"

# =============================================================================
# Functions
# =============================================================================

print_header() {
    echo -e "${PURPLE}================================================================================================${NC}"
    echo -e "${PURPLE}${SCRIPT_NAME} v${VERSION}${NC}"
    echo -e "${PURPLE}================================================================================================${NC}"
    echo -e "${CYAN}Timestamp: $(date)${NC}"
    echo -e "${CYAN}Directory: $(pwd)${NC}"
    echo ""
}

print_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_info() {
    echo -e "${CYAN}[INFO]${NC} $1"
}

print_help() {
    echo -e "${PURPLE}${SCRIPT_NAME} v${VERSION}${NC}"
    echo ""
    echo -e "${CYAN}USAGE:${NC}"
    echo -e "  ./scripts/kill-ui-services.sh [OPTIONS]"
    echo ""
    echo -e "${CYAN}OPTIONS:${NC}"
    echo -e "  -f, --force    Skip confirmation prompt and terminate processes immediately"
    echo -e "  -h, --help     Show this help message"
    echo ""
    echo -e "${CYAN}DESCRIPTION:${NC}"
    echo -e "  Forcefully terminates all UI runtime processes and development servers."
    echo -e "  Compatible with Node.js, npm, yarn, pnpm, Vite, Webpack, Parcel, Next.js, etc."
    echo ""
    echo -e "${CYAN}EXAMPLES:${NC}"
    echo -e "  ./scripts/kill-ui-services.sh              # Interactive mode with confirmation"
    echo -e "  ./scripts/kill-ui-services.sh -f           # Force mode, no confirmation"
    echo -e "  ./scripts/kill-ui-services.sh --force      # Force mode, no confirmation"
    echo ""
}

# =============================================================================
# Main Functions
# =============================================================================

find_ui_processes() {
    print_step "Scanning for UI runtime processes..."
    
    # Define patterns for UI-related processes
    local patterns=(
        "node"
        "npm"
        "yarn" 
        "pnpm"
        "vite"
        "webpack"
        "parcel"
        "rollup"
        "next"
        "gatsby"
        "nuxt"
        "react-scripts"
        "angular"
        "vue-cli-service"
        "esbuild"
        "snowpack"
        "turbo"
    )
    
    local pattern_regex=$(printf "|%s" "${patterns[@]}")
    pattern_regex=${pattern_regex:1} # Remove leading |
    
    # Find processes
    local processes=$(ps aux | grep -E "(${pattern_regex})" | grep -v grep | grep -v "$0" || true)
    
    if [[ -z "$processes" ]]; then
        print_info "No UI runtime processes found."
        return 1
    else
        echo -e "${YELLOW}Found UI processes:${NC}"
        echo "$processes" | while read -r line; do
            local pid=$(echo "$line" | awk '{print $2}')
            local command=$(echo "$line" | awk '{for(i=11;i<=NF;i++) printf "%s ", $i; print ""}')
            echo -e "  ${RED}PID $pid${NC}: $command"
        done
        echo ""
        return 0
    fi
}

find_listening_ports() {
    print_step "Scanning for UI services on listening ports..."
    
    # Common UI development ports
    local common_ports=(3000 3001 3002 4000 4001 4200 5000 5173 8080 8000 8001 8080 9000)
    
    local found_ports=""
    for port in "${common_ports[@]}"; do
        local process=$(lsof -i ":$port" 2>/dev/null | grep LISTEN | head -1 || true)
        if [[ -n "$process" ]]; then
            found_ports="$found_ports $port"
            local pid=$(echo "$process" | awk '{print $2}')
            local name=$(echo "$process" | awk '{print $1}')
            echo -e "  ${YELLOW}Port $port${NC}: $name (PID $pid)"
        fi
    done
    
    if [[ -z "$found_ports" ]]; then
        print_info "No UI services found on common development ports."
    else
        echo ""
    fi
}

kill_ui_processes() {
    print_step "Terminating UI runtime processes..."
    
    # Define patterns for UI-related processes
    local patterns=(
        "node"
        "npm"
        "yarn" 
        "pnpm"
        "vite"
        "webpack"
        "parcel"
        "rollup"
        "next"
        "gatsby"
        "nuxt"
        "react-scripts"
        "angular"
        "vue-cli-service"
        "esbuild"
        "snowpack"
        "turbo"
    )
    
    local pattern_regex=$(printf "|%s" "${patterns[@]}")
    pattern_regex=${pattern_regex:1} # Remove leading |
    
    # Get PIDs of UI processes
    local pids=$(ps aux | grep -E "(${pattern_regex})" | grep -v grep | grep -v "$0" | awk '{print $2}' || true)
    
    if [[ -z "$pids" ]]; then
        print_info "No UI processes to terminate."
        return 0
    fi
    
    local killed_count=0
    echo "$pids" | while read -r pid; do
        if [[ -n "$pid" ]] && kill -0 "$pid" 2>/dev/null; then
            local process_info=$(ps -p "$pid" -o command= 2>/dev/null || echo "Unknown process")
            echo -e "  ${RED}Killing PID $pid${NC}: $process_info"
            
            if kill -9 "$pid" 2>/dev/null; then
                ((killed_count++))
                print_success "Terminated PID $pid"
            else
                print_warning "Failed to terminate PID $pid (may already be dead)"
            fi
        fi
    done
    
    # Count actual killed processes
    local final_pids=$(ps aux | grep -E "(${pattern_regex})" | grep -v grep | grep -v "$0" | awk '{print $2}' || true)
    if [[ -z "$final_pids" ]]; then
        print_success "All UI processes have been terminated."
    else
        print_warning "Some processes may still be running."
    fi
}

verify_cleanup() {
    print_step "Verifying cleanup..."
    
    # Check for remaining processes
    local remaining=$(ps aux | grep -E "(node|npm|yarn|pnpm|vite|webpack|parcel|rollup|next)" | grep -v grep | grep -v "$0" || true)
    
    if [[ -z "$remaining" ]]; then
        print_success "✅ No UI runtime processes detected"
    else
        print_warning "⚠️  Some processes may still be running:"
        echo "$remaining"
    fi
    
    # Check common development ports
    local ports_in_use=""
    local common_ports=(3000 3001 3002 4000 4001 4200 5000 5173 8080 8000 8001 9000)
    
    for port in "${common_ports[@]}"; do
        if lsof -i ":$port" >/dev/null 2>&1; then
            ports_in_use="$ports_in_use $port"
        fi
    done
    
    if [[ -z "$ports_in_use" ]]; then
        print_success "✅ All common development ports are available"
    else
        print_info "ℹ️  Ports still in use:$ports_in_use (may be non-UI services)"
    fi
}

print_footer() {
    echo ""
    echo -e "${PURPLE}================================================================================================${NC}"
    echo -e "${GREEN}WARP 2.0 UI Services Cleanup Complete${NC}"
    echo -e "${PURPLE}================================================================================================${NC}"
    echo -e "${CYAN}Next steps:${NC}"
    echo -e "  • Run ${YELLOW}cd ui && npm run dev${NC} to start the React development server"
    echo -e "  • Run ${YELLOW}docker compose up -d${NC} to start backend services"
    echo -e "  • Run ${YELLOW}lsof -i :3000${NC} to verify port 3000 is available"
    echo ""
}

# =============================================================================
# Main Execution
# =============================================================================

main() {
    # Check for help flag first
    if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
        print_help
        exit 0
    fi
    
    print_header
    
    # Check for force flag
    local force_mode=false
    if [[ "${1:-}" == "-f" || "${1:-}" == "--force" ]]; then
        force_mode=true
        print_warning "Force mode enabled - skipping confirmation"
        echo ""
    fi
    
    # Step 1: Find running UI processes
    if find_ui_processes; then
        echo ""
        find_listening_ports
        echo ""
        
        # Step 2: Confirm termination (unless force mode)
        if [[ "$force_mode" == "false" ]]; then
            echo -e "${YELLOW}⚠️  WARNING: This will forcefully terminate all UI runtime processes using kill -9${NC}"
            read -p "Continue? [y/N]: " -r confirm
            echo ""
            
            if [[ ! $confirm =~ ^[Yy]$ ]]; then
                print_info "Operation cancelled by user."
                exit 0
            fi
        fi
        
        # Step 3: Kill processes
        kill_ui_processes
        echo ""
        
        # Step 4: Verify cleanup
        sleep 1  # Give processes time to terminate
        verify_cleanup
    fi
    
    print_footer
}

# =============================================================================
# Error Handling
# =============================================================================

trap 'print_error "Script interrupted"; exit 1' INT TERM

# Check if running on macOS
if [[ "$(uname)" != "Darwin" ]]; then
    print_warning "This script is optimized for macOS. Some commands may need adjustment for other platforms."
fi

# =============================================================================
# Execute Main Function
# =============================================================================

main "$@"