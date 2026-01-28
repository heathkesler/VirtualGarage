import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { 
  ArrowLeft, 
  Car, 
  Sparkles, 
  Send, 
  Loader2, 
  Wrench, 
  AlertTriangle, 
  DollarSign, 
  Gauge,
  Cog,
  Zap,
  Calendar,
  Search
} from 'lucide-react';

const QUICK_PROMPTS = [
  { icon: Wrench, label: 'Maintenance due?', prompt: 'maintenance' },
  { icon: AlertTriangle, label: 'Troubleshoot issue', prompt: 'troubleshoot' },
  { icon: Search, label: 'Find parts', prompt: 'parts' },
  { icon: Zap, label: 'Performance mods', prompt: 'mods' },
  { icon: DollarSign, label: 'Value estimate', prompt: 'value' },
];

// Maintenance schedules by mileage (miles)
const MAINTENANCE_SCHEDULE = {
  oil: { interval: 5000, name: 'Oil Change', urgency: 'high' },
  tires: { interval: 6000, name: 'Tire Rotation', urgency: 'medium' },
  airFilter: { interval: 15000, name: 'Air Filter', urgency: 'low' },
  brakeFluid: { interval: 30000, name: 'Brake Fluid Flush', urgency: 'high' },
  transmission: { interval: 60000, name: 'Transmission Service', urgency: 'high' },
  coolant: { interval: 30000, name: 'Coolant Flush', urgency: 'medium' },
  sparkPlugs: { interval: 30000, name: 'Spark Plugs', urgency: 'medium' },
  timingBelt: { interval: 60000, name: 'Timing Belt', urgency: 'critical' },
  brakes: { interval: 50000, name: 'Brake Pads/Rotors', urgency: 'high' },
};

// Common trouble codes and symptoms
const TROUBLE_SYMPTOMS = {
  'check engine': {
    common: ['Loose gas cap', 'O2 sensor', 'Catalytic converter', 'Mass airflow sensor', 'Spark plugs/ignition coils'],
    action: 'Get codes read at auto parts store (free) or with OBD2 scanner. Common codes: P0420 (cat), P0171/P0174 (lean), P0300 (misfire)',
  },
  'rough idle': {
    common: ['Dirty throttle body', 'Vacuum leak', 'Fouled spark plugs', 'Dirty fuel injectors', 'Failing idle air control valve'],
    action: 'Start with cleaning throttle body and checking for vacuum leaks. Listen for hissing sounds.',
  },
  'won\'t start': {
    common: ['Dead battery', 'Bad starter', 'Fuel pump failure', 'Ignition switch', 'Clogged fuel filter'],
    action: 'Check battery voltage first (should be 12.4V+). Listen for clicking (starter) or fuel pump priming sound.',
  },
  'overheating': {
    common: ['Low coolant', 'Thermostat stuck', 'Water pump failure', 'Radiator blockage', 'Head gasket'],
    action: 'STOP DRIVING immediately. Check coolant level when cool. Look for leaks. White smoke = head gasket.',
  },
  'brake': {
    common: ['Worn pads', 'Warped rotors', 'Stuck caliper', 'Air in brake lines', 'Low brake fluid'],
    action: 'Check pad thickness (min 3mm). Pulsating = warped rotors. Pulling = stuck caliper.',
  },
  'noise': {
    common: ['Worn brake pads (squealing)', 'Bad wheel bearing (humming)', 'CV joint (clicking when turning)', 'Exhaust leak (ticking)', 'Serpentine belt (squeaking)'],
    action: 'Note when noise occurs: accelerating, braking, turning, cold start? This helps pinpoint source.',
  },
  'vibration': {
    common: ['Unbalanced tires', 'Warped brake rotors', 'Worn CV axle', 'Engine mounts', 'Wheel bearing'],
    action: 'At highway speed = tire balance. When braking = rotors. At idle = engine mount.',
  },
  'transmission': {
    common: ['Low fluid', 'Worn clutch (manual)', 'Solenoid failure (auto)', 'Torque converter', 'Synchro wear'],
    action: 'Check fluid level and color. Dark/burnt smell = internal damage. Slipping = clutch/bands.',
  },
};

