-- ============================================================
-- BANCO DE DADOS: UMBRAFELL
-- Trabalho TLP2 - JavaFX + PostgreSQL + JDBC + MVC
-- Versão corrigida: sem SORTE, sem CHAVE e sem tabela de configurações
-- ============================================================

-- Se ainda não criou o banco, execute este comando separadamente:
-- CREATE DATABASE umbrafell;

-- No pgAdmin, selecione o banco "umbrafell" antes de executar este script.
-- No psql, você pode conectar com:
-- \c umbrafell


-- ============================================================
-- LIMPEZA DAS TABELAS E VIEW
-- Use somente em ambiente de desenvolvimento.
-- ============================================================

DROP VIEW IF EXISTS vw_ranking CASCADE;

DROP TABLE IF EXISTS inventario_itens CASCADE;
DROP TABLE IF EXISTS jogador_upgrades CASCADE;
DROP TABLE IF EXISTS jogador_talismas CASCADE;
DROP TABLE IF EXISTS runs CASCADE;
DROP TABLE IF EXISTS talismas CASCADE;
DROP TABLE IF EXISTS itens CASCADE;
DROP TABLE IF EXISTS upgrades CASCADE;
DROP TABLE IF EXISTS inimigos CASCADE;
DROP TABLE IF EXISTS jogadores CASCADE;


-- ============================================================
-- TABELA: jogadores
-- Armazena os dados principais e o progresso básico do jogador.
-- ============================================================

CREATE TABLE jogadores (
    id_jogador INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    nome VARCHAR(100) NOT NULL,

    vida_maxima INTEGER NOT NULL DEFAULT 100,
    vida_atual INTEGER NOT NULL DEFAULT 100,

    dano INTEGER NOT NULL DEFAULT 10,
    defesa INTEGER NOT NULL DEFAULT 0,
    velocidade NUMERIC(10,2) NOT NULL DEFAULT 1.00,

    ataque_principal_nivel INTEGER NOT NULL DEFAULT 1,
    ataque_secundario_nivel INTEGER NOT NULL DEFAULT 1,

    joias_sombrias INTEGER NOT NULL DEFAULT 0,
    fase_atual INTEGER NOT NULL DEFAULT 1,

    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_jogadores_nome
        CHECK (char_length(trim(nome)) > 0),

    CONSTRAINT chk_jogadores_vida_maxima
        CHECK (vida_maxima > 0),

    CONSTRAINT chk_jogadores_vida_atual
        CHECK (vida_atual >= 0 AND vida_atual <= vida_maxima),

    CONSTRAINT chk_jogadores_dano
        CHECK (dano >= 0),

    CONSTRAINT chk_jogadores_defesa
        CHECK (defesa >= 0),

    CONSTRAINT chk_jogadores_velocidade
        CHECK (velocidade > 0),

    CONSTRAINT chk_jogadores_ataque_principal
        CHECK (ataque_principal_nivel >= 1),

    CONSTRAINT chk_jogadores_ataque_secundario
        CHECK (ataque_secundario_nivel >= 1),

    CONSTRAINT chk_jogadores_joias
        CHECK (joias_sombrias >= 0),

    CONSTRAINT chk_jogadores_fase
        CHECK (fase_atual >= 1)
);


-- ============================================================
-- TABELA: inimigos
-- Armazena os inimigos e seus atributos base.
-- ============================================================

CREATE TABLE inimigos (
    id_inimigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    nome VARCHAR(100) NOT NULL UNIQUE,

    vida_base INTEGER NOT NULL,
    dano_multiplicador NUMERIC(10,2) NOT NULL,
    velocidade NUMERIC(10,2) NOT NULL,
    recompensa_multiplicador NUMERIC(10,2) NOT NULL,

    aparece_fase_normal BOOLEAN NOT NULL DEFAULT TRUE,
    aparece_fase_boss BOOLEAN NOT NULL DEFAULT FALSE,

    recebe_dano_distancia BOOLEAN NOT NULL DEFAULT TRUE,

    observacao TEXT,

    CONSTRAINT chk_inimigos_vida_base
        CHECK (vida_base > 0),

    CONSTRAINT chk_inimigos_dano_multiplicador
        CHECK (dano_multiplicador > 0),

    CONSTRAINT chk_inimigos_velocidade
        CHECK (velocidade >= 0),

    CONSTRAINT chk_inimigos_recompensa
        CHECK (recompensa_multiplicador >= 0)
);


