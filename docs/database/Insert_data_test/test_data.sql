-- ============================================================================
-- COMPLETE TEST DATA - ALL MIGRATIONS
-- Execute after all migrations (V001-V009) are applied
-- All UUIDs use only hexadecimal characters (0-9, a-f)
-- Author: Backend Team
-- Date: 2025-12-29
-- Database: PostgreSQL 17.7
-- ============================================================================

-- ============================================================================
-- V001: USERS TABLE
-- 15 users: 5 clients + 5 consultants + 5 admins
-- ============================================================================

-- CLIENT USERS
INSERT INTO users (id, email, password_hash, user_type, name, phone, status, created_at, updated_at) VALUES
('11111111-1111-1111-1111-111111111111', 'client1@example.com', '$2a$10$dummyHashForTestingPassword123!', 'CLIENT', 'João Silva Santos', '+5511987654321', 'ACTIVE', NOW(), NOW()),
('22222222-2222-2222-2222-222222222222', 'client2@example.com', '$2a$10$dummyHashForTestingPassword123!', 'CLIENT', 'Maria Oliveira Costa', '+5521987654322', 'ACTIVE', NOW(), NOW()),
('33333333-3333-3333-3333-333333333333', 'client3@example.com', '$2a$10$dummyHashForTestingPassword123!', 'CLIENT', 'Pedro Henrique Souza', '+5531987654323', 'PENDING_VERIFICATION', NOW(), NOW()),
('44444444-4444-4444-4444-444444444444', 'client4@example.com', '$2a$10$dummyHashForTestingPassword123!', 'CLIENT', 'Ana Paula Fernandes', '+5541987654324', 'ACTIVE', NOW(), NOW()),
('55555555-5555-5555-5555-555555555555', 'client5@example.com', '$2a$10$dummyHashForTestingPassword123!', 'CLIENT', 'Carlos Eduardo Lima', '+5551987654325', 'INACTIVE', NOW(), NOW());

-- CONSULTANT USERS
INSERT INTO users (id, email, password_hash, user_type, name, phone, status, created_at, updated_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'consultant1@energia.com', '$2a$10$dummyHashForTestingPassword123!', 'CONSULTANT', 'Consultoria Energia SP', '+5511912345678', 'ACTIVE', NOW(), NOW()),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'consultant2@energia.com', '$2a$10$dummyHashForTestingPassword123!', 'CONSULTANT', 'Eficiência Total RJ', '+5521912345679', 'ACTIVE', NOW(), NOW()),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'consultant3@energia.com', '$2a$10$dummyHashForTestingPassword123!', 'CONSULTANT', 'Green Energy MG', '+5531912345680', 'ACTIVE', NOW(), NOW()),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'consultant4@energia.com', '$2a$10$dummyHashForTestingPassword123!', 'CONSULTANT', 'Eco Power RS', '+5551912345681', 'ACTIVE', NOW(), NOW()),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'consultant5@energia.com', '$2a$10$dummyHashForTestingPassword123!', 'CONSULTANT', 'Smart Energy PR', '+5541912345682', 'SUSPENDED', NOW(), NOW());

-- ADMIN USERS
INSERT INTO users (id, email, password_hash, user_type, name, phone, status, created_at, updated_at) VALUES
('f0000000-0000-0000-0000-000000000001', 'admin1@system.com', '$2a$10$dummyHashForTestingPassword123!', 'ADMIN', 'Administrator Master', '+5511999999991', 'ACTIVE', NOW(), NOW()),
('f0000000-0000-0000-0000-000000000002', 'admin2@system.com', '$2a$10$dummyHashForTestingPassword123!', 'ADMIN', 'Administrator Support', '+5511999999992', 'ACTIVE', NOW(), NOW()),
('f0000000-0000-0000-0000-000000000003', 'admin3@system.com', '$2a$10$dummyHashForTestingPassword123!', 'ADMIN', 'Administrator Finance', '+5511999999993', 'ACTIVE', NOW(), NOW()),
('f0000000-0000-0000-0000-000000000004', 'admin4@system.com', '$2a$10$dummyHashForTestingPassword123!', 'ADMIN', 'Administrator Tech', '+5511999999994', 'ACTIVE', NOW(), NOW()),
('f0000000-0000-0000-0000-000000000005', 'admin5@system.com', '$2a$10$dummyHashForTestingPassword123!', 'ADMIN', 'Administrator Reports', '+5511999999995', 'ACTIVE', NOW(), NOW());

