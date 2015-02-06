-- ----------------------------------------------------------------------
-- Erweitert die Spalte "kontonummer"
-- ----------------------------------------------------------------------

ALTER TABLE konto change kontonummer kontonummer varchar(6) NOT NULL;

COMMIT;
