# MultiCloudAPI
API com suporte ao Oracle Cloud para backup e recuperação, além de continuar com suporte ao AWS, GCP e Azure.


![new sem titulo](https://github.com/user-attachments/assets/7a9b8484-c5bb-40f6-9805-8c5d4579e1ff)



versão atualizada da API com suporte ao Oracle Cloud para backup e recuperação, além de continuar com suporte ao AWS, GCP e Azure.


I# MultiCloudProject

Um sistema completo para gerenciamento de arquivos em múltiplas nuvens (AWS, Azure, OCI, GCP), com backend em Java/Spring Boot e frontend em React.

## Estrutura
- **backend/**: API RESTful escrita em Java com Spring Boot.
- **frontend/**: Interface web em React para interagir com a API.

## Pré-requisitos
- Java 17 (para o backend)
- Node.js e npm (para o frontend)
- Credenciais configuradas para AWS, Azure, OCI e GCP
- Docker (opcional, para containerização)

## Como Rodar

### Backend
1. Navegue até `backend/`:
   ```bash
   cd backend


   Configure as credenciais em src/main/resources/application.properties.
Build e rode:
mvn package
java -jar target/multicloudapi-1.0.0-RELEASE.jar
Ou com Docker:

docker build -t multicloudapi .
docker run -p 8080:8080 -e API_KEY=seu_segredo_aqui multicloudapi
Frontend
Navegue até frontend/:

cd frontend
Instale as dependências:

npm install
Atualize a chave da API nos arquivos FileUpload.js, FileList.js e FileActions.js.


npm start
Acesse http://localhost:3000 no navegador.

Endpoints da API
POST /api/storage/upload: Upload de arquivo.
GET /api/storage/download: Download de arquivo.
DELETE /api/storage/delete: Exclusão de arquivo.
GET /api/storage/list: Listagem paginada de arquivos.

Frontend
Interface simples com upload, listagem paginada e ações (download/delete).
Contribuições
Sinta-se à vontade para abrir issues ou enviar pull requests!
rodrigo.digau@gmail.com