-- ============================================================================
-- V002: CLIENTS TABLE
-- 5 clients with complete address information
-- ============================================================================

INSERT INTO clients (user_id, address, city, state, zip_code, cpf, registration_date) VALUES
('11111111-1111-1111-1111-111111111111', 'Rua das Flores, 123, Apt 45', 'São Paulo', 'SP', '01234-567', '12345678901', '2024-01-15'),
('22222222-2222-2222-2222-222222222222', 'Av. Atlântica, 456', 'Rio de Janeiro', 'RJ', '20000-000', '23456789012', '2024-02-20'),
('33333333-3333-3333-3333-333333333333', 'Rua da Paz, 789', 'Belo Horizonte', 'MG', '30000000', '34567890123', '2024-03-10'),
('44444444-4444-4444-4444-444444444444', 'Av. Paulista, 1000, Sala 12', 'São Paulo', 'SP', '01310-100', '45678901234', '2024-04-05'),
('55555555-5555-5555-5555-555555555555', 'Rua XV de Novembro, 321', 'Curitiba', 'PR', '80000-000', '56789012345', '2024-05-22');

-- ============================================================================
-- V003: CONSULTANTS TABLE
-- 5 consultants with company information
-- ============================================================================

INSERT INTO consultants (user_id, consultant_name, company, cnpj, registration_number, address, city, state, zip_code, company_logo, registration_date) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Roberto Carlos Pereira', 'Consultoria Energia SP Ltda', '12345678000190', 'CREA-SP 123456', 'Av. Brigadeiro Faria Lima, 1000', 'São Paulo', 'SP', '01452-000', NULL, '2023-06-01'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Fernanda Lima Costa', 'Eficiência Total RJ S.A.', '23456789000191', 'CREA-RJ 234567', 'Rua Visconde de Pirajá, 500', 'Rio de Janeiro', 'RJ', '22410-000', NULL, '2023-07-15'),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Marcos Antonio Silva', 'Green Energy MG Ltda', '34567890000192', 'CREA-MG 345678', 'Av. Afonso Pena, 800', 'Belo Horizonte', 'MG', '30130-000', NULL, '2023-08-20'),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Juliana Rodrigues', 'Eco Power RS Ltda', '45678901000193', 'CREA-RS 456789', 'Rua dos Andradas, 1200', 'Porto Alegre', 'RS', '90020-000', NULL, '2023-09-10'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Ricardo Mendes', 'Smart Energy PR Ltda', '56789012000194', 'CREA-PR 567890', 'Rua Marechal Deodoro, 300', 'Curitiba', 'PR', '80010-000', NULL, '2023-10-05');

-- ============================================================================
-- V004: ADMINS TABLE
-- 5 admins with different roles and permissions
-- ============================================================================

INSERT INTO admins (user_id, role, permissions) VALUES
('f0000000-0000-0000-0000-000000000001', 'SUPER_ADMIN', '{"all": true, "users": "full", "bills": "full", "reports": "full", "system": "full"}'),
('f0000000-0000-0000-0000-000000000002', 'SUPPORT_ADMIN', '{"users": "read-write", "bills": "read", "reports": "read"}'),
('f0000000-0000-0000-0000-000000000003', 'FINANCE_ADMIN', '{"bills": "full", "reports": "full", "payments": "full"}'),
('f0000000-0000-0000-0000-000000000004', 'TECH_ADMIN', '{"system": "full", "logs": "full", "monitoring": "full"}'),
('f0000000-0000-0000-0000-000000000005', 'REPORTS_ADMIN', '{"reports": "full", "analytics": "full", "exports": "full"}');