// Popular modifications by category
const POPULAR_MODS = {
  performance: [
    { name: 'Cold Air Intake', gain: '5-15hp', cost: '$150-400', difficulty: 'Easy' },
    { name: 'Cat-back Exhaust', gain: '10-25hp', cost: '$400-1500', difficulty: 'Medium' },
    { name: 'ECU Tune', gain: '15-50hp', cost: '$300-800', difficulty: 'Easy' },
    { name: 'Headers', gain: '15-30hp', cost: '$300-1200', difficulty: 'Hard' },
    { name: 'Forced Induction', gain: '50-200hp', cost: '$3000-8000', difficulty: 'Expert' },
  ],
  handling: [
    { name: 'Lowering Springs', gain: '0.5-1.5" drop', cost: '$200-500', difficulty: 'Medium' },
    { name: 'Coilovers', gain: 'Adjustable height/damping', cost: '$800-3000', difficulty: 'Medium' },
    { name: 'Sway Bars', gain: 'Reduced body roll', cost: '$200-600', difficulty: 'Medium' },
    { name: 'Strut Tower Brace', gain: 'Chassis rigidity', cost: '$100-300', difficulty: 'Easy' },
  ],
  appearance: [
    { name: 'Wheels', cost: '$800-4000', notes: 'Consider offset and fitment' },
    { name: 'Tint', cost: '$150-500', notes: 'Check local laws' },
    { name: 'Wrap/Paint', cost: '$2000-8000', notes: 'Wrap is reversible' },
    { name: 'LED Lighting', cost: '$50-300', notes: 'Check DOT compliance' },
  ],
};

