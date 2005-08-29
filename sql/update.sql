ALTER CREATE TABLE anlagevermoegen (
  id NUMERIC default UNIQUEKEY('anlagevermoegen'),
  mandant_id int(10) NOT NULL,
  name varchar(255) NOT NULL,
  anschaffungskosten double NOT NULL,
  anschaffungsdatum date NOT NULL,
  k_abschreibung_id int(10) NOT NULL,
  konto_id int(10) NULL,
  buchung_id int(10) NULL,
  laufzeit int(2) NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

ALTER CREATE TABLE geschaeftsjahr (
  id NUMERIC default UNIQUEKEY('geschaeftsjahr'),
  mandant_id int(10) NOT NULL,
  beginn date NOT NULL,
  ende date NOT NULL,
  kontenrahmen_id int(10) NOT NULL,
  closed int(1) NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

ALTER CREATE TABLE kontenrahmen (
  id NUMERIC default UNIQUEKEY('kontenrahmen'),
  name varchar(100) NOT NULL,
  k_abschreibung_id int(10) NOT NULL,
  UNIQUE (id),
  UNIQUE (name),
  PRIMARY KEY (id)
);

ALTER CREATE TABLE abschreibung (
  id NUMERIC default UNIQUEKEY('abschreibung'),
  av_id int(10) NOT NULL,
  buchung_id int(10) NOT NULL,
  geschaeftsjahr_id int(10) NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);