-- ============================================================================
-- V005: TARIFFS TABLE
-- 5 tariffs from different distributors (ANEEL-based data)
-- ============================================================================

INSERT INTO tariffs (
    id, generation_date, description_reh, distributor, cnpj_distributor,
    valid_from, valid_until, tariff_base_desc, subgroup, tariff_modality,
    consumer_class, consumer_subclass, detail, tariff_post_name, tertiary_unit,
    accessing_agent, tusd_value, te_value, flag_generation_date, competence_date,
    activated_flag_name, flag_additional_value
) VALUES
('10000000-0000-0000-0000-000000000001', '2024-12-01', 'RESOLUÇÃO HOMOLOGATÓRIA Nº 3.500/2024', 'CPFL PAULISTA', '02198431000104',
 '2024-12-01', NULL, 'Tarifa de Aplicação', 'B1', 'Convencional', 'Residencial', 'Residencial', 'Normal',
 'Não se aplica', 'kWh', 'Não se aplica', 0.3450, 0.4250, '2024-12-01', '2024-12-01', 'Verde', 0.0000),

('20000000-0000-0000-0000-000000000002', '2024-11-15', 'RESOLUÇÃO HOMOLOGATÓRIA Nº 3.495/2024', 'LIGHT', '60444437000171',
 '2024-11-15', NULL, 'Tarifa de Aplicação', 'B1', 'Convencional', 'Residencial', 'Residencial', 'Normal',
 'Não se aplica', 'kWh', 'Não se aplica', 0.3680, 0.4520, '2024-11-15', '2024-11-01', 'Amarela', 0.0178),

('30000000-0000-0000-0000-000000000003', '2024-10-20', 'RESOLUÇÃO HOMOLOGATÓRIA Nº 3.480/2024', 'CEMIG', '06981180000116',
 '2024-10-20', NULL, 'Tarifa de Aplicação', 'B3', 'Convencional', 'Comercial', 'Comercial', 'Normal',
 'Não se aplica', 'kWh', 'Não se aplica', 0.3890, 0.4780, '2024-10-20', '2024-10-01', 'Vermelha P1', 0.0432),

('40000000-0000-0000-0000-000000000004', '2024-09-10', 'RESOLUÇÃO HOMOLOGATÓRIA Nº 3.465/2024', 'COPEL', '04831376000141',
 '2024-09-10', NULL, 'Tarifa de Aplicação', 'A4', 'Verde', 'Industrial', 'Industrial', 'APE',
 'Fora ponta', 'kW', 'Não se aplica', 0.4120, 0.3950, '2024-09-10', '2024-09-01', 'Verde', 0.0000),

('50000000-0000-0000-0000-000000000005', '2024-08-05', 'RESOLUÇÃO HOMOLOGATÓRIA Nº 3.450/2024', 'RGE SUL', '02016440000162',
 '2024-08-05', NULL, 'Tarifa de Aplicação', 'B1', 'Convencional', 'Residencial', 'Residencial Baixa Renda', 'Baixa Renda',
 'Não se aplica', 'kWh', 'Não se aplica', 0.2890, 0.3650, '2024-08-05', '2024-08-01', 'Amarela', 0.0178);

-- ============================================================================
-- V006: ELECTRICITY_BILLS TABLE
-- 7 bills across multiple months and clients
-- ============================================================================

INSERT INTO electricity_bills (
    id, client_id, consultant_id, tariff_id, reference_month, due_date,
    total_amount, consumption_kwh, pdf_url, installation_number, invoice_number
) VALUES
('b0000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 '10000000-0000-0000-0000-000000000001', '2024-11-01', '2024-11-20', 385.50, 350.00,
 '/bills/2024/11/client1_nov.pdf', '1234567890', 'CPFL-202411-001'),

