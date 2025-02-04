ALTER TABLE buchung add split_id INT(10) NULL;
ALTER TABLE buchung add CONSTRAINT fk_buchung_buchung FOREIGN KEY (split_id) REFERENCES buchung (id);

COMMIT ;
