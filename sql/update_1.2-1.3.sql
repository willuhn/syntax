alter table buchung add hb_umsatz_id char(7) NULL;
CREATE INDEX idx_buchung_hb_umsatz_id ON buchung(hb_umsatz_id);

