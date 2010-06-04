-- ----------------------------------------------------------------------
-- Erweitert die Tabelle "steuer"
-- ----------------------------------------------------------------------

ALTER TABLE steuer add ust_nr_steuer varchar(10);
ALTER TABLE steuer add ust_nr_bemessung varchar(10);

COMMIT;

-- SKR03
update steuer set ust_nr_steuer='66' where id = 1;
update steuer set ust_nr_steuer='66' where id = 2;
update steuer set ust_nr_steuer='36',ust_nr_bemessung='35' where id = 3;
update steuer set ust_nr_steuer='36',ust_nr_bemessung='35' where id = 4;
update steuer set ust_nr_bemessung='86' where id = 5;
update steuer set ust_nr_bemessung='48' where id = 7;
update steuer set ust_nr_steuer='66' where id = 8;
update steuer set ust_nr_bemessung='81' where id = 9;

COMMIT;

-- SKR04
update steuer set ust_nr_steuer='66' where id = 1001;
update steuer set ust_nr_steuer='66' where id = 1002;
update steuer set ust_nr_steuer='36',ust_nr_bemessung='35' where id = 1003;
update steuer set ust_nr_steuer='36',ust_nr_bemessung='35' where id = 1004;
update steuer set ust_nr_bemessung='86' where id = 1005;
update steuer set ust_nr_bemessung='48' where id = 1007;
update steuer set ust_nr_steuer='66' where id = 1008;
update steuer set ust_nr_bemessung='81' where id = 1009;

COMMIT;

-- ----------------------------------------------------------------------
-- $Log: mysql-update0005.sql,v $
-- Revision 1.2  2010/06/04 13:49:48  willuhn
-- @N Kennzeichen fuer Steuer und Bemessungsgrundlage fuer UST-Voranmeldung
--
-- Revision 1.1  2010/06/04 13:34:45  willuhn
-- @B Da fehlten ein paar Commits
--
-- ----------------------------------------------------------------------