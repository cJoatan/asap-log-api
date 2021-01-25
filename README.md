# REST API AVALIACÃO ASAP

Tecnolgias: Java8, Gradle, Springboot, MongoDB


## Executar app
gradle bootRun

## Executar testes
./gradlew test

# REST API

## A API REST para o app é descrita abaixo.

## Lista de Clientes

### Requisição

`GET api/clients/`

### Resposta
HTTP/1.1 200 OK
Status: 200 OK
Content-Type: application/json
```json
{
    size: 10,
    number: 0,
    totalElements: 2,
    last: true,
    totalPages: 1,
    sort: {
        sorted: false,
        unsorted: true,
        empty: true
    },
    first: true,
    numberOfElements: 2,
    content: [
        {
            id: "600c6c9db16df808354b113f",
            name: "Cicero",
            cpf: "03957835348",
            city: "Fortaleza",
            uf: "CE"
        }
    ]
}
```

## Criar Cliente com parâmetros válidos

### Requisição
`POST api/clients`
```json
{
name: "Cicero",
cpf: "03957835348",
city: "Fortaleza",
uf: "CE"
}
```
### Resposta
HTTP/1.1 201 CREATED
Status: 201 CREATED
Content-Type: application/json
```json
{
id: "600c6c9db16df808354b113f",
name: "Cicero",
cpf: "03957835348",
city: "Fortaleza",
uf: "CE"
}
```
## Criar Cliente com parametros inválidos

### Requisição
`POST api/clients`
```json
{
name: "Cicero",
cpf: "03957835348",
city: "Fortaleza",
uf: "CE"
}
```
### Resposta
```json
[
    {
        "codes": [
            "NotEmpty.client.cpf",
            "NotEmpty.cpf",
            "NotEmpty.java.lang.String",
            "NotEmpty"
        ],
        "defaultMessage": "CPF é obrigatório",
        "objectName": "client"
    },
    {
        "codes": [
            "NotEmpty.client.uf",
            "NotEmpty.uf",
            "NotEmpty.java.lang.String",
            "NotEmpty"
        ],
        "defaultMessage": "Estado é obrigatório",
        "objectName": "client"
    },
    {
        "codes": [
            "NotEmpty.client.city",
            "NotEmpty.city",
            "NotEmpty.java.lang.String",
            "NotEmpty"
        ],
        "defaultMessage": "Cidade é obrigatória",
        "objectName": "client"
    },
    {
        "codes": [
            "NotEmpty.client.name",
            "NotEmpty.name",
            "NotEmpty.java.lang.String",
            "NotEmpty"
        ],
        "defaultMessage": "Nome é obrigatório",
        "objectName": "client"
    }
]
```
## Requisitar um Cliente existente

### Requisição
`GET api/clients/1`

### Resposta
HTTP/1.1 200 OK
Status: 200 OK
Content-Type: application/json
```json
{
id: "600c6c9db16df808354b113f",
name: "Cicero",
cpf: "03957835348",
city: "Fortaleza",
uf: "CE"
}
```
## Requisitar um Cliente Não existente

### Requisição
`GET api/clients/999` 

### Resposta

HTTP/1.1 404 NOT FOUND
Status: 404 NOT FOUND
Content-Type: application/json


## Atualizar um Cliente com parâmetros válidos

### Requisição
`PUT api/clients/1`
```json
{
name: "Cicero",
cpf: "03957835348",
city: "Fortaleza",
uf: "CE"
}
```
### Resposta
HTTP/1.1 200 OK
Status: 200 OK
Content-Type: application/json
```json
{
id: "600c6c9db16df808354b113f",
name: "Cicero",
cpf: "03957835348",
city: "Fortaleza",
uf: "CE"
}
```
## Atualizar um Cliente não existente

### Requisição
`POST api/clients/999` 
 
### Resposta
HTTP/1.1 404 NOT FOUND
Status: 404 NOT FOUND
Content-Type: application/json

## Atualizar um Cliente com parâmetros inválidos

### Requisição
`PUT api/clients/1`
{}

