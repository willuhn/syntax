-- Flag, mit dem eine Buchung als "geprueft" markiert werden kann
alter table buchung add geprueft int(1) NULL;

-- optionale Buchung, ueber die ein Anlagegut im Bestand gelandet ist.
alter table anlagevermoegen add buchung_id int(10) NULL;
CREATE INDEX idx_av_buchung ON anlagevermoegen(buchung_id);
ALTER TABLE anlagevermoegen ADD CONSTRAINT fk_av_buchung FOREIGN KEY (buchung_id) REFERENCES buchung (id);