-- ============================================================
-- TABELA: upgrades
-- Armazena os upgrades permanentes disponíveis.
-- ============================================================

CREATE TABLE upgrades (
    id_upgrade INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,

    atributo VARCHAR(50) NOT NULL,
    nivel_maximo INTEGER NOT NULL DEFAULT 5,

    custo_nivel_1 INTEGER NOT NULL DEFAULT 20,
    custo_nivel_2 INTEGER NOT NULL DEFAULT 35,
    custo_nivel_3 INTEGER NOT NULL DEFAULT 55,
    custo_nivel_4 INTEGER NOT NULL DEFAULT 80,
    custo_nivel_5 INTEGER NOT NULL DEFAULT 110,

    incremento NUMERIC(10,2) NOT NULL,

    CONSTRAINT chk_upgrades_atributo
        CHECK (atributo IN (
            'VIDA_MAXIMA',
            'DANO',
            'VELOCIDADE',
            'DEFESA',
            'ATAQUE_PRINCIPAL',
            'ATAQUE_SECUNDARIO'
        )),

    CONSTRAINT chk_upgrades_nivel_maximo
        CHECK (nivel_maximo >= 1 AND nivel_maximo <= 5),

    CONSTRAINT chk_upgrades_custos
        CHECK (
            custo_nivel_1 >= 0 AND
            custo_nivel_2 >= 0 AND
            custo_nivel_3 >= 0 AND
            custo_nivel_4 >= 0 AND
            custo_nivel_5 >= 0
        ),

    CONSTRAINT chk_upgrades_incremento
        CHECK (incremento > 0)
);


-- ============================================================
-- TABELA: itens
-- Armazena itens da loja e do inventário.
-- ============================================================

CREATE TABLE itens (
    id_item INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    nome VARCHAR(100) NOT NULL UNIQUE,
    tipo VARCHAR(50) NOT NULL,
    descricao TEXT,

    valor_joias_sombrias INTEGER NOT NULL,

    CONSTRAINT chk_itens_tipo
        CHECK (tipo IN (
            'POCAO',
            'TALISMA',
            'MELHORIA_ARMA_PRINCIPAL',
            'MELHORIA_ATAQUE_SECUNDARIO'
        )),

    CONSTRAINT chk_itens_valor
        CHECK (valor_joias_sombrias >= 0)
);


-- ============================================================
-- TABELA: talismas
-- Cada talismã possui dois buffs e um debuff.
-- ============================================================

CREATE TABLE talismas (
    id_talisma INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    id_item INTEGER NOT NULL UNIQUE,

    atributo_buff_1 VARCHAR(50) NOT NULL,
    valor_buff_1 NUMERIC(10,2) NOT NULL,

    atributo_buff_2 VARCHAR(50) NOT NULL,
    valor_buff_2 NUMERIC(10,2) NOT NULL,

    atributo_debuff VARCHAR(50) NOT NULL,
    valor_debuff NUMERIC(10,2) NOT NULL,

    CONSTRAINT fk_talismas_itens
        FOREIGN KEY (id_item)
        REFERENCES itens(id_item)
        ON DELETE CASCADE,

    CONSTRAINT chk_talismas_atributo_buff_1
        CHECK (atributo_buff_1 IN (
            'VIDA_MAXIMA',
            'DANO',
            'VELOCIDADE',
            'DEFESA',
            'ATAQUE_PRINCIPAL',
            'ATAQUE_SECUNDARIO'
        )),

    CONSTRAINT chk_talismas_atributo_buff_2
        CHECK (atributo_buff_2 IN (
            'VIDA_MAXIMA',
            'DANO',
            'VELOCIDADE',
            'DEFESA',
            'ATAQUE_PRINCIPAL',
            'ATAQUE_SECUNDARIO'
        )),

    CONSTRAINT chk_talismas_atributo_debuff
        CHECK (atributo_debuff IN (
            'VIDA_MAXIMA',
            'DANO',
            'VELOCIDADE',
            'DEFESA',
            'ATAQUE_PRINCIPAL',
            'ATAQUE_SECUNDARIO'
        )),

    CONSTRAINT chk_talismas_buffs_diferentes
        CHECK (
            atributo_buff_1 <> atributo_buff_2
            AND atributo_buff_1 <> atributo_debuff
            AND atributo_buff_2 <> atributo_debuff
        ),

    CONSTRAINT chk_talismas_valores
        CHECK (
            valor_buff_1 > 0
            AND valor_buff_2 > 0
            AND valor_debuff > 0
        )
);


