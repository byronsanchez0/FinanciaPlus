CREATE TABLE applications (
    id UUID PRIMARY KEY,

    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    full_name VARCHAR(150),
    address VARCHAR(250),
    birth_date DATE,
    gender VARCHAR(30),
    identity_number VARCHAR(50) UNIQUE,
    email VARCHAR(150),
    phone VARCHAR(30),

    biometric_score NUMERIC(5, 2),
    credit_score NUMERIC(5, 2),

    ip_address VARCHAR(45),
    country VARCHAR(100),
    region VARCHAR(100),
    city VARCHAR(100),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_application_status CHECK (
        status IN (
            'DRAFT',
            'REJECTED_AML',
            'REJECTED_SCORE',
            'APPROVED',
            'COMPLETED'
        )
    )
);

CREATE INDEX idx_applications_identity_number
    ON applications(identity_number);

CREATE INDEX idx_applications_status
    ON applications(status);