('b0000001-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
 '10000000-0000-0000-0000-000000000001', '2024-12-01', '2024-12-20', 425.80, 410.00,
 '/bills/2024/12/client1_dec.pdf', '1234567890', 'CPFL-202412-001'),

('b0000002-0000-0000-0000-000000000001', '22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
 '20000000-0000-0000-0000-000000000002', '2024-12-01', '2024-12-15', 520.30, 480.00,
 '/bills/2024/12/client2_dec.pdf', '9876543210', 'LIGHT-202412-002'),

('b0000003-0000-0000-0000-000000000001', '33333333-3333-3333-3333-333333333333', NULL,
 '30000000-0000-0000-0000-000000000003', '2024-11-01', '2024-11-18', 680.75, 550.00,
 '/bills/2024/11/client3_nov.pdf', '5555555555', 'CEMIG-202411-003'),

('b0000004-0000-0000-0000-000000000001', '44444444-4444-4444-4444-444444444444', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
 '10000000-0000-0000-0000-000000000001', '2024-10-01', '2024-10-20', 295.40, 280.00,
 '/bills/2024/10/client4_oct.pdf', '7777777777', 'CPFL-202410-004'),

('b0000004-0000-0000-0000-000000000002', '44444444-4444-4444-4444-444444444444', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
 '10000000-0000-0000-0000-000000000001', '2024-11-01', '2024-11-20', 310.20, 295.00,
 '/bills/2024/11/client4_nov.pdf', '7777777777', 'CPFL-202411-004'),

('b0000005-0000-0000-0000-000000000001', '55555555-5555-5555-5555-555555555555', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
 '50000000-0000-0000-0000-000000000005', '2024-12-01', '2024-12-22', 185.90, 220.00,
 '/bills/2024/12/client5_dec.pdf', '8888888888', 'RGE-202412-005');

-- ============================================================================
-- V007: BILL_ITEMS TABLE
-- 37 items across 7 bills with various types
-- ============================================================================

-- Bill 1 items (5)
INSERT INTO bill_items (id, bill_id, item_type, description, quantity, unit_price, amount) VALUES
('10000001-0001-0000-0000-000000000001', 'b0000001-0000-0000-0000-000000000001', 'CONSUMPTION_STANDARD', 'Consumo de energia - 350 kWh', 350.00, 0.7700, 269.50),
('10000001-0001-0000-0000-000000000002', 'b0000001-0000-0000-0000-000000000001', 'TARIFF_FLAG', 'Bandeira tarifária - Verde', 350.00, 0.0000, 0.00),
('10000001-0001-0000-0000-000000000003', 'b0000001-0000-0000-0000-000000000001', 'PUBLIC_LIGHTING', 'Contribuição de iluminação pública', NULL, NULL, 35.00),
('10000001-0001-0000-0000-000000000004', 'b0000001-0000-0000-0000-000000000001', 'TAXES', 'ICMS (18%)', NULL, NULL, 48.51),
('10000001-0001-0000-0000-000000000005', 'b0000001-0000-0000-0000-000000000001', 'TAXES', 'PIS/COFINS (9.25%)', NULL, NULL, 32.49);

-- Bill 2 items (5)
INSERT INTO bill_items (id, bill_id, item_type, description, quantity, unit_price, amount) VALUES
('10000001-0002-0000-0000-000000000001', 'b0000001-0000-0000-0000-000000000002', 'CONSUMPTION_STANDARD', 'Consumo de energia - 410 kWh', 410.00, 0.7700, 315.70),
('10000001-0002-0000-0000-000000000002', 'b0000001-0000-0000-0000-000000000002', 'TARIFF_FLAG', 'Bandeira tarifária - Verde', 410.00, 0.0000, 0.00),
('10000001-0002-0000-0000-000000000003', 'b0000001-0000-0000-0000-000000000002', 'PUBLIC_LIGHTING', 'Contribuição de iluminação pública', NULL, NULL, 35.00),
('10000001-0002-0000-0000-000000000004', 'b0000001-0000-0000-0000-000000000002', 'TAXES', 'ICMS (18%)', NULL, NULL, 56.83),
('10000001-0002-0000-0000-000000000005', 'b0000001-0000-0000-0000-000000000002', 'TAXES', 'PIS/COFINS (9.25%)', NULL, NULL, 18.27);