### Resposta
HTTP/1.1 422 UNPROCESABLE ENTITY
Status: 422 UNPROCESABLE ENTITY
Content-Type: application/json
```json
[
    {
        "codes": [
            "NotEmpty.client.cpf",
            "NotEmpty.cpf",
            "NotEmpty.java.lang.String",
            "NotEmpty"
        ],
        "defaultMessage": "CPF é obrigatório",
        "objectName": "client"
    },
    {
        "codes": [
            "NotEmpty.client.uf",
            "NotEmpty.uf",
            "NotEmpty.java.lang.String",
            "NotEmpty"
        ],
        "defaultMessage": "Estado é obrigatório",
        "objectName": "client"
    },
    {
        "codes": [
            "NotEmpty.client.city",
            "NotEmpty.city",
            "NotEmpty.java.lang.String",
            "NotEmpty"
        ],
        "defaultMessage": "Cidade é obrigatória",
        "objectName": "client"
    },
    {
        "codes": [
            "NotEmpty.client.name",
            "NotEmpty.name",
            "NotEmpty.java.lang.String",
            "NotEmpty"
        ],
        "defaultMessage": "Nome é obrigatório",
        "objectName": "client"
    }
]
```

## Deletar um existente
### Requisição
`DELETE api/clients/1`

### Resposta
HTTP/1.1 200 0K
Status: 200 0K
Content-Type: application/json


## Deletar um não existente
### Requisição
`DELETE api/clients/999`

### Resposta
HTTP/1.1 404 NOT FOUND
Status: 404 NOT FOUND
Content-Type: application/json


## Lista de apólices

### Requisição

`GET api/policies/`

### Resposta
```json
{
size: 20,
number: 0,
totalElements: 1,
last: true,
totalPages: 1,
sort: {
sorted: false,
unsorted: true,
empty: true
},
first: true,
numberOfElements: 1,
content: [
{
id: "600e0668f09f2a7d4d5c7305",
number: 76079,
effectiveDateStartAt: "2020-02-01T00:00:00",
effectiveDateEndAt: "2020-02-25T23:59:59",
vehiclePlate: "ABC-1234",
notExpired: false,
daysToExpire: -333,
daysExpired: 333,
expired: true
}
]
}
```

## Criar Apólice com parâmetros válidos

### Requisição
`POST api/policies`
```json
{
    "effectiveDateStartAt": "2020-02-01T00:00:00",
    "effectiveDateEndAt": "2020-02-25T23:59:59",
    "vehiclePlate": "ABC-1234",
    "client": {
        "id": "600c6c9db16df808354b113f"
    }
}
```
### Resposta
HTTP/1.1 201 CREATED
Status: 201 CREATED
Content-Type: application/json

## Criar Apólice com parametros inválidos

### Requisição
`POST api/policies`
{}

### Resposta
HTTP/1.1 422 UNPROCESSABLE ENTITY
Status: 422 UNPROCESSABLE ENTITY
Content-Type: application/json
```json
[
    {
        "codes": [
            "NotNull.policy.client",
            "NotNull.client",
            "NotNull.br.com.asap.api.models.Client",
            "NotNull"
        ],
        "defaultMessage": "Cliente é obrigatório",
        "objectName": "policy"
    },
    {
        "codes": [
            "NotNull.policy.effectiveDateEndAt",
            "NotNull.effectiveDateEndAt",
            "NotNull.java.time.LocalDateTime",
            "NotNull"
        ],
        "defaultMessage": "Data final de vigência é obrigatória",
        "objectName": "policy"
    },
    {
        "codes": [
            "NotEmpty.policy.vehiclePlate",
            "NotEmpty.vehiclePlate",
            "NotEmpty.java.lang.String",
            "NotEmpty"
        ],
        "defaultMessage": "Placa do veículo é oobrigatória",
        "objectName": "policy"
    },
    {
        "codes": [
            "NotNull.policy.effectiveDateStartAt",
            "NotNull.effectiveDateStartAt",
            "NotNull.java.time.LocalDateTime",
            "NotNull"
        ],
        "defaultMessage": "Data inicial de vigência é obrigatória",
        "objectName": "policy"
    }
]
```
## Requisitar uma Apólice existente

