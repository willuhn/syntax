-- ----------------------------------------------------------------------
-- Erweitert die Tabelle "steuer" um die Spalte "ust_kennzeichen" und fuegt die neuen Werte hinzu
-- ----------------------------------------------------------------------

ALTER TABLE steuer add ust_kennzeichen varchar(10);

COMMIT;

-- SKR03
update steuer set us_kennzeichen='66' where id = 1;
update steuer set us_kennzeichen='66' where id = 2;
update steuer set us_kennzeichen='36' where id = 3;
update steuer set us_kennzeichen='36' where id = 4;
update steuer set us_kennzeichen='86' where id = 5;
update steuer set us_kennzeichen=NULL where id = 6;
update steuer set us_kennzeichen='48' where id = 7;
update steuer set us_kennzeichen='66' where id = 8;
update steuer set us_kennzeichen='81' where id = 9;

COMMIT;

-- SKR04
update steuer set us_kennzeichen='66' where id = 1001;
update steuer set us_kennzeichen='66' where id = 1002;
update steuer set us_kennzeichen='36' where id = 1003;
update steuer set us_kennzeichen='36' where id = 1004;
update steuer set us_kennzeichen='86' where id = 1005;
update steuer set us_kennzeichen=NULL where id = 1006;
update steuer set us_kennzeichen='48' where id = 1007;
update steuer set us_kennzeichen='66' where id = 1008;
update steuer set us_kennzeichen='81' where id = 1009;

COMMIT;

-- ----------------------------------------------------------------------
-- $Log: mckoi-update0005.sql,v $
-- Revision 1.1  2010/06/04 13:34:45  willuhn
-- @B Da fehlten ein paar Commits
--
-- ----------------------------------------------------------------------