-- Bill 3 items (6)
INSERT INTO bill_items (id, bill_id, item_type, description, quantity, unit_price, amount) VALUES
('10000002-0001-0000-0000-000000000001', 'b0000002-0000-0000-0000-000000000001', 'CONSUMPTION_STANDARD', 'Consumo de energia - 480 kWh', 480.00, 0.8200, 393.60),
('10000002-0001-0000-0000-000000000002', 'b0000002-0000-0000-0000-000000000001', 'TARIFF_FLAG', 'Bandeira tarifária - Amarela', 480.00, 0.0178, 8.54),
('10000002-0001-0000-0000-000000000003', 'b0000002-0000-0000-0000-000000000001', 'PUBLIC_LIGHTING', 'Contribuição de iluminação pública', NULL, NULL, 40.00),
('10000002-0001-0000-0000-000000000004', 'b0000002-0000-0000-0000-000000000001', 'TAXES', 'ICMS (19%)', NULL, NULL, 72.48),
('10000002-0001-0000-0000-000000000005', 'b0000002-0000-0000-0000-000000000001', 'TAXES', 'PIS/COFINS (9.25%)', NULL, NULL, 36.41),
('10000002-0001-0000-0000-000000000006', 'b0000002-0000-0000-0000-000000000001', 'OTHER', 'Taxa de disponibilidade', NULL, NULL, 5.27);

-- Bill 4 items (7) - Commercial
INSERT INTO bill_items (id, bill_id, item_type, description, quantity, unit_price, amount) VALUES
('10000003-0001-0000-0000-000000000001', 'b0000003-0000-0000-0000-000000000001', 'CONSUMPTION_OFF_PEAK', 'Consumo fora ponta - 420 kWh', 420.00, 0.8670, 364.14),
('10000003-0001-0000-0000-000000000002', 'b0000003-0000-0000-0000-000000000001', 'CONSUMPTION_PEAK', 'Consumo ponta - 130 kWh', 130.00, 1.2500, 162.50),
('10000003-0001-0000-0000-000000000003', 'b0000003-0000-0000-0000-000000000001', 'DEMAND', 'Demanda contratada', 50.00, 18.50, 92.50),
('10000003-0001-0000-0000-000000000004', 'b0000003-0000-0000-0000-000000000001', 'TARIFF_FLAG', 'Bandeira tarifária - Vermelha P1', 550.00, 0.0432, 23.76),
('10000003-0001-0000-0000-000000000005', 'b0000003-0000-0000-0000-000000000001', 'PUBLIC_LIGHTING', 'Contribuição de iluminação pública', NULL, NULL, 45.00),
('10000003-0001-0000-0000-000000000006', 'b0000003-0000-0000-0000-000000000001', 'TAXES', 'ICMS (18%)', NULL, NULL, 122.54),
('10000003-0001-0000-0000-000000000007', 'b0000003-0000-0000-0000-000000000001', 'TAXES', 'PIS/COFINS (9.25%)', NULL, NULL, 62.31);

