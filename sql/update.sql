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
