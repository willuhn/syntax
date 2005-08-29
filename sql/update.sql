ALTER CREATE TABLE anlagevermoegen (
  id NUMERIC default UNIQUEKEY('anlagevermoegen'),
  mandant_id int(10) NOT NULL,
  name varchar(255) NOT NULL,
  anschaffungskosten double NOT NULL,
  anschaffungsdatum date NOT NULL,
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
  abgeschlossen int(1) NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);
