# Padrões de Repositório no Projeto

**Data:** 2025-12-17  
**Status:** ✅ Definido

---

## 🎯 Padrão Adotado

Utilizaremos **`JpaRepository`** como interface base para todos os repositórios do projeto.

---

## 📊 Hierarquia de Interfaces Spring Data JPA

```
Repository (marker interface)
    ↓
CrudRepository (operações CRUD básicas)
    ↓
PagingAndSortingRepository (+ paginação e ordenação)
    ↓
JpaRepository (+ batch operations e flush)
```

---

## ✅ Por que JpaRepository?

| Funcionalidade | Disponível |
|----------------|------------|
| Operações CRUD | ✅ |
| Paginação e Ordenação | ✅ |
| Batch operations (`deleteAllInBatch`, `saveAllAndFlush`) | ✅ |
| Retorna `List<T>` ao invés de `Iterable<T>` | ✅ |
| Método `flush()` para sincronização imediata | ✅ |

**Conclusão:** `JpaRepository` oferece o conjunto mais completo de funcionalidades prontas para uso.

---

## 🏗️ Estrutura Padrão de Repository

```java
package com.understand_your_electricity_bill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    
    // Query Methods (derivados automaticamente)
    Optional<Client> findByCpf(String cpf);
    
    // Consultas customizadas com @Query (quando necessário)
    @Query("SELECT c FROM Client c WHERE c.status = :status")
    List<Client> findByStatus(@Param("status") UserStatus status);
}
```

---

## 📋 Convenções de Nomenclatura

### Query Methods (Derivação Automática)

| Padrão | Exemplo | SQL Gerado |
|--------|---------|------------|
| `findBy<Campo>` | `findByCpf(String cpf)` | `WHERE cpf = ?` |
| `findBy<Campo1>And<Campo2>` | `findByNameAndEmail(...)` | `WHERE name = ? AND email = ?` |
| `findBy<Campo>Containing` | `findByNameContaining(String name)` | `WHERE name LIKE %?%` |
| `findBy<Campo>OrderBy<Campo2>` | `findByStatusOrderByCreatedAtDesc(...)` | `WHERE status = ? ORDER BY created_at DESC` |

### Consultas Customizadas (@Query)

Use apenas quando:
- A consulta é complexa demais para derivação automática
- Precisa de `JOIN`s específicos
- Requer funcionalidades nativas do PostgreSQL (ex: `JSONB`)

```java
@Query(value = "SELECT * FROM admins WHERE permissions ->> 'can_delete' = 'true'", 
       nativeQuery = true)
List<Admin> findAdminsWithDeletePermission();
```

---

## 📦 Repositórios do Projeto

| Entidade | Repository | Chave Primária |
|----------|------------|----------------|
| `User` | `UserRepository` | `UUID` |
| `Client` | `ClientRepository` | `UUID` |
| `Consultant` | `ConsultantRepository` | `UUID` |
| `Admin` | `AdminRepository` | `UUID` |

---

## 🔍 Exemplos de Query Methods Comuns

### ClientRepository
```java
Optional<Client> findByCpf(String cpf);
boolean existsByCpf(String cpf);
List<Client> findByRegistrationDateAfter(LocalDate date);
```

### ConsultantRepository
```java
Optional<Consultant> findByCnpj(String cnpj);
List<Consultant> findByCompany(String company);
boolean existsByCnpj(String cnpj);
```

### AdminRepository
```java
List<Admin> findByRole(String role);
Optional<Admin> findByEmail(String email);
```

### UserRepository (Base)
```java
Optional<User> findByEmail(String email);
List<User> findByStatus(UserStatus status);
List<User> findByUserType(UserType userType);
boolean existsByEmail(String email);
```

---

## 🚀 Próximos Passos

1. ✅ Criar `UserRepository` 
2. ✅ Criar `ClientRepository` com método `findByCpf(String cpf)`
3. ✅ Criar `ConsultantRepository` com método `findByCnpj(String cnpj)`
4. ✅ Criar `AdminRepository` com método `findByRole(String role)`
5. ⏳ Implementar Services que utilizam os repositories

---

**Padrão estabelecido e pronto para implementação!** ✅

