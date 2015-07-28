-- ----------------------------------------------------------------------
-- Erzeugt die Tabelle "version"
-- ----------------------------------------------------------------------

CREATE TABLE version (
  id int(10) AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  version int(5) NOT NULL,
  UNIQUE (id),
  PRIMARY KEY (id)
) ENGINE = InnoDB;

COMMIT;

INSERT INTO version (name,version) values ('db',0);

COMMIT;

-- ----------------------------------------------------------------------
-- $Log: mysql-update0001.sql,v $
-- Revision 1.2  2010/06/04 13:34:45  willuhn
-- @B Da fehlten ein paar Commits
--
-- Revision 1.1  2010/06/02 15:47:42  willuhn
-- @N Separierte SQL-Scripts fuer McKoi und MySQL - dann brauchen wir nicht dauernd eine extra Update-Klasse sondern koennen Plain-SQL-Scripts nehmen
--
-- ----------------------------------------------------------------------