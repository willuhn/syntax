-- ----------------------------------------------------------------------
-- Erweitert die Tabelle "anlagevermoegen"
-- ----------------------------------------------------------------------

ALTER TABLE anlagevermoegen add status int(2) null;

COMMIT;

-- ----------------------------------------------------------------------
-- $Log: mckoi-update0006.sql,v $
-- Revision 1.1  2010/09/20 10:27:36  willuhn
-- @N Neuer Status fuer Anlagevermoegen - damit kann ein Anlagegut auch dann noch in der Auswertung erscheinen, wenn es zwar abgeschrieben ist aber sich noch im Bestand befindet. Siehe http://www.onlinebanking-forum.de/phpBB2/viewtopic.php?p=69910#69910
--
-- ----------------------------------------------------------------------