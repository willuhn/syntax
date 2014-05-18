-- ----------------------------------------------------------------------
-- Erweitert die Tabelle "buchung"
-- ----------------------------------------------------------------------

ALTER TABLE buchung add kommentar varchar(1000) null;

COMMIT;
