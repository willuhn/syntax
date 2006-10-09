CREATE TABLE IF NOT EXISTS kontenrahmen (
  id int NOT NULL AUTO_INCREMENT,
  name varchar(100) NOT NULL,
  mandant_id int(10) NULL,
  UNIQUE (id),
  UNIQUE (name),
  PRIMARY KEY (id)
) TYPE = INNODB;

CREATE TABLE IF NOT EXISTS steuer (
  id int NOT NULL AUTO_INCREMENT,
  mandant_id int(10) NULL,
  name varchar(255) NOT NULL,
  satz double NOT NULL,
  steuerkonto_id int(10) NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
) TYPE = INNODB;

CREATE TABLE IF NOT EXISTS konto (
  id int NOT NULL AUTO_INCREMENT,
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
) TYPE = INNODB;

CREATE TABLE IF NOT EXISTS konto_ab (
  id int NOT NULL AUTO_INCREMENT,
  konto_id int(10) NOT NULL,
  geschaeftsjahr_id int(10) NOT NULL,
  betrag double NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
) TYPE = INNODB;

CREATE TABLE IF NOT EXISTS buchung (
  id int NOT NULL AUTO_INCREMENT,
  datum date NOT NULL,
  sollkonto_id int(10) NOT NULL,
  habenkonto_id int(10) NOT NULL,
  buchungstext varchar(255) NOT NULL,
  belegnummer int(10) NOT NULL,
  betrag double NOT NULL,
  steuer double,
  geschaeftsjahr_id int(10) NOT NULL,
  buchung_id int(10),
  geprueft int(1) NULL,
  hb_umsatz_id char(7),
  UNIQUE (id),
  PRIMARY KEY (id)
) TYPE = INNODB;

CREATE TABLE IF NOT EXISTS buchungstemplate (
  id int NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  sollkonto_id int(10) NULL,
  habenkonto_id int(10) NULL,
  buchungstext varchar(255) NULL,
  mandant_id int(10) NULL,
  kontenrahmen_id int(10) NULL,
  betrag double NULL,
  steuer double NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
) TYPE = INNODB;

CREATE TABLE IF NOT EXISTS finanzamt (
  id int NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  strasse varchar(255) NULL,
  postfach varchar(50) NULL,
  plz varchar(7) NULL,
  ort varchar(255) NULL,
  UNIQUE (id),
  UNIQUE (name),
  PRIMARY KEY (id)
) TYPE = INNODB;

CREATE TABLE IF NOT EXISTS kontoart (
  id int NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  UNIQUE (id),
  UNIQUE (name),
  PRIMARY KEY (id)
) TYPE = INNODB;

CREATE TABLE IF NOT EXISTS kontotyp (
  id int NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  UNIQUE (id),
  UNIQUE (name),
  PRIMARY KEY (id)
) TYPE = INNODB;

CREATE TABLE IF NOT EXISTS mandant (
  id int NOT NULL AUTO_INCREMENT,
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
) TYPE = INNODB;

CREATE TABLE IF NOT EXISTS geschaeftsjahr (
  id int NOT NULL AUTO_INCREMENT,
  vorjahr_id int(10) NULL,
  mandant_id int(10) NOT NULL,
  beginn date NULL,
  ende date NULL,
  kontenrahmen_id int(10) NOT NULL,
  closed int(1) NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
) TYPE = INNODB;

CREATE TABLE IF NOT EXISTS anlagevermoegen (
  id int NOT NULL AUTO_INCREMENT,
  mandant_id int(10) NOT NULL,
  name varchar(255) NOT NULL,
  anschaffungskosten double NOT NULL,
  restwert double NULL,
  anschaffungsdatum date NOT NULL,
  k_abschreibung_id int(10) NOT NULL,
  konto_id int(10) NULL,
  nutzungsdauer int(2) NOT NULL,
  buchung_id int(10) NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
) TYPE = INNODB;

CREATE TABLE IF NOT EXISTS abschreibung (
  id int NOT NULL AUTO_INCREMENT,
  av_id int(10) NOT NULL,
  buchung_id int(10) NOT NULL,
  sonderabschreibung int(1) NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
) TYPE = INNODB;

CREATE INDEX idx_kr_mandant           ON kontenrahmen(mandant_id);

CREATE INDEX idx_steuer_steuerkonto   ON steuer(steuerkonto_id);
CREATE INDEX idx_steuer_mandant       ON steuer(mandant_id);

CREATE INDEX idx_konto_kontonummer    ON konto(kontonummer);
CREATE INDEX idx_konto_kontoart       ON konto(kontoart_id);
CREATE INDEX idx_konto_kontotyp       ON konto(kontotyp_id);
CREATE INDEX idx_konto_kontenrahmen   ON konto(kontenrahmen_id);
CREATE INDEX idx_konto_steuer         ON konto(steuer_id);
CREATE INDEX idx_konto_mandant        ON konto(mandant_id);

CREATE INDEX idx_buchung_belegnummer  ON buchung(belegnummer);
CREATE INDEX idx_buchung_gj           ON buchung(geschaeftsjahr_id);
CREATE INDEX idx_buchung_self         ON buchung(buchung_id);
CREATE INDEX idx_buchung_sk           ON buchung(sollkonto_id);
CREATE INDEX idx_buchung_hk           ON buchung(habenkonto_id);
CREATE INDEX idx_buchung_hb_umsatz_id ON buchung(hb_umsatz_id);

