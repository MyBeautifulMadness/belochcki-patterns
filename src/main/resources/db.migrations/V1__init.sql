CREATE TABLE debit_account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL,
    created_date DATE NOT NULL,
    created_time TIME NOT NULL,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
    name VARCHAR(16) NOT NULL,
    currency_code VARCHAR(3) NOT NULL REFERENCES currency(code),
    status VARCHAR(16) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_debit_account_name ON debit_account(name);

CREATE INDEX idx_debit_account_client_id ON debit_account (client_id);
CREATE INDEX idx_debit_account_status ON debit_account (status);

CREATE TABLE credit_account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL,
    created_date DATE NOT NULL,
    created_time TIME NOT NULL,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
    name VARCHAR(16) NOT NULL,
    currency_code VARCHAR(3) NOT NULL REFERENCES currency(code),
    status VARCHAR(16) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_credit_account_client_id ON credit_account(client_id);

CREATE INDEX idx_credit_account_client_id ON credit_account (client_id);
CREATE INDEX idx_credit_account_status ON credit_account (status);

CREATE TABLE account_operation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL,
    op_date DATE NOT NULL,
    op_time TIME NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    comment VARCHAR(400),
    operation_type VARCHAR(32) NOT NULL
);

CREATE INDEX idx_account_operation_account_id ON account_operation (account_type, account_id);
CREATE INDEX idx_account_operation_date_time ON account_operation (op_date DESC, op_time DESC);

CREATE TABLE master_account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_date DATE NOT NULL,
    created_time TIME NOT NULL,
    balance NUMERIC(19,2) NOT NULL,
    name VARCHAR(16) NOT NULL,
    currency_code VARCHAR(3) NOT NULL REFERENCES currency(code),
    status VARCHAR(16) NOT NULL
);

CREATE TABLE processed_command (
    operation_id UUID PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL
);

CREATE TABLE idempotency_record (
    idempotency_key VARCHAR(255) PRIMARY KEY,
    operation_name VARCHAR(100) NOT NULL,
    response_status INT NOT NULL,
    response_body text,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);