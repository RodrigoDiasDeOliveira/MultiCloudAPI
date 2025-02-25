import React, { useState } from 'react';
import axios from 'axios';

const FileUpload = ({ provider }) => {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState('');

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    if (!file) {
      setMessage('Selecione um arquivo primeiro!');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);
    formData.append('provider', provider);

    try {
      const response = await axios.post('http://localhost:8080/api/storage/upload', formData, {
        headers: {
          'X-API-Key': 'seu_segredo_aqui', // Substitua pela sua chave
        },
      });
      setMessage(response.data.message);
    } catch (error) {
      setMessage('Erro ao fazer upload: ' + (error.response?.data || error.message));
    }
  };

  return (
    <div className="card mb-4">
      <div className="card-body">
        <h5 className="card-title">Upload de Arquivo</h5>
        <input type="file" className="form-control mb-2" onChange={handleFileChange} />
        <button className="btn btn-primary" onClick={handleUpload}>
          Upload para {provider}
        </button>
        {message && <p className="mt-2">{message}</p>}
      </div>
    </div>
  );
};

export default FileUpload;