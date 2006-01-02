CREATE TABLE kontenrahmen (
  id NUMERIC default UNIQUEKEY('kontenrahmen'),
  name varchar(100) NOT NULL,
  mandant_id int(10) NULL,
  UNIQUE (id),
  UNIQUE (name),
  PRIMARY KEY (id)
);

CREATE TABLE steuer (
  id NUMERIC default UNIQUEKEY('steuer'),
  mandant_id int(10) NULL,
  name varchar(255) NOT NULL,
  satz double NOT NULL,
  steuerkonto_id int(10) NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

CREATE TABLE konto (
  id NUMERIC default UNIQUEKEY('konto'),
  kontoart_id int(10) NOT NULL,
  kontotyp_id int(10) NULL,
  kontonummer varchar(4) NOT NULL,
  name varchar(255) NOT NULL,
  kontenrahmen_id int(10) NOT NULL,
  steuer_id int(10) NULL,
  mandant_id int(10) NULL,
  UNIQUE (id),
  UNIQUE (kontenrahmen_id,kontonummer),
  PRIMARY KEY (id)
);

CREATE TABLE konto_ab (
  id NUMERIC default UNIQUEKEY('konto_ab'),
  konto_id int(10) NOT NULL,
  geschaeftsjahr_id int(10) NOT NULL,
  betrag double NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

CREATE TABLE buchung (
  id NUMERIC default UNIQUEKEY('buchung'),
  datum date NOT NULL,
  sollkonto_id int(10) NOT NULL,
  habenkonto_id int(10) NOT NULL,
  buchungstext varchar(255) NOT NULL,
  belegnummer int(10) NOT NULL,
  betrag double NOT NULL,
  steuer double,
  geschaeftsjahr_id int(10) NOT NULL,
  buchung_id int(10),
  UNIQUE (id),
  PRIMARY KEY (id)
);

CREATE TABLE buchungstemplate (
  id NUMERIC default UNIQUEKEY('buchungstemplate'),
  name varchar(255) NOT NULL,
  sollkonto_id int(10) NULL,
  habenkonto_id int(10) NULL,
  buchungstext varchar(255) NULL,
  betrag double NULL,
  steuer double NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

CREATE TABLE finanzamt (
  id NUMERIC default UNIQUEKEY('finanzamt'),
  name varchar(255) NOT NULL,
  strasse varchar(255) NULL,
  postfach varchar(50) NULL,
  plz varchar(7) NULL,
  ort varchar(255) NULL,
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

CREATE TABLE kontotyp (
  id NUMERIC default UNIQUEKEY('kontotyp'),
  name varchar(255) NOT NULL,
  UNIQUE (id),
  UNIQUE (name),
  PRIMARY KEY (id)
);

CREATE TABLE mandant (
  id NUMERIC default UNIQUEKEY('mandant'),
  name1 varchar(255) NULL,
  name2 varchar(255) NULL,
  firma varchar(255) NOT NULL,
  strasse varchar(255) NULL,
  plz varchar(7) NULL,
  ort varchar(255) NULL,
  steuernummer varchar(100) NULL,
  waehrung varchar(10) NULL,
  finanzamt_id int(10) NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

CREATE TABLE geschaeftsjahr (
  id NUMERIC default UNIQUEKEY('geschaeftsjahr'),
  vorjahr_id int(10) NULL,
  mandant_id int(10) NOT NULL,
  beginn date NOT NULL,
  ende date NOT NULL,
  kontenrahmen_id int(10) NOT NULL,
  closed int(1) NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

CREATE TABLE anlagevermoegen (
  id NUMERIC default UNIQUEKEY('anlagevermoegen'),
  mandant_id int(10) NOT NULL,
  name varchar(255) NOT NULL,
  anschaffungskosten double NOT NULL,
  anschaffungsdatum date NOT NULL,
  k_abschreibung_id int(10) NOT NULL,
  konto_id int(10) NULL,
  buchung_id int(10) NULL,
  nutzungsdauer int(2) NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

CREATE TABLE abschreibung (
  id NUMERIC default UNIQUEKEY('abschreibung'),
  av_id int(10) NOT NULL,
  buchung_id int(10) NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

ALTER TABLE kontenrahmen ADD CONSTRAINT fk_kontenrahmen_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id) DEFERRABLE;

ALTER TABLE steuer ADD CONSTRAINT fk_steuer_konto FOREIGN KEY (steuerkonto_id) REFERENCES konto (id) DEFERRABLE;
ALTER TABLE steuer ADD CONSTRAINT fk_steuer_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id) DEFERRABLE;

ALTER TABLE konto ADD CONSTRAINT fk_konto_kontoart FOREIGN KEY (kontoart_id) REFERENCES kontoart (id) DEFERRABLE;
ALTER TABLE konto ADD CONSTRAINT fk_konto_kr FOREIGN KEY (kontenrahmen_id) REFERENCES kontenrahmen (id) DEFERRABLE;
ALTER TABLE konto ADD CONSTRAINT fk_konto_steuer FOREIGN KEY (steuer_id) REFERENCES steuer (id) DEFERRABLE;
ALTER TABLE konto ADD CONSTRAINT fk_konto_kontotyp FOREIGN KEY (kontotyp_id) REFERENCES kontotyp (id) DEFERRABLE;
ALTER TABLE konto ADD CONSTRAINT fk_konto_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id) DEFERRABLE;

ALTER TABLE buchung ADD CONSTRAINT fk_buchung_sk FOREIGN KEY (sollkonto_id) REFERENCES konto (id) DEFERRABLE;
ALTER TABLE buchung ADD CONSTRAINT fk_buchung_hk FOREIGN KEY (habenkonto_id) REFERENCES konto (id) DEFERRABLE;
ALTER TABLE buchung ADD CONSTRAINT fk_buchung_gj FOREIGN KEY (geschaeftsjahr_id) REFERENCES geschaeftsjahr (id) DEFERRABLE;
ALTER TABLE buchung ADD CONSTRAINT fk_buchung_self FOREIGN KEY (buchung_id) REFERENCES buchung (id) DEFERRABLE;

ALTER TABLE buchungstemplate ADD CONSTRAINT fk_buchungt_sk FOREIGN KEY (sollkonto_id) REFERENCES konto (id) DEFERRABLE;
ALTER TABLE buchungstemplate ADD CONSTRAINT fk_buchungt_hk FOREIGN KEY (habenkonto_id) REFERENCES konto (id) DEFERRABLE;

ALTER TABLE mandant ADD CONSTRAINT fk_mandant_fa FOREIGN KEY (finanzamt_id) REFERENCES finanzamt (id) DEFERRABLE;

ALTER TABLE konto_ab ADD CONSTRAINT fk_kontoab_konto FOREIGN KEY (konto_id) REFERENCES konto (id) DEFERRABLE;
ALTER TABLE konto_ab ADD CONSTRAINT fk_kontoab_gj FOREIGN KEY (geschaeftsjahr_id) REFERENCES geschaeftsjahr (id) DEFERRABLE;

ALTER TABLE anlagevermoegen ADD CONSTRAINT fk_av_buchung FOREIGN KEY (buchung_id) REFERENCES buchung (id) DEFERRABLE;
ALTER TABLE anlagevermoegen ADD CONSTRAINT fk_av_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id) DEFERRABLE;
ALTER TABLE anlagevermoegen ADD CONSTRAINT fk_av_konto FOREIGN KEY (konto_id) REFERENCES konto (id) DEFERRABLE;
ALTER TABLE anlagevermoegen ADD CONSTRAINT fk_av_abschreibung FOREIGN KEY (konto_id) REFERENCES konto (id) DEFERRABLE;

ALTER TABLE geschaeftsjahr ADD CONSTRAINT fk_gj_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id) DEFERRABLE;
ALTER TABLE geschaeftsjahr ADD CONSTRAINT fk_gj_kr FOREIGN KEY (kontenrahmen_id) REFERENCES kontenrahmen (id) DEFERRABLE;
ALTER TABLE geschaeftsjahr ADD CONSTRAINT fk_gj_self FOREIGN KEY (vorjahr_id) REFERENCES geschaeftsjahr (id) DEFERRABLE;

ALTER TABLE abschreibung ADD CONSTRAINT fk_abschreibung_av FOREIGN KEY (av_id) REFERENCES anlagevermoegen (id) DEFERRABLE;
ALTER TABLE abschreibung ADD CONSTRAINT fk_abschreibung_buchung FOREIGN KEY (buchung_id) REFERENCES buchung (id) DEFERRABLE;


CREATE INDEX idx_gj_mandant           ON geschaeftsjahr(mandant_id);

CREATE INDEX idx_buchung_belegnummer  ON buchung(belegnummer);
CREATE INDEX idx_buchung_gj           ON buchung(geschaeftsjahr_id);
CREATE INDEX idx_buchung_hilfe        ON buchung(buchung_id);

CREATE INDEX idx_steuer_steuerkonto   ON steuer(steuerkonto_id);

CREATE INDEX idx_konto_kontonummer    ON konto(kontonummer);
CREATE INDEX idx_konto_kontoart       ON konto(kontoart_id);
CREATE INDEX idx_konto_kontenrahmen   ON konto(kontenrahmen_id);
CREATE INDEX idx_konto_steuer         ON konto(steuer_id);
CREATE INDEX idx_konto_mandant        ON konto(mandant_id);

CREATE INDEX idx_av_mandant           ON anlagevermoegen(mandant_id);

CREATE INDEX idx_kontoab_konto        ON konto_ab(konto_id);
CREATE INDEX idx_kontoab_gj           ON konto_ab(geschaeftsjahr_id);

CREATE INDEX idx_abschreibung_av      ON abschreibung(av_id);
CREATE INDEX idx_abschreibung_buchung ON abschreibung(buchung_id);
