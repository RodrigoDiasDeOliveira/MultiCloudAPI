import React, { useState, useEffect } from 'react';
import axios from 'axios';

const LogViewer = () => {
  const [logs, setLogs] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchLogs = async () => {
    setLoading(true);
    try {
      const response = await axios.get('http://localhost:8080/api/storage/logs', {
        params: { page, size },
        headers: { 'X-API-Key': 'seu_segredo_aqui' },
      });
      setLogs(response.data.content || []);
      setError('');
    } catch (err) {
      setError('Erro ao carregar logs: ' + (err.response?.data || err.message));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, [page]);

  const handleNextPage = () => setPage(page + 1);
  const handlePrevPage = () => setPage(page > 0 ? page - 1 : 0);

  return (
    <div className="card">
      <div className="card-body">
        <h5 className="card-title">Logs de Operações</h5>
        {loading && <p>Carregando...</p>}
        {error && <p className="text-danger">{error}</p>}
        <table className="table">
          <thead>
            <tr>
              <th>Data/Hora</th>
              <th>Operação</th>
              <th>Provedor</th>
              <th>Arquivo</th>
              <th>Status</th>
              <th>Mensagem</th>
            </tr>
          </thead>
          <tbody>
            {logs.map((log) => (
              <tr key={log.id}>
                <td>{log.timestamp}</td>
                <td>{log.operation}</td>
                <td>{log.provider}</td>
                <td>{log.fileName || '-'}</td>
                <td>{log.status}</td>
                <td>{log.message}</td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className="d-flex justify-content-between">
          <button className="btn btn-secondary" onClick={handlePrevPage} disabled={page === 0}>
            Anterior
          </button>
          <span>Página {page + 1}</span>
          <button className="btn btn-secondary" onClick={handleNextPage} disabled={logs.length < size}>
            Próxima
          </button>
        </div>
      </div>
    </div>
  );
};

export default LogViewer;