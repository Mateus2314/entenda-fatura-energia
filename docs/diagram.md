graph TB
subgraph "Frontend - React"
A[Usuário] -->|Acessa| B[Login/Registro]
B -->|Seleciona Tipo| C{Tipo de Usuário}

        C -->|Cliente| D1[Dashboard Cliente]
        C -->|Consultor/Empresa| D2[Dashboard Consultor]
        C -->|Admin| D3[Dashboard Admin]
        
        B -->|Primeiro Acesso| E[Cadastro de Dados]
        E --> F[Dados da Fatura]
        E --> G[Dados de Engenharia]
        E --> H[Seleção de Concessionária]
        
        H -->|Consulta| I[Busca Tarifas ANEEL]
        I -->|Retorna| J[Preenche Valores Automáticos]
        
        D1 --> K[Análise de Consumo]
        D1 --> L[Comparação Fatura Atual vs Simulada]
        D1 --> M[Recomendações]
        
        D2 --> N[Gestão de Clientes]
        D2 --> O[Análises Avançadas]
        D2 --> P[Geração de Relatórios PDF]
        P --> Q[PDF com Marca da Empresa]
        
        D3 --> R[Gerenciar Usuários]
        D3 --> S[Logs e Auditoria]
        D3 --> T[Todas as Funcionalidades]
        
        L --> U[Seletor de Melhorias]
        U --> V[Recalcula Fatura]
    end
    
    subgraph "Backend - Spring Boot"
        B -->|POST /api/auth/register| W[Auth Controller]
        W --> X[User Service]
        X --> Y{Tipo de Usuário}
        
        Y -->|Cliente| Z1[Cliente Repository]
        Y -->|Consultor| Z2[Consultor Repository]
        Y -->|Admin| Z3[Admin Repository]
        
        F -->|POST /api/bills/data| AA[Bill Data Controller]
        AA --> AB[Bill Data Service]
        AB --> AC[Validação de Dados]
        
        G -->|POST /api/engineering| AD[Engineering Controller]
        AD --> AE[Engineering Service]
        
        I -->|GET /api/external/aneel| AF[External API Controller]
        AF --> AG[ANEEL Integration Service]
        AG -->|HTTP Client| AH[API ANEEL]
        
        AF -->|GET /api/external/taxes| AI[Tax Integration Service]
        AI -->|HTTP Client| AJ[APIs de Impostos]
        
        K -->|GET /api/analysis| AK[Analysis Controller]
        AK --> AL[Analysis Service]
        AL --> AM[Cálculos de Consumo]
        
        V -->|POST /api/simulation| AN[Simulation Controller]
        AN --> AO[Simulation Service]
        AO --> AP[Comparação Atual vs Simulada]
        
        P -->|POST /api/reports/pdf| AQ[Report Controller]
        AQ --> AR[PDF Generation Service]
        AR --> AS[iText PDF]
        
        N -->|GET /api/consultant/clients| AT[Consultant Controller]
        AT --> AU[Client Management Service]
        
        R -->|GET /api/admin/users| AV[Admin Controller]
        AV --> AW[User Management Service]
        
        S -->|GET /api/admin/logs| AX[Audit Controller]
        AX --> AY[Audit Service]
    end
    
    subgraph "Banco de Dados - PostgreSQL"
        Z1 --> BA[(users)]
        Z2 --> BA
        Z3 --> BA
        BA --> BB[(clients - herda users)]
        BA --> BC[(consultants - herda users)]
        BA --> BD[(admins - herda users)]
        
        AB --> BE[(bills_data)]
        AE --> BF[(engineering_data)]
        AL --> BG[(analysis_results)]
        AO --> BH[(simulations)]
        AU --> BI[(client_consultant - relação)]
        AY --> BJ[(audit_logs)]
        
        BK[(countries)]
        BL[(utility_companies)]
        BM[(tariff_cache)]
    end
    
    subgraph "APIs Externas"
        AH[ANEEL Tarifas API]
        AJ[API Impostos - Serpro/RFB]
        BN[API Cambio - Futuro]
        BO[API Internacional - Futuro]
    end
    
    subgraph "Testes"
        BP[Robot Framework] -.->|E2E Tests| A
        BQ[Jest + RTL] -.->|Unit Tests| D1
        BR[JUnit 5] -.->|Unit Tests| AA
        BS[Testcontainers] -.->|Integration Tests| BA
        BT[WireMock] -.->|Mock APIs| AG
    end
    
    subgraph "Internacionalização"
        BU[i18n Service] --> BV[pt-BR]
        BU --> BW[en-US - Futuro]
        BU --> BX[es-ES - Futuro]
    end
    
    style A fill:#e1f5ff
    style BA fill:#fff4e1
    style AH fill:#e8f5e9
    style BU fill:#fff3e0
