-- ----------------------------------------------------------------------
-- Erzeugt die Tabelle "property"
-- ----------------------------------------------------------------------

CREATE TABLE property (
  id NUMERIC default UNIQUEKEY('property'),
  name varchar(1000) NOT NULL,
  content varchar(1000) NULL,
  UNIQUE (id),
  UNIQUE (name),
  PRIMARY KEY (id)
);

-- ----------------------------------------------------------------------
-- $Log: mckoi-update0002.sql,v $
-- Revision 1.1  2010/06/02 15:47:42  willuhn
-- @N Separierte SQL-Scripts fuer McKoi und MySQL - dann brauchen wir nicht dauernd eine extra Update-Klasse sondern koennen Plain-SQL-Scripts nehmen
--
-- ----------------------------------------------------------------------