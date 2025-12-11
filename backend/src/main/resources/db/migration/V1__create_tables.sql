
-- Exemplo simples
CREATE TABLE IF NOT EXISTS tarifa (
                                      id SERIAL PRIMARY KEY,
                                      nome VARCHAR(100) NOT NULL,
    valor_kwh NUMERIC(10,4) NOT NULL,
    criado_em TIMESTAMP DEFAULT now()
    );
