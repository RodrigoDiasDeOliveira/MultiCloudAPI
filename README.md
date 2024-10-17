# MultiCloudAPI
API com suporte ao Oracle Cloud para backup e recuperação, além de continuar com suporte ao AWS, GCP e Azure.


![multiCloud backups](https://github.com/user-attachments/assets/7f65b175-46f1-4ceb-98a4-7ab22d3c233a)


versão atualizada da API com suporte ao Oracle Cloud para backup e recuperação, além de continuar com suporte ao AWS, GCP e Azure.
****Será necessário utilizar a biblioteca oficial do Oracle Cloud SDK para Python.****

Instalação do SDK do Oracle Cloud:
Primeiro, instale o SDK do Oracle Cloud usando o pip:
bash
pip install oci

Estrutura de Diretórios do Projeto Atualizada:

bash

/multicloud-api
│
├── main.py                # Arquivo principal para rodar a API
├── auth/                  # Autenticação e autorização (OAuth2, JWT)
│   ├── auth.py            # Lógica de autenticação e autorização
│   └── models.py          # Modelos de autenticação
├── db/                    # Conexões de bancos de dados
│   ├── base.py            # Configuração do SQLAlchemy
│   └── models.py          # Modelos de bancos de dados
├── cloud/                 # Integrações com provedores de nuvem
│   ├── aws.py             # Integração com AWS
│   ├── gcp.py             # Integração com GCP
│   ├── azure.py           # Integração com Azure
│   └── oracle.py          # Integração com Oracle Cloud
├── services/              # Serviços de backup, sincronização e governança
│   ├── sync.py            # Sincronização entre bancos de dados
│   ├── backup.py          # Backup multicloud
│   └── governance.py      # Governança de dados
├── search/                # Suporte a busca e análise de dados
│   └── elasticsearch.py   # Conexão com Elasticsearch
└── tasks/                 # Tarefas assíncronas com Celery
    ├── celery.py          # Configuração do Celery
    └── tasks.py           # Tarefas de backup e sincronização

    --------------------------------------------------------------------------------------------------------------------
1. Integração com Oracle Cloud
Criaremos um módulo para interagir com o Oracle Cloud, especificamente para realizar backups e gerenciamento de objetos (similar ao AWS S3, Azure Blob, e GCP Buckets).

cloud/oracle.py
python
Copiar código
import oci

# Configuração do cliente Oracle Cloud
config = oci.config.from_file("~/.oci/config", "DEFAULT")
object_storage_client = oci.object_storage.ObjectStorageClient(config)
namespace = object_storage_client.get_namespace().data

def list_oracle_buckets(compartment_id):
    buckets = object_storage_client.list_buckets(namespace, compartment_id)
    return [bucket.name for bucket in buckets.data]

def upload_to_oracle(bucket_name, object_name, file_path):
    with open(file_path, 'rb') as file_data:
        response = object_storage_client.put_object(namespace, bucket_name, object_name, file_data)
    return response.headers

def download_from_oracle(bucket_name, object_name, file_path):
    response = object_storage_client.get_object(namespace, bucket_name, object_name)
    with open(file_path, 'wb') as file:
        for chunk in response.data.raw.stream(1024 * 1024, decode_content=False):
            file.write(chunk)
    return {"status": "Downloaded successfully"}

    **********************************************************************************************************************
    
2. Backup Multicloud com Oracle Cloud
Aqui, vou incluir o Oracle Cloud no serviço de backup.

services/backup.py
python
from cloud.aws import backup_aws
from cloud.gcp import backup_gcp
from cloud.azure import backup_azure
from cloud.oracle import upload_to_oracle
from tasks.tasks import perform_multicloud_backup

def initiate_backup():
    perform_multicloud_backup.delay()  # Executa backup em segundo plano
    return {"status": "Backup initiated"}

def backup_to_oracle(bucket_name, object_name, file_path):
    return upload_to_oracle(bucket_name, object_name, file_path)

*****************************************************************************************************************

3. Tarefa Assíncrona para Backup Multicloud
Incluímos a Oracle Cloud no processo de backup, executado em segundo plano.

tasks/tasks.py
python
Copiar código
from celery import Celery
from cloud.aws import backup_aws
from cloud.gcp import backup_gcp
from cloud.azure import backup_azure
from cloud.oracle import upload_to_oracle

celery_app = Celery('tasks', broker='redis://localhost:6379/0')

@celery_app.task
def perform_multicloud_backup():
    # Realiza o backup em AWS, GCP, Azure e Oracle Cloud
    backup_aws()
    backup_gcp()
    backup_azure()
    upload_to_oracle(bucket_name="oracle-bucket", object_name="backup.tar.gz", file_path="/path/to/backup/file")
    return "Backup completed"

    ***************************************************************************************************************************
    
4. Atualização no main.py
Aqui, adicionamos endpoints para listar buckets e realizar uploads e downloads com o Oracle Cloud.

main.py
python
Copiar código
from fastapi import FastAPI, Depends
from auth.auth import authenticate_user, create_access_token, get_current_user
from services.sync import sync_data
from services.backup import initiate_backup, backup_to_oracle
from cloud.oracle import list_oracle_buckets
from search.elasticsearch import index_unstructured_data, search_data
from datetime import timedelta
from auth.models import Token, User

app = FastAPI()

# Autenticação e Autorização
@app.post("/token", response_model=Token)
async def login(form_data: OAuth2PasswordRequestForm = Depends()):
    user = authenticate_user(fake_users_db, form_data.username, form_data.password)
    if not user:
        raise HTTPException(status_code=400, detail="Incorrect username or password")
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(data={"sub": user["email"]}, expires_delta=access_token_expires)
    return {"access_token": access_token, "token_type": "bearer"}

@app.get("/users/me", response_model=User)
async def read_users_me(current_user: User = Depends(get_current_user)):
    return current_user

# Sincronização de Bancos de Dados
@app.get("/sync")
def sync_databases():
    return sync_data()

# Backup Multicloud
@app.get("/backup")
def trigger_backup():
    return initiate_backup()

# Oracle Cloud - Listar Buckets
@app.get("/oracle/buckets")
def list_oracle_buckets_endpoint(compartment_id: str):
    return list_oracle_buckets(compartment_id)

# Oracle Cloud - Realizar Backup
@app.post("/oracle/upload")
def backup_to_oracle_endpoint(bucket_name: str, object_name: str, file_path: str):
    return backup_to_oracle(bucket_name, object_name, file_path)

# Dados Não Estruturados
@app.post("/index")
def index_data(data: str):
    return index_unstructured_data(data)

@app.get("/search")
def search_index(query: str):
    return search_data(query)

    ****************************************************************************************************************************************
    
5. Configuração do SDK do Oracle Cloud
Para que o SDK da Oracle funcione corretamente, você precisa configurar seu arquivo ~/.oci/config. Ele deve seguir o seguinte formato:

makefile
[DEFAULT]
user=<your_user_ocid>
fingerprint=<your_fingerprint>
key_file=<path_to_private_key>
tenancy=<your_tenancy_ocid>
region=<region>


Funcionalidades Adicionadas:
Integração com Oracle Cloud para upload, download e listagem de buckets.
Tarefas assíncronas de backup multicloud, agora incluindo Oracle Cloud.
Endpoints para realizar backup no Oracle Cloud e listar buckets do Oracle.

Agora a API está pronta para fazer backups e recuperar dados de AWS, GCP, Azure e Oracle Cloud. 

Para testar isso, rodando a aplicação com o uvicorn:
bash
uvicorn main:app --reload
