CREATE TABLE buchung (
  id NUMERIC default UNIQUEKEY('buchung'),
  datum date NOT NULL,
  konto_id int(10) NOT NULL,
  geldkonto_id int(10) NOT NULL,
  text varchar(255) NOT NULL,
  belegnummer int(4) NOT NULL,
  betrag double NOT NULL,
  steuer double,
  mandant_id int(10) NOT NULL,
  buchung_id int(10),
  PRIMARY KEY  (id),
  KEY datum (datum)
);

CREATE TABLE finanzamt (
  id NUMERIC default UNIQUEKEY('finanzamt'),
  name varchar(255) NOT NULL,
  strasse varchar(255) NOT NULL,
  postfach varchar(50) NOT NULL,
  plz varchar(7) NOT NULL,
  ort varchar(255) NOT NULL,
  PRIMARY KEY  (id),
  KEY name (name)
);

CREATE TABLE kontenrahmen (
  id NUMERIC default UNIQUEKEY('kontenrahmen'),
  name varchar(100) NOT NULL,
  PRIMARY KEY  (id),
  KEY name (name)
);

CREATE TABLE konto (
  id NUMERIC default UNIQUEKEY('konto'),
  kontoart int(1) NOT NULL,
  kontonummer varchar(4) NOT NULL,
  name varchar(255) NOT NULL,
  kontenrahmen_id int(2) NOT NULL,
  steuer_id int(2),
  PRIMARY KEY  (id)
);

CREATE TABLE kontoart (
  id NUMERIC default UNIQUEKEY('kontoart'),
  name varchar(255) NOT NULL,
  PRIMARY KEY  (id)
);

CREATE TABLE mandant (
  id NUMERIC default UNIQUEKEY('mandant'),
  name1 varchar(255) NOT NULL,
  name2 varchar(255) NOT NULL,
  firma varchar(255) NOT NULL,
  strasse varchar(255) NOT NULL,
  plz varchar(7) NOT NULL,
  ort varchar(255) NOT NULL,
  steuernummer varchar(100) NOT NULL,
  kontenrahmen_id int(2) NOT NULL,
  finanzamt_id int(2) NOT NULL,
  geschaeftsjahr int(4) NOT NULL,
  PRIMARY KEY  (id),
  KEY firma (firma)
);

CREATE TABLE steuer (
  id NUMERIC default UNIQUEKEY('steuer'),
  name varchar(255) NOT NULL,
  satz double NOT NULL,
  steuerkonto_id int(10) NOT NULL,
  PRIMARY KEY (id)
);
