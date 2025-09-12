import { useState } from 'react'

export default function App() {
  const [count, setCount] = useState(0)
  
  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: '#f3f4f6',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      fontFamily: 'system-ui, -apple-system, sans-serif'
    }}>
      <div style={{
        backgroundColor: 'white',
        padding: '2rem',
        borderRadius: '8px',
        boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
        textAlign: 'center',
        maxWidth: '400px',
        width: '100%'
      }}>
        <div style={{
          fontSize: '2rem',
          marginBottom: '1rem'
        }}>
          🚗 Virtual Garage
        </div>
        
        <h1 style={{
          fontSize: '1.5rem',
          fontWeight: 'bold',
          marginBottom: '1rem',
          color: '#1f2937'
        }}>
          React is Working!
        </h1>
        
        <p style={{
          color: '#6b7280',
          marginBottom: '2rem'
        }}>
          This is a test to make sure the basic setup works.
        </p>
        
        <div style={{ marginBottom: '1rem' }}>
          <button 
            onClick={() => setCount(count + 1)}
            style={{
              backgroundColor: '#3b82f6',
              color: 'white',
              border: 'none',
              padding: '0.5rem 1rem',
              borderRadius: '4px',
              cursor: 'pointer',
              marginRight: '0.5rem'
            }}
          >
            Count: {count}
          </button>
          
          <button 
            onClick={() => alert('JavaScript is working!')}
            style={{
              backgroundColor: '#10b981',
              color: 'white',
              border: 'none',
              padding: '0.5rem 1rem',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Test Alert
          </button>
        </div>
        
        <div style={{
          fontSize: '0.875rem',
          color: '#6b7280'
        }}>
          ✅ Vite + React + TypeScript working<br/>
          ✅ State management working<br/>
          ✅ Event handlers working<br/>
          ✅ Styling working
        </div>
      </div>
    </div>
  )
}