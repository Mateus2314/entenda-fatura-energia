# Frontend - Entenda sua Fatura de Energia

Frontend em React para o sistema de análise de faturas de energia elétrica.

## 🚀 Tecnologias Utilizadas

- **React 18.2.0** - Biblioteca JavaScript para construção de interfaces
- **React Router DOM 6.20.0** - Roteamento para aplicações React
- **Axios** - Cliente HTTP para comunicação com a API
- **Tailwind CSS 4.x** - Framework CSS utilitário
- **React Scripts 5.0.1** - Scripts e configuração do Create React App

## 📁 Estrutura do Projeto

```
frontend/
├── public/              # Arquivos públicos estáticos
├── src/
│   ├── services/        # Serviços de API
│   │   └── api.js      # Configuração do Axios
│   ├── App.js          # Componente principal
│   ├── App.css         # Estilos do App
│   ├── index.js        # Ponto de entrada
│   └── index.css       # Estilos globais (Tailwind)
├── .env                # Variáveis de ambiente (desenvolvimento)
├── .env.production     # Variáveis de ambiente (produção)
├── Dockerfile          # Configuração Docker
├── package.json        # Dependências do projeto
└── tailwind.config.js  # Configuração do Tailwind CSS
```

## 🔧 Configuração e Instalação

### Pré-requisitos

- Node.js 18+ 
- npm ou yarn

### Instalação

1. Entre no diretório do frontend:
```bash
cd frontend
```

2. Instale as dependências:
```bash
npm install
```

## 🏃 Executando o Projeto

### Modo Desenvolvimento

```bash
npm start
```

O aplicativo estará disponível em: `http://localhost:3000`

### Build para Produção

```bash
npm build
```

Os arquivos otimizados serão gerados na pasta `build/`.

### Testes

```bash
npm test
```

## 🐳 Docker

### Build da imagem

```bash
docker build -t energia-frontend .
```

### Executar container

```bash
docker run -p 3000:3000 energia-frontend
```

### Com Docker Compose (recomendado)

Na raiz do projeto:

```bash
docker-compose up
```

## 🌐 Variáveis de Ambiente

### Desenvolvimento (.env)
```
REACT_APP_API_URL=http://localhost:8080/api
```

### Produção (.env.production)
```
REACT_APP_API_URL=http://backend:8080/api
```

## 📡 Integração com Backend

O frontend se comunica com o backend através do serviço configurado em `src/services/api.js`:

- URL base configurável via variável de ambiente `REACT_APP_API_URL`
- Interceptors para autenticação automática (Bearer token)
- Tratamento de erros HTTP (401, etc.)

## 🎨 Tailwind CSS

O Tailwind CSS está configurado para processar todos os arquivos em `src/`:

- Classes utilitárias disponíveis em todo o projeto
- Configuração em `tailwind.config.js`
- PostCSS configurado para autoprefixer

## 📝 Scripts Disponíveis

- `npm start` - Inicia o servidor de desenvolvimento
- `npm build` - Cria build de produção
- `npm test` - Executa os testes
- `npm eject` - Ejeta a configuração do Create React App (irreversível)

## 🔐 Autenticação

O sistema está preparado para usar autenticação JWT:
- Token armazenado no localStorage
- Interceptor automático que adiciona token nas requisições
- Redirecionamento para login em caso de token inválido/expirado

## 📦 Próximos Passos

1. ✅ Configuração base do React
2. ✅ Integração com Tailwind CSS
3. ✅ Serviço de API configurado
4. ✅ Docker configurado
5. ⏳ Implementar componentes de upload de faturas
6. ⏳ Implementar visualização de análises
7. ⏳ Implementar autenticação de usuários
8. ⏳ Implementar dashboard de estatísticas

## 🤝 Contribuindo

1. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
2. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
3. Push para a branch (`git push origin feature/AmazingFeature`)
4. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT.

