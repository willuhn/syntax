CREATE TABLE kontenrahmen (
  id NUMERIC default UNIQUEKEY('kontenrahmen'),
  name varchar(100) NOT NULL,
  UNIQUE (id),
  UNIQUE (name),
  PRIMARY KEY (id)
);

CREATE TABLE steuer (
  id NUMERIC default UNIQUEKEY('steuer'),
  name varchar(255) NOT NULL,
  satz double NOT NULL,
  steuerkonto_id int(10) NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

CREATE TABLE konto (
  id NUMERIC default UNIQUEKEY('konto'),
  kontoart_id int(2) NOT NULL,
  kontonummer varchar(4) NOT NULL,
  name varchar(255) NOT NULL,
  kontenrahmen_id int(2) NOT NULL,
  steuer_id int(2),
  UNIQUE (id),
  UNIQUE (kontonummer),
  PRIMARY KEY (id)
);

CREATE TABLE konto_ab (
  id NUMERIC default UNIQUEKEY('konto_ab'),
  konto_id int(2) NOT NULL,
  mandant_id int(2) NOT NULL,
  betrag double NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

CREATE TABLE buchung (
  id NUMERIC default UNIQUEKEY('buchung'),
  datum date NOT NULL,
  konto_id int(10) NOT NULL,
  geldkonto_id int(10) NOT NULL,
  buchungstext varchar(255) NOT NULL,
  belegnummer int(4) NOT NULL,
  betrag double NOT NULL,
  steuer double,
  mandant_id int(10) NOT NULL,
  buchung_id int(10),
  UNIQUE (id),
  PRIMARY KEY (id)
);

CREATE TABLE finanzamt (
  id NUMERIC default UNIQUEKEY('finanzamt'),
  name varchar(255) NOT NULL,
  strasse varchar(255) NOT NULL,
  postfach varchar(50) NOT NULL,
  plz varchar(7) NOT NULL,
  ort varchar(255) NOT NULL,
  UNIQUE (id),
  UNIQUE (name),
  PRIMARY KEY (id)
);

CREATE TABLE kontoart (
  id NUMERIC default UNIQUEKEY('kontoart'),
  name varchar(255) NOT NULL,
  UNIQUE (id),
  UNIQUE (name),
  PRIMARY KEY (id)
);

CREATE TABLE mandant (
  id NUMERIC default UNIQUEKEY('mandant'),
  name1 varchar(255) NOT NULL,
  name2 varchar(255) NOT NULL,
  firma varchar(255) NOT NULL,
  strasse varchar(255) NOT NULL,
  plz varchar(7) NOT NULL,
  ort varchar(255) NOT NULL,
  steuernummer varchar(100) NOT NULL,
  kontenrahmen_id int(2) NOT NULL,
  finanzamt_id int(2) NOT NULL,
  gj_von date NOT NULL,
  gj_bis date NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

ALTER TABLE steuer ADD CONSTRAINT fk_steuerkonto FOREIGN KEY (steuerkonto_id) REFERENCES konto (id) DEFERRABLE;

ALTER TABLE konto ADD CONSTRAINT fk_kontoart FOREIGN KEY (kontoart_id) REFERENCES kontoart (id) DEFERRABLE;
ALTER TABLE konto ADD CONSTRAINT fk_kontenrahmen FOREIGN KEY (kontenrahmen_id) REFERENCES kontenrahmen (id) DEFERRABLE;
ALTER TABLE konto ADD CONSTRAINT fk_steuer FOREIGN KEY (steuer_id) REFERENCES steuer (id) DEFERRABLE;

ALTER TABLE buchung ADD CONSTRAINT fk_konto FOREIGN KEY (konto_id) REFERENCES konto (id) DEFERRABLE;
ALTER TABLE buchung ADD CONSTRAINT fk_geldkonto FOREIGN KEY (geldkonto_id) REFERENCES konto (id) DEFERRABLE;
ALTER TABLE buchung ADD CONSTRAINT fk_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id) DEFERRABLE;

ALTER TABLE mandant ADD CONSTRAINT fk_kontenrahmen_mand FOREIGN KEY (kontenrahmen_id) REFERENCES kontenrahmen (id) DEFERRABLE;
ALTER TABLE mandant ADD CONSTRAINT fk_finanzamt FOREIGN KEY (finanzamt_id) REFERENCES finanzamt (id) DEFERRABLE;

ALTER TABLE konto_ab ADD CONSTRAINT fk_konto2 FOREIGN KEY (konto_id) REFERENCES konto (id) DEFERRABLE;
ALTER TABLE konto_ab ADD CONSTRAINT fk_mandant2 FOREIGN KEY (mandant_id) REFERENCES mandant (id) DEFERRABLE;

CREATE INDEX idx_belegnummer ON buchung(belegnummer);
CREATE INDEX idx_mandant ON buchung(mandant_id);
CREATE INDEX idx_steuerkonto ON steuer(steuerkonto_id);
CREATE INDEX idx_kontonummer ON konto(kontonummer);
CREATE INDEX idx_kontoart ON konto(kontoart_id);
CREATE INDEX idx_kontenrahmen ON konto(kontenrahmen_id);
CREATE INDEX idx_steuer ON konto(steuer_id);
