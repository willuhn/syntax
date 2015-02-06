-- ----------------------------------------------------------------------
-- Erweitert die Spalte "kontonummer"
-- ----------------------------------------------------------------------

ALTER CREATE TABLE konto (
  id NUMERIC default UNIQUEKEY('konto'),
  kontoart_id int(10) NOT NULL,
  kontotyp_id int(10) NULL,
  kontonummer varchar(6) NOT NULL,
  name varchar(255) NOT NULL,
  kontenrahmen_id int(10) NOT NULL,
  steuer_id int(10) NULL,
  mandant_id int(10) NULL,
  UNIQUE (id),
  UNIQUE (kontenrahmen_id,kontonummer),
  PRIMARY KEY (id)
);

COMMIT;
