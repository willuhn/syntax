-- ----------------------------------------------------------------------
-- Erweitert die Tabelle "buchungstemplate" um die Spalte "hb_umsatztyp_id" aus Hibiscus
-- ----------------------------------------------------------------------

ALTER TABLE buchungstemplate add hb_umsatztyp_id varchar(10);

COMMIT;

-- ----------------------------------------------------------------------
-- $Log: mysql-update0003.sql,v $
-- Revision 1.2  2010/06/04 13:34:45  willuhn
-- @B Da fehlten ein paar Commits
--
-- Revision 1.1  2010/06/03 14:26:16  willuhn
-- @N Extension zum Zuordnen von Hibiscus-Kategorien zu SynTAX-Buchungsvorlagen
-- @C Code-Cleanup
--
-- ----------------------------------------------------------------------