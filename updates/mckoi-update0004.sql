-- ----------------------------------------------------------------------
-- Korrigiert die Bezeichnung des Steuerkontos (16%) in SKR03 und die Zuordnung zum Steuersatz
-- ----------------------------------------------------------------------

update konto set name='Abziehbare Vorsteuer 19%' where id=135 and name='Abziehbare Vorsteuer 16%';
update steuer set steuerkonto_id=1 where id=1 and steuerkonto_id=135 and satz='16';
update steuer set steuerkonto_id=135 where id=8 and steuerkonto_id=1 and satz='19';

COMMIT;

-- ----------------------------------------------------------------------
-- $Log: mckoi-update0004.sql,v $
-- Revision 1.2  2010/06/04 13:34:45  willuhn
-- @B Da fehlten ein paar Commits
--
-- Revision 1.1  2010/06/04 09:26:28  willuhn
-- @N Korrektur der 16%-Steuerkonten in SKR03 jetzt auch als Update
--
-- ----------------------------------------------------------------------