import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Landing from './pages/Landing';
import Dashboard from './pages/Dashboard';
import VehicleEdit from './pages/VehicleEdit';
import ForumHome from './pages/forum/ForumHome';
import Assistant from './pages/Assistant';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/vehicle/edit/:id" element={<VehicleEdit />} />
        <Route path="/vehicle/new" element={<VehicleEdit />} />
        <Route path="/forum" element={<ForumHome />} />
        <Route path="/assistant" element={<Assistant />} />
      </Routes>
    </Router>
  );
}

export default App;
