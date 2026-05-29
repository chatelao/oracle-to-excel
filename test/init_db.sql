CREATE TABLE test_data (
    id NUMBER PRIMARY KEY,
    name VARCHAR2(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO test_data (id, name) VALUES (1, 'Test Item 1');
INSERT INTO test_data (id, name) VALUES (2, 'Test Item 2');

CREATE TABLE large_test_data (
    id NUMBER PRIMARY KEY,
    category VARCHAR2(50),
    val1 NUMBER,
    val2 VARCHAR2(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO large_test_data (id, category, val1, val2)
SELECT
    LEVEL,
    'Category ' || MOD(LEVEL, 10),
    LEVEL * 1.5,
    'Value ' || LEVEL
FROM DUAL
CONNECT BY LEVEL <= 1000;

COMMIT;
EXIT;
