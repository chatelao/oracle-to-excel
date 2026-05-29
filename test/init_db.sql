CREATE TABLE test_data (
    id NUMBER PRIMARY KEY,
    name VARCHAR2(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO test_data (id, name) VALUES (1, 'Test Item 1');
INSERT INTO test_data (id, name) VALUES (2, 'Test Item 2');
COMMIT;
EXIT;
