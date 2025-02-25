import React, { useState } from 'react';
import FileUpload from './components/FileUpload';
import FileList from './components/FileList';

function App() {
  const [provider, setProvider] = useState('AWS');
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
      <FileUpload provider={provider} />
      <FileList provider={provider} />
    </div>
  );
}

export default App;