ALTER CREATE TABLE konto (
  id NUMERIC default UNIQUEKEY('konto'),
  kontoart_id int(10) NOT NULL,
  kontotyp_id int(10) NULL,
  kontonummer varchar(4) NOT NULL,
  name varchar(255) NOT NULL,
  kontenrahmen_id int(10) NOT NULL,
  steuer_id int(10),
  mandant_id int(10),
  UNIQUE (id),
  UNIQUE (kontenrahmen_id,kontonummer),
  PRIMARY KEY (id)
);

ALTER TABLE konto ADD CONSTRAINT fk_konto_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id) DEFERRABLE;

CREATE INDEX idx_konto_mandant ON konto(mandant_id);