const Assistant = () => {
  const [vehicles, setVehicles] = useState([]);
  const [selectedVehicle, setSelectedVehicle] = useState(null);
  const [messages, setMessages] = useState([
    { 
      role: 'assistant', 
      content: "Hey! I'm your Garage Assistant. I can help with:\n\n🔧 **Maintenance schedules** based on mileage\n⚠️ **Troubleshooting** symptoms and issues\n🔍 **Parts lookup** for your vehicles\n⚡ **Modification suggestions** for performance or looks\n💰 **Value estimates** and market insights\n\nSelect a vehicle above or just ask me anything!" 
    }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [activePrompt, setActivePrompt] = useState(null);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    // Load vehicles from API
    const loadVehicles = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/vehicles');
        if (response.ok) {
          const data = await response.json();
          setVehicles(data.content || data || []);
        }
      } catch {
        // API not available, use demo data
        setVehicles([
          { id: 1, name: '1969 Camaro SS', year: 1969, make: 'Chevrolet', model: 'Camaro SS', mileage: 45000, type: 'Classic' },
          { id: 2, name: '2022 Tesla Model 3', year: 2022, make: 'Tesla', model: 'Model 3', mileage: 12000, type: 'Electric' },
          { id: 3, name: '1987 Porsche 928', year: 1987, make: 'Porsche', model: '928', mileage: 78000, type: 'Exotic' },
        ]);
      }
    };
    loadVehicles();
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleQuickPrompt = (promptType) => {
    setActivePrompt(promptType);
    const vehicleContext = selectedVehicle ? ` for my ${selectedVehicle.name}` : '';
    
    switch (promptType) {
      case 'maintenance':
        setInput(`What maintenance is due${vehicleContext}?`);
        break;
      case 'troubleshoot':
        setInput(`I'm having an issue${vehicleContext}: `);
        break;
      case 'parts':
        setInput(`Help me find parts${vehicleContext} for `);
        break;
      case 'mods':
        setInput(`What performance mods do you recommend${vehicleContext}?`);
        break;
      case 'value':
        setInput(`What's my ${selectedVehicle?.name || 'vehicle'} worth?`);
        break;
    }
  };

  const getMaintenanceResponse = (vehicle) => {
    if (!vehicle) {
      return "Select a vehicle above to get personalized maintenance recommendations, or tell me the make, model, year, and mileage.";
    }
    
    const mileage = vehicle.mileage || 0;
    const upcoming = [];
    const overdue = [];
    
    Object.entries(MAINTENANCE_SCHEDULE).forEach(([key, item]) => {
      const lastDone = Math.floor(mileage / item.interval) * item.interval;
      const nextDue = lastDone + item.interval;
      const milesUntil = nextDue - mileage;
      
      if (milesUntil <= 0) {
        overdue.push({ ...item, milesOverdue: Math.abs(milesUntil) });
      } else if (milesUntil <= 2000) {
        upcoming.push({ ...item, milesUntil });
      }
    });
    
    let response = `**Maintenance Status for ${vehicle.name}** (${mileage.toLocaleString()} miles)\n\n`;
    
    if (overdue.length > 0) {
      response += "🔴 **OVERDUE:**\n";
      overdue.forEach(item => {
        response += `- ${item.name} (${item.milesOverdue.toLocaleString()} miles overdue)\n`;
      });
      response += "\n";
    }
    
    if (upcoming.length > 0) {
      response += "🟡 **COMING UP:**\n";
      upcoming.forEach(item => {
        response += `- ${item.name} in ${item.milesUntil.toLocaleString()} miles\n`;
      });
      response += "\n";
    }
    
    if (overdue.length === 0 && upcoming.length === 0) {
      response += "✅ **All caught up!** No immediate maintenance needed.\n\n";
      response += "Next service items will be due around:\n";
      response += `- Oil change at ${(Math.ceil(mileage / 5000) * 5000).toLocaleString()} miles\n`;
    }
    
    // Special notes for classics
    if (vehicle.type === 'Classic' || vehicle.year < 1990) {
      response += "\n📌 **Classic Car Note:** Consider more frequent fluid checks. Use zinc-additive oil for flat-tappet engines. Check rubber hoses and belts for dry rot.";
    }
    
    // Electric vehicle notes
    if (vehicle.type === 'Electric' || vehicle.make === 'Tesla') {
      response += "\n⚡ **EV Note:** No oil changes needed! Focus on tire rotation, brake fluid (every 2 years), and cabin air filter. Battery health check annually.";
    }
    
    return response;
  };

  const getTroubleshootResponse = (query) => {
    const q = query.toLowerCase();
    
    for (const [symptom, data] of Object.entries(TROUBLE_SYMPTOMS)) {
      if (q.includes(symptom)) {
        let response = `**Troubleshooting: ${symptom.charAt(0).toUpperCase() + symptom.slice(1)}**\n\n`;
        response += "🔍 **Common Causes:**\n";
        data.common.forEach((cause, i) => {
          response += `${i + 1}. ${cause}\n`;
        });
        response += `\n💡 **Recommended Action:**\n${data.action}`;
        
        if (selectedVehicle) {
          response += `\n\n🚗 *For your ${selectedVehicle.name}, I'd start with the most common causes first.*`;
        }
        
        return response;
      }
    }
    
    return "I can help troubleshoot! Describe the symptom:\n\n- Check engine light\n- Rough idle\n- Won't start\n- Overheating\n- Brake issues\n- Unusual noise\n- Vibration\n- Transmission problems\n\nBe specific about when it happens (cold start, highway, braking, etc.)";
  };

  const getModsResponse = (vehicle) => {
    let response = "**Popular Modifications**\n\n";
    
    response += "⚡ **Performance:**\n";
    POPULAR_MODS.performance.forEach(mod => {
      response += `• **${mod.name}** - ${mod.gain} | $${mod.cost} | ${mod.difficulty}\n`;
    });
    
    response += "\n🏎️ **Handling:**\n";
    POPULAR_MODS.handling.forEach(mod => {
      response += `• **${mod.name}** - ${mod.gain} | $${mod.cost} | ${mod.difficulty}\n`;
    });
    
    response += "\n✨ **Appearance:**\n";
    POPULAR_MODS.appearance.forEach(mod => {
      response += `• **${mod.name}** | $${mod.cost} | ${mod.notes}\n`;
    });
    
    if (vehicle) {
      response += `\n📌 **For your ${vehicle.name}:** `;
      if (vehicle.type === 'Classic') {
        response += "Focus on period-correct upgrades or restomod approach. Hidden EFI, disc brake conversions, and modern cooling are popular.";
      } else if (vehicle.type === 'Electric') {
        response += "Focus on handling (coilovers, wheels) and appearance. Software unlocks if available.";
      } else if (vehicle.type === 'Performance') {
        response += "Intake + exhaust + tune is the classic bolt-on combo. Check forums for your specific platform.";
      } else {
        response += "Start with intake/exhaust/tune for best value. Suspension makes the biggest daily driving difference.";
      }
    }
    
    return response;
  };

  const getValueResponse = (vehicle) => {
    if (!vehicle) {
      return "Select a vehicle above to get value insights, or tell me the year, make, model, mileage, and condition.";
    }
    
    let response = `**Value Insights: ${vehicle.name}**\n\n`;
    
    const baseValue = vehicle.value ? parseInt(vehicle.value.replace(/[^0-9]/g, '')) : 0;
    
    response += "📊 **Market Factors:**\n";
    
    if (vehicle.type === 'Classic') {
      response += "• Classic car market has appreciated 10-15% annually\n";
      response += "• Matching numbers and documentation add 20-40% premium\n";
      response += "• Originality vs. restomod - both have buyers\n";
      response += "• Bring a Trailer, Hagerty, and Hemmings are good comps\n";
    } else if (vehicle.type === 'Electric') {
      response += "• EV values tied to battery health and range\n";
      response += "• Technology changes quickly - newer features matter\n";
      response += "• Tax credits affect new vs. used pricing\n";
      response += "• Check Carmax, Carvana for quick comps\n";
    } else {
      response += "• Mileage is key - each 10K miles = 2-3% value change\n";
      response += "• Service records add 5-10% to private sale\n";
      response += "• Modifications can help or hurt - know your buyer\n";
      response += "• KBB, Edmunds, and private sale forums for pricing\n";
    }
    
    response += "\n💡 **Quick Tips:**\n";
    response += "• Private sale = 10-20% more than trade-in\n";
    response += "• Timing matters - convertibles worth more in spring\n";
    response += "• Detail and photograph well for listings\n";
    
    return response;
  };

  const getPartsResponse = (query, vehicle) => {
    const vehicleContext = vehicle ? `${vehicle.year} ${vehicle.make} ${vehicle.model}` : 'your vehicle';
    
    let response = `**Parts Resources for ${vehicleContext}**\n\n`;
    
    response += "🏪 **OEM Parts:**\n";
    response += "• Dealer parts department (expensive but guaranteed fit)\n";
    response += "• OEMPartsDirect, RockAuto (OEM at discount)\n\n";
    
    response += "🔧 **Aftermarket:**\n";
    response += "• RockAuto - huge selection, good prices\n";
    response += "• Amazon - quick shipping, easy returns\n";
    response += "• AutoZone/O'Reilly/Advance - local availability\n\n";
    
    response += "🏁 **Performance:**\n";
    response += "• Summit Racing, Jegs - wide performance selection\n";
    response += "• Platform-specific vendors (forums will recommend)\n\n";
    
    if (vehicle?.type === 'Classic') {
      response += "🎪 **Classic/Vintage:**\n";
      response += "• Year One, Classic Industries - reproduction parts\n";
      response += "• Hemmings classifieds - NOS and used\n";
      response += "• eBay - search for NOS (New Old Stock)\n";
      response += "• Car show swap meets\n\n";
    }
    
    response += "💡 **Pro Tip:** Search your car's forum for recommended vendors. Enthusiasts know the good and bad sources.";
    
    return response;
  };

  const getLocalResponse = (query) => {
    const q = query.toLowerCase();
    
    // Maintenance queries
    if (q.includes('maintenance') || q.includes('service') || q.includes('due') || q.includes('oil') || q.includes('schedule')) {
      return getMaintenanceResponse(selectedVehicle);
    }
    
    // Troubleshooting queries
    if (q.includes('problem') || q.includes('issue') || q.includes('noise') || q.includes('vibrat') || 
        q.includes('check engine') || q.includes('won\'t start') || q.includes('overheat') ||
        q.includes('brake') || q.includes('rough') || q.includes('trans')) {
      return getTroubleshootResponse(q);
    }
    
    // Parts queries
    if (q.includes('part') || q.includes('where to buy') || q.includes('find') || q.includes('source')) {
      return getPartsResponse(q, selectedVehicle);
    }
    
    // Modification queries
    if (q.includes('mod') || q.includes('upgrade') || q.includes('performance') || q.includes('faster') ||
        q.includes('hp') || q.includes('horsepower') || q.includes('exhaust') || q.includes('intake')) {
      return getModsResponse(selectedVehicle);
    }
    
    // Value queries
    if (q.includes('value') || q.includes('worth') || q.includes('price') || q.includes('sell')) {
      return getValueResponse(selectedVehicle);
    }
    
    return "I can help with:\n\n🔧 **Maintenance** - \"What service is due?\"\n⚠️ **Troubleshooting** - \"My car is making a noise\"\n🔍 **Parts** - \"Where to find parts for...\"\n⚡ **Mods** - \"What performance mods do you recommend?\"\n💰 **Value** - \"What's my car worth?\"\n\nTry selecting a vehicle above for personalized advice!";
  };

  const handleSend = async () => {
    if (!input.trim()) return;

    const userMessage = input.trim();
    setInput('');
    setActivePrompt(null);
    
    setMessages(prev => [...prev, { role: 'user', content: userMessage }]);
    setLoading(true);

    // Try API first, fall back to local
    try {
      const response = await fetch('http://localhost:8080/api/recommendations/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
          message: userMessage,
          vehicleId: selectedVehicle?.id,
          context: selectedVehicle ? `${selectedVehicle.year} ${selectedVehicle.make} ${selectedVehicle.model}` : null
        }),
      });

      if (response.ok) {
        const data = await response.json();
        setMessages(prev => [...prev, { role: 'assistant', content: data.response }]);
      } else {
        const localResponse = getLocalResponse(userMessage);
        setMessages(prev => [...prev, { role: 'assistant', content: localResponse }]);
      }
    } catch {
      const localResponse = getLocalResponse(userMessage);
      setMessages(prev => [...prev, { role: 'assistant', content: localResponse }]);
    }

    setLoading(false);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950 flex flex-col">
      {/* Header */}
      <header className="px-6 py-4 border-b border-slate-800 bg-slate-900/50 backdrop-blur-sm">
        <div className="max-w-4xl mx-auto flex items-center gap-4">
          <Link to="/dashboard" className="p-2 hover:bg-slate-800 rounded-lg transition-colors">
            <ArrowLeft className="w-5 h-5 text-slate-400" />
          </Link>
          <div className="flex items-center gap-2">
            <div className="p-2 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl">
              <Sparkles className="w-5 h-5 text-white" />
            </div>
            <span className="text-lg font-bold text-white">Garage Assistant</span>
          </div>
          
          {/* Vehicle Selector */}
          <div className="ml-auto">
            <select
              value={selectedVehicle?.id || ''}
              onChange={(e) => {
                const vehicle = vehicles.find(v => v.id === parseInt(e.target.value));
                setSelectedVehicle(vehicle || null);
              }}
              className="px-3 py-2 bg-slate-800 border border-slate-700 rounded-lg text-white text-sm focus:border-primary-500 focus:ring-1 focus:ring-primary-500"
            >
              <option value="">Select a vehicle...</option>
              {vehicles.map(v => (
                <option key={v.id} value={v.id}>{v.name}</option>
              ))}
            </select>
          </div>
        </div>
      </header>

      {/* Vehicle Context Bar */}
      {selectedVehicle && (
        <div className="px-6 py-2 bg-slate-800/30 border-b border-slate-800">
          <div className="max-w-4xl mx-auto flex items-center gap-4 text-sm">
            <Car className="w-4 h-4 text-primary-400" />
            <span className="text-slate-300">{selectedVehicle.name}</span>
            <span className="text-slate-500">•</span>
            <span className="text-slate-400">{selectedVehicle.mileage?.toLocaleString() || 'Unknown'} miles</span>
            <span className="text-slate-500">•</span>
            <span className="text-slate-400">{selectedVehicle.type || 'Standard'}</span>
          </div>
        </div>
      )}

      {/* Messages */}
      <div className="flex-1 overflow-y-auto px-6 py-4">
        <div className="max-w-4xl mx-auto space-y-4">
          {messages.map((msg, idx) => (
            <div key={idx} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
              <div className={`max-w-[85%] p-4 rounded-2xl ${
                msg.role === 'user' 
                  ? 'bg-primary-600 text-white' 
                  : 'bg-slate-800/50 border border-slate-700 text-slate-200'
              }`}>
                <div className="whitespace-pre-wrap text-sm leading-relaxed"
                     dangerouslySetInnerHTML={{ 
                       __html: msg.content
                         .replace(/\*\*(.*?)\*\*/g, '<strong class="text-white">$1</strong>')
                         .replace(/\n/g, '<br/>') 
                     }} 
                />
              </div>
            </div>
          ))}
          {loading && (
            <div className="flex justify-start">
              <div className="bg-slate-800/50 border border-slate-700 p-4 rounded-2xl">
                <Loader2 className="w-5 h-5 text-primary-400 animate-spin" />
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>
      </div>

      {/* Quick Prompts */}
      <div className="px-6 py-2 border-t border-slate-800/50">
        <div className="max-w-4xl mx-auto flex gap-2 overflow-x-auto pb-2">
          {QUICK_PROMPTS.map(({ icon: Icon, label, prompt }) => (
            <button
              key={prompt}
              onClick={() => handleQuickPrompt(prompt)}
              className={`flex items-center gap-2 px-3 py-1.5 rounded-full text-sm whitespace-nowrap transition-colors ${
                activePrompt === prompt
                  ? 'bg-primary-600 text-white'
                  : 'bg-slate-800/50 text-slate-400 hover:bg-slate-700 hover:text-white'
              }`}
            >
              <Icon className="w-4 h-4" />
              {label}
            </button>
          ))}
        </div>
      </div>

      {/* Input */}
      <div className="px-6 py-4 border-t border-slate-800 bg-slate-900/50 backdrop-blur-sm">
        <div className="max-w-4xl mx-auto flex gap-3">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            placeholder="Ask about maintenance, troubleshooting, parts, mods..."
            className="flex-1 px-4 py-3 bg-slate-800 border border-slate-700 rounded-xl text-white placeholder-slate-500 focus:border-primary-500 focus:ring-1 focus:ring-primary-500"
          />
          <button
            onClick={handleSend}
            disabled={loading || !input.trim()}
            className="px-4 py-3 bg-primary-600 text-white rounded-xl hover:bg-primary-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Send className="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default Assistant;