### Requisição
`GET api/policies/1`

### Resposta
HTTP/1.1 200 OK
Status: 200 OK
Content-Type: application/json
```json
{
    "id": "600e0668f09f2a7d4d5c7305",
    "number": 76079,
    "effectiveDateStartAt": "2020-02-01T00:00:00",
    "effectiveDateEndAt": "2020-02-25T23:59:59",
    "vehiclePlate": "ABC-1234",
    "notExpired": false,
    "daysToExpire": -333,
    "daysExpired": 333,
    "expired": true
}
```
## Requisitar um Apólice Não existente

### Requisição
`GET api/policies/999` 

## Requisitar um Apólice por número existente

### Requisição
`GET api/policies/number/76079`

### Resposta
HTTP/1.1 200 OK
Status: 200 OK
Content-Type: application/json
```json
{
    "id": "600e0668f09f2a7d4d5c7305",
    "number": 76079,
    "effectiveDateStartAt": "2020-02-01T00:00:00",
    "effectiveDateEndAt": "2020-02-25T23:59:59",
    "vehiclePlate": "ABC-1234",
    "notExpired": false,
    "daysToExpire": -333,
    "daysExpired": 333,
    "expired": true
} 
```
## Requisitar uma Apólice por número Não existente

### Requisição
`GET api/policies/number/9999`

### Resposta
HTTP/1.1 404 NOT FOUND
Status: 404 NOT FOUND
Content-Type: application/json

## Atualizar um Cliente com parâmetros válidos

### Requisição
`PUT api/policies/600e0668f09f2a7d4d5c7305`

### Resposta

HTTP/1.1 200 OK
Status: 200 OK
Content-Type: application/json
```json
{
    "id": "600e0668f09f2a7d4d5c7305",
    "number": 76079,
    "effectiveDateStartAt": "2020-02-01T00:00:00",
    "effectiveDateEndAt": "2020-02-25T23:59:59",
    "vehiclePlate": "ABC-1234",
    "notExpired": false,
    "daysToExpire": -333,
    "daysExpired": 333,
    "expired": true
} 
```
## Atualizar um Cliente com parâmetros inválidos

### Requisição
`PUT api/policies/1`
{}

### Resposta
HTTP/1.1 422 UNPROCESSABLE ENTITY
Status: 422 UNPROCESSABLE ENTITY
Content-Type: application/json
```json
[
    {
        "codes": [
            "NotNull.policy.client",
            "NotNull.client",
            "NotNull.br.com.asap.api.models.Client",
            "NotNull"
        ],
        "defaultMessage": "Cliente é obrigatório",
        "objectName": "policy"
    },
    {
        "codes": [
            "NotNull.policy.effectiveDateEndAt",
            "NotNull.effectiveDateEndAt",
            "NotNull.java.time.LocalDateTime",
            "NotNull"
        ],
        "defaultMessage": "Data final de vigência é obrigatória",
        "objectName": "policy"
    },
    {
        "codes": [
            "NotEmpty.policy.vehiclePlate",
            "NotEmpty.vehiclePlate",
            "NotEmpty.java.lang.String",
            "NotEmpty"
        ],
        "defaultMessage": "Placa do veículo é oobrigatória",
        "objectName": "policy"
    },
    {
        "codes": [
            "NotNull.policy.effectiveDateStartAt",
            "NotNull.effectiveDateStartAt",
            "NotNull.java.time.LocalDateTime",
            "NotNull"
        ],
        "defaultMessage": "Data inicial de vigência é obrigatória",
        "objectName": "policy"
    }
]
```

## Deletar uma apólice existente
### Requisição
`DELETE api/policies/1`

### Resposta
HTTP/1.1 200 0K
Status: 200 0K
Content-Type: application/json


## Deletar um não existente
### Requisição
`DELETE api/policies/999`

### Resposta
HTTP/1.1 404 NOT FOUND
Status: 404 NOT FOUND
Content-Type: application/json