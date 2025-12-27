# 📚 Service Layer - Gerenciamento de Associações Consultant-Client

## 🎯 O que é o Service de Gerenciamento de Associações?

O **Service Layer** (Camada de Serviço) é responsável por implementar a **lógica de negócio** da aplicação. No caso das associações entre consultores e clientes, o service gerencia operações como:

- ✅ Atribuir um consultor a um cliente
- ✅ Remover a associação entre consultor e cliente
- ✅ Listar clientes de um consultor
- ✅ Verificar se um consultor tem acesso aos dados de um cliente
- ✅ Gerenciar status das associações (ACTIVE, INACTIVE, PENDING)
- ✅ Implementar regras de negócio (ex: um cliente pode ter múltiplos consultores?)

---

## 🏗️ Arquitetura em Camadas

```
┌─────────────────────────────────────┐
│     Controller Layer (REST API)     │  ← Recebe requisições HTTP
│   @RestController                   │
│   - POST /consultants/{id}/clients  │
│   - DELETE /consultants/{id}/clients│
│   - GET /consultants/{id}/clients   │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│       Service Layer (Business)      │  ← Lógica de Negócio
│   @Service                          │
│   - assignClientToConsultant()      │
│   - removeClientFromConsultant()    │
│   - getConsultantClients()          │
│   - checkAccess()                   │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│    Repository Layer (Data Access)   │  ← Acesso ao Banco
│   @Repository                       │
│   - ConsultantRepository            │
│   - ClientRepository                │
│   - (Optional: ConsultantClientRepo)│
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│         Database (PostgreSQL)       │
│   - consultant_clients table        │
└─────────────────────────────────────┘
```

---

## 📝 Exemplo Prático: ConsultantService

### 1. Interface do Service

```java
public interface ConsultantService {
    
    /**
     * Atribui um cliente a um consultor
     * @param consultantId ID do consultor
     * @param clientId ID do cliente
     * @return Associação criada
     */
    void assignClientToConsultant(UUID consultantId, UUID clientId);
    
    /**
     * Remove associação entre consultor e cliente
     * @param consultantId ID do consultor
     * @param clientId ID do cliente
     */
    void removeClientFromConsultant(UUID consultantId, UUID clientId);
    
    /**
     * Lista todos os clientes ativos de um consultor
     * @param consultantId ID do consultor
     * @return Lista de clientes
     */
    List<Client> getActiveClients(UUID consultantId);
    
    /**
     * Verifica se o consultor tem acesso aos dados do cliente
     * @param consultantId ID do consultor
     * @param clientId ID do cliente
     * @return true se tem acesso
     */
    boolean hasAccessToClient(UUID consultantId, UUID clientId);
    
    /**
     * Muda status da associação
     * @param consultantId ID do consultor
     * @param clientId ID do cliente
     * @param status Novo status (ACTIVE, INACTIVE, PENDING)
     */
    void updateAssociationStatus(UUID consultantId, UUID clientId, String status);
}
```

### 2. Implementação do Service

