-- ============================================
-- Stored Procedures - Muttley
-- Execute no MySQL Workbench ou console MySQL
-- ============================================

DELIMITER $$

-- 1. Calcular pontos totais de um usuário
CREATE PROCEDURE sp_calcular_pontos_usuario(
    IN p_usuario_id BIGINT,
    OUT p_total INT
)
BEGIN
    SELECT COALESCE(SUM(pontos_ganhos), 0) INTO p_total
    FROM participacao
    WHERE usuario_id = p_usuario_id;
END $$

-- 2. Contar participantes por evento e status de confirmação
CREATE PROCEDURE sp_contar_participantes_por_evento(
    IN p_evento_id BIGINT,
    IN p_confirmado TINYINT,
    OUT p_total BIGINT
)
BEGIN
    SELECT COUNT(*) INTO p_total
    FROM participacao
    WHERE evento_id = p_evento_id AND confirmado = p_confirmado;
END $$

-- 3. Contar medalhas por usuário e nome
CREATE PROCEDURE sp_contar_medalhas_usuario(
    IN p_usuario_id BIGINT,
    IN p_nome VARCHAR(255),
    OUT p_total BIGINT
)
BEGIN
    SELECT COUNT(*) INTO p_total
    FROM medalha
    WHERE usuario_id = p_usuario_id AND nome = p_nome;
END $$

-- 4. Verificar se existe participação
CREATE PROCEDURE sp_existe_participacao(
    IN p_usuario_id BIGINT,
    IN p_evento_id BIGINT,
    OUT p_existe TINYINT
)
BEGIN
    SELECT COUNT(*) > 0 INTO p_existe
    FROM participacao
    WHERE usuario_id = p_usuario_id AND evento_id = p_evento_id;
END $$

-- 5. Verificar existência de participação com status de confirmação
CREATE PROCEDURE sp_existe_participacao_confirmada(
    IN p_usuario_id BIGINT,
    IN p_evento_id BIGINT,
    IN p_confirmado TINYINT,
    OUT p_existe TINYINT
)
BEGIN
    SELECT COUNT(*) > 0 INTO p_existe
    FROM participacao
    WHERE usuario_id = p_usuario_id AND evento_id = p_evento_id AND confirmado = p_confirmado;
END $$

-- 6. Verificar se existe certificado para um usuário em um evento
CREATE PROCEDURE sp_existe_certificado(
    IN p_usuario_id BIGINT,
    IN p_evento_id BIGINT,
    OUT p_existe TINYINT
)
BEGIN
    SELECT COUNT(*) > 0 INTO p_existe
    FROM certificado
    WHERE usuario_id = p_usuario_id AND evento_id = p_evento_id;
END $$

DELIMITER ;
