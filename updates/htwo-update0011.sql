CREATE TABLE kontozuordnung (
  id IDENTITY(1),
  name varchar(255) NOT NULL,
  mandant_id int(10) NOT NULL,
  konto_id int(10) NOT NULL,
  hb_konto_id int(10) NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
);

ALTER TABLE kontozuordnung ADD CONSTRAINT FK_kontozuordnung_konto FOREIGN KEY (konto_id) REFERENCES konto (id) DEFERRABLE;
ALTER TABLE kontozuordnung ADD CONSTRAINT FK_kontozuordnung_mandant FOREIGN KEY (mandant_id) REFERENCES mandant (id) DEFERRABLE;

COMMIT;