CREATE INDEX idx_bt_hk                ON buchungstemplate(habenkonto_id);
CREATE INDEX idx_bt_sk                ON buchungstemplate(sollkonto_id);
CREATE INDEX idx_bt_mandant           ON buchungstemplate(mandant_id);
CREATE INDEX idx_bt_kr                ON buchungstemplate(kontenrahmen_id);

CREATE INDEX idx_mandant_fa           ON mandant(finanzamt_id);

CREATE INDEX idx_kontoab_konto        ON konto_ab(konto_id);
CREATE INDEX idx_kontoab_gj           ON konto_ab(geschaeftsjahr_id);

CREATE INDEX idx_gj_mandant           ON geschaeftsjahr(mandant_id);
CREATE INDEX idx_gj_kr                ON geschaeftsjahr(kontenrahmen_id);
CREATE INDEX idx_gj_self              ON geschaeftsjahr(vorjahr_id);

CREATE INDEX idx_av_mandant           ON anlagevermoegen(mandant_id);
CREATE INDEX idx_av_konto             ON anlagevermoegen(konto_id);
CREATE INDEX idx_av_k_abschreibung    ON anlagevermoegen(k_abschreibung_id);
CREATE INDEX idx_av_buchung           ON anlagevermoegen(buchung_id);

CREATE INDEX idx_abschreibung_av      ON abschreibung(av_id);
CREATE INDEX idx_abschreibung_buchung ON abschreibung(buchung_id);

ALTER TABLE kontenrahmen ADD CONSTRAINT fk_kontenrahmen_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id);

ALTER TABLE steuer ADD CONSTRAINT fk_steuer_konto FOREIGN KEY (steuerkonto_id) REFERENCES konto (id);
ALTER TABLE steuer ADD CONSTRAINT fk_steuer_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id);

ALTER TABLE konto ADD CONSTRAINT fk_konto_kontoart FOREIGN KEY (kontoart_id) REFERENCES kontoart (id);
ALTER TABLE konto ADD CONSTRAINT fk_konto_kr FOREIGN KEY (kontenrahmen_id) REFERENCES kontenrahmen (id);
ALTER TABLE konto ADD CONSTRAINT fk_konto_steuer FOREIGN KEY (steuer_id) REFERENCES steuer (id);
ALTER TABLE konto ADD CONSTRAINT fk_konto_kontotyp FOREIGN KEY (kontotyp_id) REFERENCES kontotyp (id);
ALTER TABLE konto ADD CONSTRAINT fk_konto_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id);

ALTER TABLE buchung ADD CONSTRAINT fk_buchung_sk FOREIGN KEY (sollkonto_id) REFERENCES konto (id);
ALTER TABLE buchung ADD CONSTRAINT fk_buchung_hk FOREIGN KEY (habenkonto_id) REFERENCES konto (id);
ALTER TABLE buchung ADD CONSTRAINT fk_buchung_gj FOREIGN KEY (geschaeftsjahr_id) REFERENCES geschaeftsjahr (id);
ALTER TABLE buchung ADD CONSTRAINT fk_buchung_self FOREIGN KEY (buchung_id) REFERENCES buchung (id);

ALTER TABLE buchungstemplate ADD CONSTRAINT fk_buchungt_sk FOREIGN KEY (sollkonto_id) REFERENCES konto (id);
ALTER TABLE buchungstemplate ADD CONSTRAINT fk_buchungt_hk FOREIGN KEY (habenkonto_id) REFERENCES konto (id);
ALTER TABLE buchungstemplate ADD CONSTRAINT fk_buchungt_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id);
ALTER TABLE buchungstemplate ADD CONSTRAINT fk_buchungt_kr FOREIGN KEY (kontenrahmen_id) REFERENCES kontenrahmen (id);

ALTER TABLE mandant ADD CONSTRAINT fk_mandant_fa FOREIGN KEY (finanzamt_id) REFERENCES finanzamt (id);

ALTER TABLE konto_ab ADD CONSTRAINT fk_kontoab_konto FOREIGN KEY (konto_id) REFERENCES konto (id);
ALTER TABLE konto_ab ADD CONSTRAINT fk_kontoab_gj FOREIGN KEY (geschaeftsjahr_id) REFERENCES geschaeftsjahr (id);

ALTER TABLE anlagevermoegen ADD CONSTRAINT fk_av_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id);
ALTER TABLE anlagevermoegen ADD CONSTRAINT fk_av_konto FOREIGN KEY (konto_id) REFERENCES konto (id);
ALTER TABLE anlagevermoegen ADD CONSTRAINT fk_av_abschreibung FOREIGN KEY (k_abschreibung_id) REFERENCES konto (id);
ALTER TABLE anlagevermoegen ADD CONSTRAINT fk_av_buchung FOREIGN KEY (buchung_id) REFERENCES buchung (id);

ALTER TABLE geschaeftsjahr ADD CONSTRAINT fk_gj_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id);
ALTER TABLE geschaeftsjahr ADD CONSTRAINT fk_gj_kr FOREIGN KEY (kontenrahmen_id) REFERENCES kontenrahmen (id);
ALTER TABLE geschaeftsjahr ADD CONSTRAINT fk_gj_self FOREIGN KEY (vorjahr_id) REFERENCES geschaeftsjahr (id);

ALTER TABLE abschreibung ADD CONSTRAINT fk_abschreibung_av FOREIGN KEY (av_id) REFERENCES anlagevermoegen (id);
ALTER TABLE abschreibung ADD CONSTRAINT fk_abschreibung_buchung FOREIGN KEY (buchung_id) REFERENCES buchung (id);