-- ============================================================
-- TABELA: jogador_upgrades
-- Relaciona jogadores com upgrades adquiridos.
-- ============================================================

CREATE TABLE jogador_upgrades (
    id_jogador_upgrade INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    id_jogador INTEGER NOT NULL,
    id_upgrade INTEGER NOT NULL,

    nivel INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT fk_jogador_upgrades_jogadores
        FOREIGN KEY (id_jogador)
        REFERENCES jogadores(id_jogador)
        ON DELETE CASCADE,

    CONSTRAINT fk_jogador_upgrades_upgrades
        FOREIGN KEY (id_upgrade)
        REFERENCES upgrades(id_upgrade)
        ON DELETE CASCADE,

    CONSTRAINT uq_jogador_upgrade
        UNIQUE (id_jogador, id_upgrade),

    CONSTRAINT chk_jogador_upgrades_nivel
        CHECK (nivel >= 1 AND nivel <= 5)
);


-- ============================================================
-- TABELA: jogador_talismas
-- Relaciona jogadores com talismãs adquiridos.
-- ============================================================

CREATE TABLE jogador_talismas (
    id_jogador_talisma INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    id_jogador INTEGER NOT NULL,
    id_talisma INTEGER NOT NULL,

    equipado BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_jogador_talismas_jogadores
        FOREIGN KEY (id_jogador)
        REFERENCES jogadores(id_jogador)
        ON DELETE CASCADE,

    CONSTRAINT fk_jogador_talismas_talismas
        FOREIGN KEY (id_talisma)
        REFERENCES talismas(id_talisma)
        ON DELETE CASCADE,

    CONSTRAINT uq_jogador_talisma
        UNIQUE (id_jogador, id_talisma)
);


-- ============================================================
-- TABELA: inventario_itens
-- Armazena itens do inventário do jogador.
-- ============================================================

CREATE TABLE inventario_itens (
    id_inventario_item INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    id_jogador INTEGER NOT NULL,
    id_item INTEGER NOT NULL,

    quantidade INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT fk_inventario_itens_jogadores
        FOREIGN KEY (id_jogador)
        REFERENCES jogadores(id_jogador)
        ON DELETE CASCADE,

    CONSTRAINT fk_inventario_itens_itens
        FOREIGN KEY (id_item)
        REFERENCES itens(id_item)
        ON DELETE CASCADE,

    CONSTRAINT uq_inventario_item
        UNIQUE (id_jogador, id_item),

    CONSTRAINT chk_inventario_quantidade
        CHECK (quantidade >= 0)
);


-- ============================================================
-- TABELA: runs
-- Armazena o histórico de partidas.
-- Essa tabela será usada para o ranking.
-- ============================================================

CREATE TABLE runs (
    id_run INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    id_jogador INTEGER NOT NULL,

    pontuacao INTEGER NOT NULL DEFAULT 0,
    fase_alcancada INTEGER NOT NULL DEFAULT 1,

    joias_sombrias_obtidas INTEGER NOT NULL DEFAULT 0,
    inimigos_derrotados INTEGER NOT NULL DEFAULT 0,

    resultado VARCHAR(30) NOT NULL,

    venceu_boss BOOLEAN NOT NULL DEFAULT FALSE,

    criada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_runs_jogadores
        FOREIGN KEY (id_jogador)
        REFERENCES jogadores(id_jogador)
        ON DELETE CASCADE,

    CONSTRAINT chk_runs_pontuacao
        CHECK (pontuacao >= 0),

    CONSTRAINT chk_runs_fase
        CHECK (fase_alcancada >= 1),

    CONSTRAINT chk_runs_joias
        CHECK (joias_sombrias_obtidas >= 0),

    CONSTRAINT chk_runs_inimigos
        CHECK (inimigos_derrotados >= 0),

    CONSTRAINT chk_runs_resultado
        CHECK (resultado IN ('VITORIA', 'DERROTA'))
);


-- ============================================================
-- INSERTS: INIMIGOS
-- Regras:
-- Morcego = n
-- Vampiro = n * 1,25
-- Dragão = n * 1,30
-- Sacerdote normal = n * 1,50
-- Sacerdote boss = n * 1,70
-- Drácula = n * 3,00
-- ============================================================

