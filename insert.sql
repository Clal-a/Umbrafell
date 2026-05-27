-- ============================================================
-- INSERÇÕES DE TESTE: JOGADORES + RUNS
-- ============================================================

WITH jogador_1 AS (
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
        2,
        1.00,
        1,
        1,
        80,
        5
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
FROM jogador_1;


WITH jogador_2 AS (
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
        'Lucien',
        115,
        90,
        13,
        4,
        1.10,
        2,
        1,
        55,
        4
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
    7600,
    4,
    260,
    44,
    'DERROTA',
    FALSE
FROM jogador_2;


WITH jogador_3 AS (
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
        'Valen',
        130,
        130,
        15,
        5,
        1.05,
        3,
        2,
        110,
        5
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
    8500,
    5,
    310,
    52,
    'DERROTA',
    FALSE
FROM jogador_3;


WITH jogador_4 AS (
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
        'Dorian',
        100,
        65,
        12,
        3,
        1.25,
        2,
        3,
        40,
        3
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
    5400,
    3,
    180,
    31,
    'DERROTA',
    FALSE
FROM jogador_4;


WITH jogador_5 AS (
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
        'Theron',
        145,
        120,
        18,
        7,
        0.95,
        4,
        2,
        150,
        5
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
    10100,
    5,
    390,
    67,
    'VITORIA',
    TRUE
FROM jogador_5;