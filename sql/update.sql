ALTER CREATE TABLE konto_ab (
  id NUMERIC default UNIQUEKEY('konto_ab'),
  konto_id int(2) NOT NULL,
  mandant_id int(2) NOT NULL,
  betrag double NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

ALTER TABLE konto_ab ADD CONSTRAINT fk_konto2 FOREIGN KEY (konto_id) REFERENCES konto (id) DEFERRABLE;
ALTER TABLE konto_ab ADD CONSTRAINT fk_mandant2 FOREIGN KEY (mandant_id) REFERENCES mandant (id) DEFERRABLE;
