import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-gray-100">
        <header className="bg-blue-600 text-white p-4 shadow-md">
          <div className="container mx-auto">
            <h1 className="text-2xl font-bold">Entenda sua Fatura de Energia</h1>
          </div>
        </header>
        <main className="container mx-auto p-4">
          <Routes>
            <Route path="/" element={<Home />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

function Home() {
  return (
    <div className="bg-white rounded-lg shadow-lg p-6 mt-4">
      <h2 className="text-xl font-semibold mb-4 text-gray-800">Bem-vindo!</h2>
      <p className="text-gray-600">
        Sistema de análise de faturas de energia elétrica.
      </p>
      <div className="mt-6 p-4 bg-blue-50 rounded border border-blue-200">
        <p className="text-sm text-blue-800">
          Faça upload de sua fatura de energia para começar a análise.
        </p>
      </div>
    </div>
  );
}

export default App;
