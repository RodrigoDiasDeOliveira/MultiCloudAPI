import React, { useState } from 'react';
import FileUpload from './components/FileUpload';
import FileList from './components/FileList';
import LogViewer from './components/LogViewer';

function App() {
  const [provider, setProvider] = useState('AWS');
  const [activeTab, setActiveTab] = useState('files');
  const providers = ['AWS', 'Azure', 'OCI', 'GCP'];

  return (
    <div className="container mt-5">
      <h1 className="text-center mb-4">MultiCloud API</h1>
      <div className="mb-3">
        <label htmlFor="providerSelect" className="form-label">Escolha o Provedor:</label>
        <select
          id="providerSelect"
          className="form-select"
          value={provider}
          onChange={(e) => setProvider(e.target.value)}
        >
          {providers.map((prov) => (
            <option key={prov} value={prov}>{prov}</option>
          ))}
        </select>
      </div>
      <ul className="nav nav-tabs mb-4">
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'files' ? 'active' : ''}`}
            onClick={() => setActiveTab('files')}
          >
            Arquivos
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'logs' ? 'active' : ''}`}
            onClick={() => setActiveTab('logs')}
          >
            Logs
          </button>
        </li>
      </ul>
      {activeTab === 'files' ? (
        <>
          <FileUpload provider={provider} />
          <FileList provider={provider} />
        </>
      ) : (
        <LogViewer />
      )}
    </div>
  );
}

export default App;