```java
@Service
@Transactional
public class ConsultantServiceImpl implements ConsultantService {
    
    private final ConsultantRepository consultantRepository;
    private final ClientRepository clientRepository;
    
    @Autowired
    public ConsultantServiceImpl(
        ConsultantRepository consultantRepository,
        ClientRepository clientRepository
    ) {
        this.consultantRepository = consultantRepository;
        this.clientRepository = clientRepository;
    }
    
    @Override
    public void assignClientToConsultant(UUID consultantId, UUID clientId) {
        // 1. Buscar consultor e cliente
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new ResourceNotFoundException("Consultant not found"));
        
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        
        // 2. Verificar se já existe associação
        if (consultant.getManagedClients().contains(client)) {
            throw new BusinessException("Client already assigned to this consultant");
        }
        
        // 3. Adicionar cliente ao consultor (método helper mantém bidirecionalidade)
        consultant.addManagedClient(client);
        
        // 4. Salvar (JPA gerencia a tabela join automaticamente)
        consultantRepository.save(consultant);
        
        // 5. Log ou notificação
        log.info("Client {} assigned to Consultant {}", clientId, consultantId);
    }
    
    @Override
    public void removeClientFromConsultant(UUID consultantId, UUID clientId) {
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new ResourceNotFoundException("Consultant not found"));
        
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        
        // Remove a associação (método helper mantém bidirecionalidade)
        consultant.removeManagedClient(client);
        
        consultantRepository.save(consultant);
        
        log.info("Client {} removed from Consultant {}", clientId, consultantId);
    }
    
    @Override
    public List<Client> getActiveClients(UUID consultantId) {
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new ResourceNotFoundException("Consultant not found"));
        
        // Retorna todos os clientes gerenciados
        return new ArrayList<>(consultant.getManagedClients());
    }
    
    @Override
    public boolean hasAccessToClient(UUID consultantId, UUID clientId) {
        // Opção 1: Usando JPA
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElse(null);
        
        if (consultant == null) return false;
        
        return consultant.getManagedClients().stream()
            .anyMatch(client -> client.getId().equals(clientId));
        
        // Opção 2: Usando query nativa (mais eficiente)
        // return consultantRepository.existsAssociationWithClient(consultantId, clientId);
    }
    
    @Override
    public void updateAssociationStatus(UUID consultantId, UUID clientId, String status) {
        // Para gerenciar status, você precisaria de um Repository específico
        // para a tabela consultant_clients
        // Ou usar query nativa
        
        // Exemplo com query nativa:
        // consultantClientRepository.updateStatus(consultantId, clientId, status);
    }
}
```

---

## 🎯 Por que usar Service Layer?

### ✅ **Vantagens:**

1. **Separação de Responsabilidades**
   - Controller: recebe requisições HTTP
   - Service: lógica de negócio
   - Repository: acesso ao banco

2. **Reutilização de Código**
   - Mesmo método pode ser chamado de diferentes controllers
   - Evita duplicação de lógica

3. **Testabilidade**
   - Mais fácil criar testes unitários
   - Pode mockar dependências

4. **Transações**
   - `@Transactional` garante consistência
   - Rollback automático em caso de erro

5. **Validações de Negócio**
   - Verifica regras antes de salvar
   - Lança exceções customizadas

---

## 📋 Casos de Uso Práticos

### Caso 1: API para Atribuir Cliente a Consultor

**Controller:**
```java
@RestController
@RequestMapping("/api/consultants")
public class ConsultantController {
    
    @Autowired
    private ConsultantService consultantService;
    
    @PostMapping("/{consultantId}/clients/{clientId}")
    public ResponseEntity<String> assignClient(
        @PathVariable UUID consultantId,
        @PathVariable UUID clientId
    ) {
        consultantService.assignClientToConsultant(consultantId, clientId);
        return ResponseEntity.ok("Client assigned successfully");
    }
}
```

**Requisição:**
```http
POST /api/consultants/123e4567-e89b-12d3-a456-426614174000/clients/987fbc9-4bed-5078-af07-9141ba07c9f3
```

**O que acontece:**
1. Controller recebe a requisição
2. Chama o Service
3. Service busca consultor e cliente
4. Service valida se pode associar
5. Service adiciona usando método helper da entity
6. JPA insere registro na tabela `consultant_clients`
7. Retorna sucesso

---

### Caso 2: Controle de Acesso para Ver Faturas

**Service:**
```java
@Service
public class ElectricityBillService {
    
    @Autowired
    private ConsultantService consultantService;
    
    public ElectricityBillDTO getBill(UUID billId, UUID consultantId) {
        // 1. Busca a fatura
        ElectricityBill bill = billRepository.findById(billId)
            .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));
        
        // 2. VERIFICA SE O CONSULTOR TEM ACESSO AO CLIENTE
        boolean hasAccess = consultantService.hasAccessToClient(
            consultantId, 
            bill.getClient().getId()
        );
        
        if (!hasAccess) {
            throw new ForbiddenException("You don't have access to this client's bills");
        }
        
        // 3. Retorna a fatura
        return billMapper.toDTO(bill);
    }
}
```

