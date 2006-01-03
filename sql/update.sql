-- neues Feld restwert
ALTER CREATE TABLE anlagevermoegen (
  id NUMERIC default UNIQUEKEY('anlagevermoegen'),
  mandant_id int(10) NOT NULL,
  name varchar(255) NOT NULL,
  anschaffungskosten double NOT NULL,
  restwert double NULL,
  anschaffungsdatum date NOT NULL,
  k_abschreibung_id int(10) NOT NULL,
  konto_id int(10) NULL,
  nutzungsdauer int(2) NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

-- beginn und ende koennen null sein
ALTER CREATE TABLE geschaeftsjahr (
  id NUMERIC default UNIQUEKEY('geschaeftsjahr'),
  vorjahr_id int(10) NULL,
  mandant_id int(10) NOT NULL,
  beginn date NULL,
  ende date NULL,
  kontenrahmen_id int(10) NOT NULL,
  closed int(1) NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

-- Neue Felder mandant_id und kontenrahmen_id
ALTER CREATE TABLE buchungstemplate (
  id NUMERIC default UNIQUEKEY('buchungstemplate'),
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
);
ALTER TABLE buchungstemplate ADD CONSTRAINT fk_buchungt_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id) DEFERRABLE;
ALTER TABLE buchungstemplate ADD CONSTRAINT fk_buchungt_kr FOREIGN KEY (kontenrahmen_id) REFERENCES kontenrahmen (id) DEFERRABLE;