-- Bill 5 items (5)
INSERT INTO bill_items (id, bill_id, item_type, description, quantity, unit_price, amount) VALUES
('10000004-0001-0000-0000-000000000001', 'b0000004-0000-0000-0000-000000000001', 'CONSUMPTION_STANDARD', 'Consumo de energia - 280 kWh', 280.00, 0.7700, 215.60),
('10000004-0001-0000-0000-000000000002', 'b0000004-0000-0000-0000-000000000001', 'TARIFF_FLAG', 'Bandeira tarifária - Verde', 280.00, 0.0000, 0.00),
('10000004-0001-0000-0000-000000000003', 'b0000004-0000-0000-0000-000000000001', 'PUBLIC_LIGHTING', 'Contribuição de iluminação pública', NULL, NULL, 35.00),
('10000004-0001-0000-0000-000000000004', 'b0000004-0000-0000-0000-000000000001', 'TAXES', 'ICMS (18%)', NULL, NULL, 38.81),
('10000004-0001-0000-0000-000000000005', 'b0000004-0000-0000-0000-000000000001', 'TAXES', 'PIS/COFINS (9.25%)', NULL, NULL, 5.99);

-- Bill 6 items (5)
INSERT INTO bill_items (id, bill_id, item_type, description, quantity, unit_price, amount) VALUES
('10000004-0002-0000-0000-000000000001', 'b0000004-0000-0000-0000-000000000002', 'CONSUMPTION_STANDARD', 'Consumo de energia - 295 kWh', 295.00, 0.7700, 227.15),
('10000004-0002-0000-0000-000000000002', 'b0000004-0000-0000-0000-000000000002', 'TARIFF_FLAG', 'Bandeira tarifária - Verde', 295.00, 0.0000, 0.00),
('10000004-0002-0000-0000-000000000003', 'b0000004-0000-0000-0000-000000000002', 'PUBLIC_LIGHTING', 'Contribuição de iluminação pública', NULL, NULL, 35.00),
('10000004-0002-0000-0000-000000000004', 'b0000004-0000-0000-0000-000000000002', 'TAXES', 'ICMS (18%)', NULL, NULL, 40.89),
('10000004-0002-0000-0000-000000000005', 'b0000004-0000-0000-0000-000000000002', 'TAXES', 'PIS/COFINS (9.25%)', NULL, NULL, 7.16);

-- Bill 7 items (5) - Low income tariff
INSERT INTO bill_items (id, bill_id, item_type, description, quantity, unit_price, amount) VALUES
('10000005-0001-0000-0000-000000000001', 'b0000005-0000-0000-0000-000000000001', 'CONSUMPTION_STANDARD', 'Consumo de energia - 220 kWh (Baixa Renda)', 220.00, 0.6540, 143.88),
('10000005-0001-0000-0000-000000000002', 'b0000005-0000-0000-0000-000000000001', 'TARIFF_FLAG', 'Bandeira tarifária - Amarela', 220.00, 0.0178, 3.92),
('10000005-0001-0000-0000-000000000003', 'b0000005-0000-0000-0000-000000000001', 'PUBLIC_LIGHTING', 'Contribuição de iluminação pública', NULL, NULL, 15.00),
('10000005-0001-0000-0000-000000000004', 'b0000005-0000-0000-0000-000000000001', 'TAXES', 'ICMS (12%)', NULL, NULL, 17.27),
('10000005-0001-0000-0000-000000000005', 'b0000005-0000-0000-0000-000000000001', 'TAXES', 'PIS/COFINS (9.25%)', NULL, NULL, 5.83);

-- ============================================================================
-- V008: ANALYSES TABLE
-- 5 analyses with calculations and recommendations
-- ============================================================================

INSERT INTO analyses (
    id, bill_id, average_consumption, cost_per_kwh, comparison_prev_month,
    savings_tips, report_pdf_url
) VALUES
('a0000001-0000-0000-0000-000000000002', 'b0000001-0000-0000-0000-000000000002', 380.00, 1.0385, 17.14,
 'Seu consumo aumentou 17.14% em relação ao mês anterior. Dicas: 1) Verifique se há equipamentos em standby; 2) Considere usar ar-condicionado com temperatura mais alta; 3) Substitua lâmpadas por LED.',
 '/reports/2024/12/client1_analysis_dec.pdf'),

