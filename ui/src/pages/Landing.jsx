import React from 'react';
import { Link } from 'react-router-dom';
import { Car, Shield, BarChart3, Settings, ArrowRight, Sparkles, Wrench, MessageCircle } from 'lucide-react';

const Landing = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950">
      {/* Navigation */}
      <nav className="relative z-10 px-6 py-6 md:px-12">
        <div className="flex items-center justify-between max-w-7xl mx-auto">
          <div className="flex items-center space-x-2">
            <div className="p-2 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl">
              <Car className="w-6 h-6 text-white" />
            </div>
            <span className="text-xl font-bold bg-gradient-to-r from-white to-slate-300 bg-clip-text text-transparent">
              Virtual Garage
            </span>
          </div>
          <Link
            to="/dashboard"
            className="px-6 py-2 bg-gradient-to-r from-primary-600 to-primary-700 text-white rounded-lg font-medium hover:from-primary-700 hover:to-primary-800 transition-all duration-200 shadow-lg shadow-primary-500/25"
          >
            Enter Garage
          </Link>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="relative px-6 py-20 md:px-12">
        <div className="max-w-7xl mx-auto">
          {/* Background Effects */}
          <div className="absolute inset-0 overflow-hidden">
            <div className="absolute -top-40 -right-40 w-80 h-80 bg-primary-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>
            <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-purple-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse delay-1000"></div>
          </div>

          <div className="relative z-10 text-center">
            <div className="inline-flex items-center px-4 py-2 bg-slate-800/50 border border-slate-700 rounded-full mb-8 backdrop-blur-sm">
              <Sparkles className="w-4 h-4 text-primary-400 mr-2" />
              <span className="text-sm text-slate-300">Manage your dream collection</span>
            </div>
            
            <h1 className="text-5xl md:text-7xl font-bold mb-6 leading-tight">
              <span className="bg-gradient-to-r from-white via-slate-200 to-slate-400 bg-clip-text text-transparent">
                Your Digital
              </span>
              <br />
              <span className="bg-gradient-to-r from-primary-400 to-primary-600 bg-clip-text text-transparent">
                Dream Garage
              </span>
            </h1>
            
            <p className="text-xl md:text-2xl text-slate-400 mb-12 max-w-3xl mx-auto leading-relaxed">
              Organize, track, and showcase your vehicle collection with our beautiful, 
              modern platform designed for automotive enthusiasts.
            </p>
            
            <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
              <Link
                to="/dashboard"
                className="group px-8 py-4 bg-gradient-to-r from-primary-600 to-primary-700 text-white rounded-xl font-semibold hover:from-primary-700 hover:to-primary-800 transition-all duration-300 shadow-2xl shadow-primary-500/30 flex items-center gap-2 text-lg"
              >
                View My Garage
                <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
              </Link>
              <button className="px-8 py-4 bg-slate-800/50 text-white rounded-xl font-semibold hover:bg-slate-700/50 transition-all duration-200 border border-slate-700 backdrop-blur-sm text-lg">
                Watch Demo
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="px-6 py-20 md:px-12">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-bold mb-4 bg-gradient-to-r from-white to-slate-300 bg-clip-text text-transparent">
              Everything you need
            </h2>
            <p className="text-xl text-slate-400 max-w-2xl mx-auto">
              Powerful features to help you manage and showcase your automotive collection
            </p>
          </div>
          
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            <FeatureCard
              icon={<Sparkles className="w-8 h-8 text-primary-400" />}
              title="AI Garage Assistant"
              description="Get personalized maintenance reminders, troubleshoot issues, find parts, and get modification recommendations"
              highlight
            />
            <FeatureCard
              icon={<Car className="w-8 h-8 text-blue-400" />}
              title="Vehicle Management"
              description="Add, edit, and organize your vehicles with detailed specifications and high-quality photos"
            />
            <FeatureCard
              icon={<Wrench className="w-8 h-8 text-orange-400" />}
              title="Maintenance Tracking"
              description="Never miss an oil change or service with smart maintenance schedules based on mileage"
            />
            <FeatureCard
              icon={<BarChart3 className="w-8 h-8 text-green-400" />}
              title="Value Tracking"
              description="Monitor your collection's value over time with market insights and appreciation tracking"
            />
            <FeatureCard
              icon={<Shield className="w-8 h-8 text-purple-400" />}
              title="Secure Storage"
              description="Your data is safely stored with enterprise-grade security and regular backups"
            />
            <FeatureCard
              icon={<MessageCircle className="w-8 h-8 text-yellow-400" />}
              title="Community Forums"
              description="Connect with other enthusiasts, share builds, and get advice from the community"
            />
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="px-6 py-20 md:px-12">
        <div className="max-w-4xl mx-auto text-center">
          <div className="bg-gradient-to-r from-slate-800/50 to-slate-700/50 backdrop-blur-sm border border-slate-600 rounded-2xl p-12">
            <h3 className="text-3xl md:text-4xl font-bold mb-4 bg-gradient-to-r from-white to-slate-300 bg-clip-text text-transparent">
              Ready to start your collection?
            </h3>
            <p className="text-xl text-slate-400 mb-8">
              Join thousands of automotive enthusiasts who trust Virtual Garage
            </p>
            <Link
              to="/dashboard"
              className="inline-flex items-center px-8 py-4 bg-gradient-to-r from-primary-600 to-primary-700 text-white rounded-xl font-semibold hover:from-primary-700 hover:to-primary-800 transition-all duration-300 shadow-2xl shadow-primary-500/30 gap-2 text-lg"
            >
              Get Started Free
              <ArrowRight className="w-5 h-5" />
            </Link>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="px-6 py-12 md:px-12 border-t border-slate-800">
        <div className="max-w-7xl mx-auto text-center">
          <div className="flex items-center justify-center space-x-2 mb-4">
            <div className="p-1 bg-gradient-to-br from-primary-500 to-primary-600 rounded-lg">
              <Car className="w-5 h-5 text-white" />
            </div>
            <span className="text-lg font-semibold text-white">Virtual Garage</span>
          </div>
          <p className="text-slate-400">
            © 2024 Virtual Garage. Built with passion for automotive enthusiasts.
          </p>
        </div>
      </footer>
    </div>
  );
};

const FeatureCard = ({ icon, title, description, highlight = false }) => {
  return (
    <div className={`group p-6 backdrop-blur-sm rounded-xl transition-all duration-300 hover:shadow-xl hover:shadow-slate-900/50 ${
      highlight 
        ? 'bg-gradient-to-br from-primary-900/40 to-primary-800/20 border-2 border-primary-500/50 hover:border-primary-400/70' 
        : 'bg-gradient-to-br from-slate-800/50 to-slate-700/30 border border-slate-600 hover:border-slate-500'
    }`}>
      <div className={`mb-4 p-3 rounded-lg w-fit transition-colors ${
        highlight 
          ? 'bg-gradient-to-br from-primary-500 to-primary-600' 
          : 'bg-slate-700/50 group-hover:bg-slate-600/50'
      }`}>
        {highlight ? React.cloneElement(icon, { className: 'w-8 h-8 text-white' }) : icon}
      </div>
      <h3 className="text-xl font-semibold text-white mb-3">{title}</h3>
      <p className="text-slate-400 leading-relaxed">{description}</p>
      {highlight && (
        <div className="mt-4 flex items-center text-primary-400 text-sm font-medium">
          <span>Try it now</span>
          <ArrowRight className="w-4 h-4 ml-1 group-hover:translate-x-1 transition-transform" />
        </div>
      )}
    </div>
  );
};

export default Landing;