INSERT INTO inimigos (
    nome,
    vida_base,
    dano_multiplicador,
    velocidade,
    recompensa_multiplicador,
    aparece_fase_normal,
    aparece_fase_boss,
    recebe_dano_distancia,
    observacao
) VALUES
(
    'Morcego',
    15,
    1.00,
    2.50,
    1.00,
    TRUE,
    TRUE,
    TRUE,
    'Inimigo mais fraco e rápido. Dano base n. Recompensa J.'
),
(
    'Vampiro',
    30,
    1.25,
    1.50,
    2.00,
    TRUE,
    TRUE,
    TRUE,
    'Inimigo intermediário. Dano n * 1,25. Recompensa J * 2.'
),
(
    'Dragão',
    40,
    1.30,
    0.00,
    3.00,
    TRUE,
    FALSE,
    FALSE,
    'Aparece aleatoriamente em fases específicas. Só recebe dano de ataques próximos. Recompensa J * 3.'
),
(
    'Sacerdote',
    50,
    1.50,
    1.20,
    2.00,
    TRUE,
    FALSE,
    TRUE,
    'Inimigo perigoso das fases normais. Dano n * 1,50. Recompensa J * 2.'
),
(
    'Sacerdote Boss',
    50,
    1.70,
    1.20,
    2.00,
    FALSE,
    TRUE,
    TRUE,
    'Versão fortalecida do sacerdote na fase do boss. Dano n * 1,70.'
),
(
    'Drácula',
    200,
    3.00,
    1.00,
    0.00,
    FALSE,
    TRUE,
    TRUE,
    'Boss final. Dano n * 3,00. Possui projéteis e invocação de morcegos e vampiros.'
);


-- ============================================================
-- INSERTS: ITENS DA LOJA
-- Valores:
-- Poção = 18
-- Talismã comum = 30
-- Talismã forte = 45
-- Melhoria da arma principal = 40
-- Melhoria do ataque secundário = 45
-- ============================================================

INSERT INTO itens (
    nome,
    tipo,
    descricao,
    valor_joias_sombrias
) VALUES
(
    'Poção de Sangue',
    'POCAO',
    'Consumível que recupera parte da vida do jogador.',
    18
),
(
    'Talismã Carmesim',
    'TALISMA',
    'Talismã comum que aumenta dano e defesa, mas reduz velocidade.',
    30
),
(
    'Talismã da Névoa',
    'TALISMA',
    'Talismã comum que aumenta velocidade e ataque secundário, mas reduz defesa.',
    30
),
(
    'Talismã Profano',
    'TALISMA',
    'Talismã forte que aumenta vida máxima e dano, mas reduz velocidade.',
    45
),
(
    'Melhoria da Lâmina Sombria',
    'MELHORIA_ARMA_PRINCIPAL',
    'Melhoria que fortalece o ataque corpo a corpo da Lâmina Sombria.',
    40
),
(
    'Melhoria do Ataque Sombrio',
    'MELHORIA_ATAQUE_SECUNDARIO',
    'Melhoria que fortalece o ataque secundário à distância.',
    45
);


-- ============================================================
-- INSERTS: TALISMÃS
-- Cada talismã possui dois buffs e um debuff.
-- ============================================================

INSERT INTO talismas (
    id_item,
    atributo_buff_1,
    valor_buff_1,
    atributo_buff_2,
    valor_buff_2,
    atributo_debuff,
    valor_debuff
) VALUES
(
    (SELECT id_item FROM itens WHERE nome = 'Talismã Carmesim'),
    'DANO',
    5.00,
    'DEFESA',
    3.00,
    'VELOCIDADE',
    0.20
),
(
    (SELECT id_item FROM itens WHERE nome = 'Talismã da Névoa'),
    'VELOCIDADE',
    0.30,
    'ATAQUE_SECUNDARIO',
    3.00,
    'DEFESA',
    2.00
),
(
    (SELECT id_item FROM itens WHERE nome = 'Talismã Profano'),
    'VIDA_MAXIMA',
    20.00,
    'DANO',
    4.00,
    'VELOCIDADE',
    0.15
);


-- ============================================================
-- INSERTS: UPGRADES
-- Custos:
-- Nível 1 = 20
-- Nível 2 = 35
-- Nível 3 = 55
-- Nível 4 = 80
-- Nível 5 = 110
-- ============================================================

