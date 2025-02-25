import React, { useState } from 'react';
import axios from 'axios';

const FileActions = ({ fileName, provider, onActionComplete }) => {
  const [message, setMessage] = useState('');

  const handleDownload = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/storage/download', {
        params: { fileName, provider },
        headers: { 'X-API-Key': 'seu_segredo_aqui' },
        responseType: 'blob',
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
      link.remove();
      setMessage('Download concluído!');
    } catch (error) {
      setMessage('Erro ao baixar: ' + (error.response?.data || error.message));
    }
  };

  const handleDelete = async () => {
    if (window.confirm(`Tem certeza que deseja deletar ${fileName}?`)) {
      try {
        const response = await axios.delete('http://localhost:8080/api/storage/delete', {
          params: { fileName, provider },
          headers: { 'X-API-Key': 'seu_segredo_aqui' },
        });
        setMessage(response.data.message);
        onActionComplete();
      } catch (error) {
        setMessage('Erro ao deletar: ' + (error.response?.data || error.message));
      }
    }
  };

  return (
    <div>
      <button className="btn btn-success btn-sm me-2" onClick={handleDownload}>
        Download
      </button>
      <button className="btn btn-danger btn-sm" onClick={handleDelete}>
        Deletar
      </button>
      {message && <p className="mt-2 small">{message}</p>}
    </div>
  );
};

export default FileActions;