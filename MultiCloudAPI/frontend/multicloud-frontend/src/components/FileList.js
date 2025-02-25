import React, { useState, useEffect } from 'react';
import axios from 'axios';
import FileActions from './FileActions';

const FileList = ({ provider }) => {
  const [files, setFiles] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchFiles = async () => {
    setLoading(true);
    try {
      const response = await axios.get('http://localhost:8080/api/storage/list', {
        params: { provider, page, size },
        headers: { 'X-API-Key': 'seu_segredo_aqui' },
      });
      setFiles(response.data.data.files || []);
      setError('');
    } catch (err) {
      setError('Erro ao listar arquivos: ' + (err.response?.data || err.message));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFiles();
  }, [provider, page]);

  const handleNextPage = () => setPage(page + 1);
  const handlePrevPage = () => setPage(page > 0 ? page - 1 : 0);

  return (
    <div className="card">
      <div className="card-body">
        <h5 className="card-title">Arquivos em {provider}</h5>
        {loading && <p>Carregando...</p>}
        {error && <p className="text-danger">{error}</p>}
        <table className="table">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Tamanho (bytes)</th>
              <th>Data de Upload</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {files.map((file) => (
              <tr key={file.fileName}>
                <td>{file.fileName}</td>
                <td>{file.size}</td>
                <td>{file.uploadTime}</td>
                <td>
                  <FileActions fileName={file.fileName} provider={provider} onActionComplete={fetchFiles} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className="d-flex justify-content-between">
          <button className="btn btn-secondary" onClick={handlePrevPage} disabled={page === 0}>
            Anterior
          </button>
          <span>Página {page + 1}</span>
          <button className="btn btn-secondary" onClick={handleNextPage} disabled={files.length < size}>
            Próxima
          </button>
        </div>
      </div>
    </div>
  );
};

export default FileList;