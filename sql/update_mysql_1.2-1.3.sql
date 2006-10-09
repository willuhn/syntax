alter table buchung add hb_umsatz_id char(7);
CREATE INDEX idx_buchung_hb_umsatz_id ON buchung(hb_umsatz_id);
