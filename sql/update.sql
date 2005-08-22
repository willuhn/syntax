ALTER CREATE TABLE konto_ab (
  id NUMERIC default UNIQUEKEY('konto_ab'),
  konto_id int(2) NOT NULL,
  mandant_id int(2) NOT NULL,
  betrag double NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

ALTER CREATE TABLE buchung (
  id NUMERIC default UNIQUEKEY('buchung'),
  datum date NOT NULL,
  sollkonto_id int(10) NOT NULL,
  habenkonto_id int(10) NOT NULL,
  buchungstext varchar(255) NOT NULL,
  belegnummer int(4) NOT NULL,
  betrag double NOT NULL,
  steuer double,
  mandant_id int(10) NOT NULL,
  buchung_id int(10),
  UNIQUE (id),
  PRIMARY KEY (id)
);

ALTER TABLE buchung ADD CONSTRAINT fk_sollkonto FOREIGN KEY (sollkonto_id) REFERENCES konto (id) DEFERRABLE;
ALTER TABLE buchung ADD CONSTRAINT fk_habenkonto FOREIGN KEY (habenkonto_id) REFERENCES konto (id) DEFERRABLE;

ALTER TABLE konto_ab ADD CONSTRAINT fk_konto2 FOREIGN KEY (konto_id) REFERENCES konto (id) DEFERRABLE;
ALTER TABLE konto_ab ADD CONSTRAINT fk_mandant2 FOREIGN KEY (mandant_id) REFERENCES mandant (id) DEFERRABLE;
