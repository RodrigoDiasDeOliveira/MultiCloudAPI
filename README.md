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


atualizado em 26/02/2025
# 📌 Guia de Configuração e Execução - MultiCloudAPI

## 1. Pré-requisitos
Antes de iniciar, certifique-se de ter instalado:
- **Java 17+** (para o backend)
- **Node.js 18+** (para o frontend)
- **Docker** (caso queira executar o banco de dados localmente)
- **Maven** (para gerenciamento do backend)

## 2. Configuração das Credenciais dos Provedores de Nuvem
O MultiCloudAPI suporta múltiplos provedores de nuvem. As credenciais devem ser configuradas no **arquivo de propriedades do Spring Boot** (`application.yml`) ou como **variáveis de ambiente**.

### 2.1 AWS (Amazon Web Services)
1. Acesse o [AWS IAM](https://console.aws.amazon.com/iam/).
2. Crie um novo usuário com permissões necessárias.
3. Gere uma **Access Key** e uma **Secret Key**.
4. Configure no `application.yml` ou como variáveis de ambiente:

#### Configuração no `application.yml`
```yaml
cloud:
  aws:
    access-key: YOUR_AWS_ACCESS_KEY
    secret-key: YOUR_AWS_SECRET_KEY
    region: us-east-1
```

#### Configuração via variáveis de ambiente
```sh
export AWS_ACCESS_KEY=YOUR_AWS_ACCESS_KEY
export AWS_SECRET_KEY=YOUR_AWS_SECRET_KEY
export AWS_REGION=us-east-1
```

### 2.2 Microsoft Azure
1. Acesse o [portal da Azure](https://portal.azure.com).
2. Crie um aplicativo no **Azure Active Directory**.
3. Gere um **Client ID**, **Tenant ID** e **Client Secret**.
4. Configure no `application.yml` ou como variáveis de ambiente:

#### Configuração no `application.yml`
```yaml
cloud:
  azure:
    client-id: YOUR_AZURE_CLIENT_ID
    tenant-id: YOUR_AZURE_TENANT_ID
    client-secret: YOUR_AZURE_CLIENT_SECRET
```

#### Configuração via variáveis de ambiente
```sh
export AZURE_CLIENT_ID=YOUR_AZURE_CLIENT_ID
export AZURE_TENANT_ID=YOUR_AZURE_TENANT_ID
export AZURE_CLIENT_SECRET=YOUR_AZURE_CLIENT_SECRET
```

### 2.3 Google Cloud Platform (GCP)
1. Acesse o [Console da GCP](https://console.cloud.google.com/).
2. Crie um service account em **IAM & Admin**.
3. Gere um arquivo JSON de credenciais.
4. Configure no `application.yml` ou como variável de ambiente:

#### Configuração no `application.yml`
```yaml
cloud:
  gcp:
    credentials-path: /caminho/para/seu-arquivo.json
    project-id: seu-projeto-id
```

#### Configuração via variável de ambiente
```sh
export GOOGLE_APPLICATION_CREDENTIALS="/caminho/para/seu-arquivo.json"
```

### 2.4 Oracle Cloud Infrastructure (OCI)
1. Acesse o [Oracle Cloud Console](https://cloud.oracle.com/).
2. Gere um par de chaves RSA para autenticação.
3. Configure o arquivo `~/.oci/config`:

#### Exemplo de configuração em `~/.oci/config`
```
[DEFAULT]
user=ocid1.user.oc1..xxxxx
fingerprint=xx:xx:xx:xx:xx
key_file=/caminho/para/sua-chave.pem
tenancy=ocid1.tenancy.oc1..xxxxx
region=us-ashburn-1
```

#### Configuração no `application.yml`
```yaml
cloud:
  oracle:
    user: ocid1.user.oc1..xxxxx
    fingerprint: xx:xx:xx:xx:xx
    private-key-path: /caminho/para/sua-chave.pem
    tenancy: ocid1.tenancy.oc1..xxxxx
    region: us-ashburn-1
```

#### Configuração via variáveis de ambiente
```sh
export OCI_CONFIG_FILE=~/.oci/config
```

## 3. Como Rodar o Projeto?

### 3.1 Backend (Spring Boot)
Para executar o backend:
```sh
cd backend
mvn spring-boot:run
```

Se quiser rodar com um banco de dados em Docker:
```sh
docker-compose up -d
```

### 3.2 Frontend (React)
Para rodar o frontend:
```sh
cd frontend
npm install
npm start
```

## 4. Testando a API
Acesse a API para verificar se está funcionando:
```
http://localhost:8080/api/status
```
Se estiver funcionando, você verá uma resposta JSON com o status da API.

## 5. Melhorias Futuras
- [ ] Criar um script automatizado para configuração de credenciais
- [ ] Implementar testes automatizados para validação das credenciais
- [ ] Melhorar logging para debugging mais eficiente



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
