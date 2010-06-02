-- ----------------------------------------------------------------------
-- Erzeugt die Tabelle "version"
-- ----------------------------------------------------------------------

CREATE TABLE property (
  id int(10) AUTO_INCREMENT,
  name text NOT NULL,
  content text NULL,
  UNIQUE (id),
  UNIQUE KEY name (name(255)),
  PRIMARY KEY (id)
) TYPE = InnoDB;

-- ----------------------------------------------------------------------
-- $Log: mysql-update0002.sql,v $
-- Revision 1.1  2010/06/02 15:47:42  willuhn
-- @N Separierte SQL-Scripts fuer McKoi und MySQL - dann brauchen wir nicht dauernd eine extra Update-Klasse sondern koennen Plain-SQL-Scripts nehmen
--
-- ----------------------------------------------------------------------