INSERT INTO upgrades (
    nome,
    descricao,
    atributo,
    nivel_maximo,
    custo_nivel_1,
    custo_nivel_2,
    custo_nivel_3,
    custo_nivel_4,
    custo_nivel_5,
    incremento
) VALUES
(
    'Vigor Sombrio',
    'Aumenta a vida máxima do jogador.',
    'VIDA_MAXIMA',
    5,
    20, 35, 55, 80, 110,
    15.00
),
(
    'Força da Lâmina',
    'Aumenta o dano geral do jogador.',
    'DANO',
    5,
    20, 35, 55, 80, 110,
    3.00
),
(
    'Passos da Névoa',
    'Aumenta a velocidade do jogador.',
    'VELOCIDADE',
    5,
    20, 35, 55, 80, 110,
    0.15
),
(
    'Defesa Carmesim',
    'Aumenta a defesa do jogador.',
    'DEFESA',
    5,
    20, 35, 55, 80, 110,
    2.00
),
(
    'Domínio da Lâmina Sombria',
    'Aumenta o poder do ataque principal corpo a corpo.',
    'ATAQUE_PRINCIPAL',
    5,
    20, 35, 55, 80, 110,
    4.00
),
(
    'Disparo Sombrio',
    'Aumenta o poder do ataque secundário à distância.',
    'ATAQUE_SECUNDARIO',
    5,
    20, 35, 55, 80, 110,
    4.00
);


-- ============================================================
-- VIEW: RANKING
-- Campos exigidos:
-- posição, jogador, pontuação, fase alcançada,
-- Joias Sombrias obtidas, inimigos derrotados e resultado.
-- ============================================================

CREATE OR REPLACE VIEW vw_ranking AS
SELECT
    ROW_NUMBER() OVER (
        ORDER BY
            r.pontuacao DESC,
            r.fase_alcancada DESC,
            r.joias_sombrias_obtidas DESC,
            r.inimigos_derrotados DESC
    ) AS posicao,
    j.nome AS jogador,
    r.pontuacao,
    r.fase_alcancada,
    r.joias_sombrias_obtidas,
    r.inimigos_derrotados,
    r.resultado
FROM runs r
JOIN jogadores j ON j.id_jogador = r.id_jogador;


-- ============================================================
-- ÍNDICES ÚTEIS PARA CONSULTAS
-- ============================================================

CREATE INDEX idx_runs_jogador
ON runs(id_jogador);

CREATE INDEX idx_runs_ranking
ON runs(
    pontuacao DESC,
    fase_alcancada DESC,
    joias_sombrias_obtidas DESC,
    inimigos_derrotados DESC
);

CREATE INDEX idx_inventario_jogador
ON inventario_itens(id_jogador);

CREATE INDEX idx_jogador_upgrades_jogador
ON jogador_upgrades(id_jogador);

CREATE INDEX idx_jogador_talismas_jogador
ON jogador_talismas(id_jogador);


-- ============================================================
-- DADOS DE TESTE OPCIONAIS
-- Pode apagar essa parte se quiser começar sem jogador cadastrado.
-- ============================================================

WITH jogador_teste AS (
    INSERT INTO jogadores (
        nome,
        vida_maxima,
        vida_atual,
        dano,
        defesa,
        velocidade,
        ataque_principal_nivel,
        ataque_secundario_nivel,
        joias_sombrias,
        fase_atual
    ) VALUES (
        'Aldric',
        100,
        100,
        10,
        0,
        1.00,
        1,
        1,
        0,
        1
    )
    RETURNING id_jogador
)
INSERT INTO runs (
    id_jogador,
    pontuacao,
    fase_alcancada,
    joias_sombrias_obtidas,
    inimigos_derrotados,
    resultado,
    venceu_boss
)
SELECT
    id_jogador,
    9200,
    5,
    340,
    58,
    'VITORIA',
    TRUE
FROM jogador_teste;


-- ============================================================
-- CONSULTAS ÚTEIS
-- ============================================================

-- Ranking:
-- SELECT * FROM vw_ranking LIMIT 10;

-- Loja:
-- SELECT nome, tipo, descricao, valor_joias_sombrias
-- FROM itens
-- ORDER BY valor_joias_sombrias;

-- Upgrades:
-- SELECT *
-- FROM upgrades
-- ORDER BY id_upgrade;

-- Inimigos:
-- SELECT *
-- FROM inimigos
-- ORDER BY id_inimigo;

-- Inventário de um jogador:
-- SELECT 
--     j.nome AS jogador,
--     i.nome AS item,
--     i.tipo,
--     inv.quantidade
-- FROM inventario_itens inv
-- JOIN jogadores j ON j.id_jogador = inv.id_jogador
-- JOIN itens i ON i.id_item = inv.id_item
-- WHERE j.id_jogador = 1;
