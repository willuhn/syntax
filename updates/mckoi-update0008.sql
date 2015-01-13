-- ----------------------------------------------------------------------
-- Erweitert die Tabelle "mandant"
-- ----------------------------------------------------------------------

ALTER TABLE mandant add kuerzel varchar(10) NULL;

COMMIT;