---

### Caso 3: Dashboard do Consultor

**Service:**
```java
@Service
public class ConsultantDashboardService {
    
    @Autowired
    private ConsultantService consultantService;
    
    @Autowired
    private ElectricityBillRepository billRepository;
    
    public DashboardDTO getConsultantDashboard(UUID consultantId) {
        // 1. Busca todos os clientes ativos
        List<Client> clients = consultantService.getActiveClients(consultantId);
        
        // 2. Conta total de clientes
        int totalClients = clients.size();
        
        // 3. Busca total de faturas dos clientes
        int totalBills = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        
        for (Client client : clients) {
            List<ElectricityBill> bills = billRepository.findByClientId(client.getId());
            totalBills += bills.size();
            totalRevenue = totalRevenue.add(
                bills.stream()
                    .map(ElectricityBill::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
            );
        }
        
        // 4. Monta o DTO
        return DashboardDTO.builder()
            .totalClients(totalClients)
            .totalBills(totalBills)
            .totalRevenue(totalRevenue)
            .clients(clients)
            .build();
    }
}
```

---

## 🔧 Repository Opcional: ConsultantClientRepository

Se você precisar fazer queries mais complexas na tabela `consultant_clients`, pode criar um repository específico:

```java
@Repository
public interface ConsultantClientRepository extends JpaRepository<ConsultantClient, ConsultantClientId> {
    
    @Query("SELECT cc FROM ConsultantClient cc WHERE cc.consultantId = :consultantId AND cc.status = 'ACTIVE'")
    List<ConsultantClient> findActiveByConsultantId(@Param("consultantId") UUID consultantId);
    
    @Query("SELECT COUNT(cc) > 0 FROM ConsultantClient cc WHERE cc.consultantId = :consultantId AND cc.clientId = :clientId AND cc.status = 'ACTIVE'")
    boolean existsActiveAssociation(@Param("consultantId") UUID consultantId, @Param("clientId") UUID clientId);
    
    @Modifying
    @Query("UPDATE ConsultantClient cc SET cc.status = :status WHERE cc.consultantId = :consultantId AND cc.clientId = :clientId")
    void updateStatus(@Param("consultantId") UUID consultantId, @Param("clientId") UUID clientId, @Param("status") String status);
}
```

**Nota:** Para isso, você precisaria criar uma entity `ConsultantClient` (opcional, já que JPA gerencia join tables automaticamente).

---

## 📊 Resumo

| Camada | Responsabilidade | Exemplo |
|--------|-----------------|---------|
| **Controller** | Receber requisições HTTP | `@PostMapping("/consultants/{id}/clients")` |
| **Service** | Lógica de negócio | `assignClientToConsultant()` |
| **Repository** | Acesso ao banco | `consultantRepository.findById()` |
| **Entity** | Modelo de dados | `Consultant.addManagedClient()` |

### Fluxo Completo:
1. **Cliente HTTP** faz requisição → Controller
2. **Controller** chama → Service
3. **Service** valida regras de negócio
4. **Service** chama → Repository
5. **Repository** executa → Query SQL
6. **JPA** gerencia → Tabela `consultant_clients`
7. **Service** retorna → Controller
8. **Controller** retorna → Resposta HTTP

---

## 🚀 Próximos Passos

1. ✅ Entities atualizadas com @ManyToMany
2. ✅ ER_DIAGRAM.md atualizado
3. 📝 Criar `ConsultantService` e `ConsultantServiceImpl`
4. 📝 Criar endpoints REST no `ConsultantController`
5. 📝 Implementar controle de acesso nas queries de faturas
6. 📝 Criar testes unitários para o service

---

**Documento criado em:** 2025-12-27  
**Versão:** 1.0