('a0000002-0000-0000-0000-000000000001', 'b0000002-0000-0000-0000-000000000001', 480.00, 1.0840, NULL,
 'Seu consumo está acima da média residencial. Dicas: 1) Verifique o tempo de uso do chuveiro elétrico; 2) Evite deixar geladeira próxima ao fogão; 3) Use máquina de lavar com carga completa.',
 '/reports/2024/12/client2_analysis_dec.pdf'),

('a0000003-0000-0000-0000-000000000001', 'b0000003-0000-0000-0000-000000000001', 550.00, 1.2377, NULL,
 'Perfil comercial com demanda contratada. Dicas: 1) Avalie se a demanda contratada está adequada; 2) Reduza consumo no horário de ponta; 3) Considere instalar sistema solar fotovoltaico.',
 '/reports/2024/11/client3_analysis_nov.pdf'),

('a0000004-0000-0000-0000-000000000002', 'b0000004-0000-0000-0000-000000000002', 287.50, 1.0516, 5.36,
 'Consumo estável com pequeno aumento de 5.36%. Dicas: 1) Mantenha o consumo consciente; 2) Verifique a eficiência energética dos aparelhos; 3) Considere horários de menor demanda.',
 '/reports/2024/11/client4_analysis_nov.pdf'),

('a0000005-0000-0000-0000-000000000001', 'b0000005-0000-0000-0000-000000000001', 220.00, 0.8450, NULL,
 'Consumo dentro da faixa de tarifa social (Baixa Renda). Dicas: 1) Mantenha o consumo abaixo de 220 kWh para continuar no programa; 2) Desligue aparelhos quando não usar; 3) Use luz natural sempre que possível.',
 '/reports/2024/12/client5_analysis_dec.pdf');

-- ============================================================================
-- V009: CONSULTANT_CLIENTS TABLE
-- 10 many-to-many relationships
-- ============================================================================

INSERT INTO consultant_clients (consultant_id, client_id, assigned_at, status) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', '2024-01-20 10:00:00', 'ACTIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '44444444-4444-4444-4444-444444444444', '2024-04-10 14:30:00', 'ACTIVE'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', '2024-02-25 09:15:00', 'ACTIVE'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '55555555-5555-5555-5555-555555555555', '2024-05-28 16:45:00', 'ACTIVE'),
('cccccccc-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', '2024-03-15 11:20:00', 'PENDING'),
('cccccccc-cccc-cccc-cccc-cccccccccccc', '44444444-4444-4444-4444-444444444444', '2024-04-12 08:00:00', 'ACTIVE'),
('dddddddd-dddd-dddd-dddd-dddddddddddd', '55555555-5555-5555-5555-555555555555', '2024-06-01 10:30:00', 'ACTIVE'),
('dddddddd-dddd-dddd-dddd-dddddddddddd', '11111111-1111-1111-1111-111111111111', '2024-01-25 15:00:00', 'INACTIVE'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '22222222-2222-2222-2222-222222222222', '2024-03-01 09:00:00', 'INACTIVE'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '33333333-3333-3333-3333-333333333333', '2024-03-05 13:30:00', 'INACTIVE');

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================

-- Count all records
SELECT 'users' AS table_name, COUNT(*) AS count FROM users
UNION ALL SELECT 'clients', COUNT(*) FROM clients
UNION ALL SELECT 'consultants', COUNT(*) FROM consultants
UNION ALL SELECT 'admins', COUNT(*) FROM admins
UNION ALL SELECT 'tariffs', COUNT(*) FROM tariffs
UNION ALL SELECT 'electricity_bills', COUNT(*) FROM electricity_bills
UNION ALL SELECT 'bill_items', COUNT(*) FROM bill_items
UNION ALL SELECT 'analyses', COUNT(*) FROM analyses
UNION ALL SELECT 'consultant_clients', COUNT(*) FROM consultant_clients
ORDER BY table_name;

-- Expected: users=15, clients=5, consultants=5, admins=5, tariffs=5,
--           electricity_bills=7, bill_items=37, analyses=5, consultant_clients=10

